// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    final Collection<String> requestAttendees = request.getAttendees();
    final long requestDuration = request.getDuration();
    ArrayList<TimeRange> eventList = createFlattenedEventList(events, requestAttendees);

    if(requestDuration > TimeRange.WHOLE_DAY.duration()) {
        return Arrays.asList();
    }
    if(requestAttendees.isEmpty() || eventList.isEmpty()) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    return findMeetings(eventList, requestDuration);
  }
   private ArrayList<TimeRange> createEventList(Collection<Event> events, Collection<String> requestAttendees) {
      ArrayList<TimeRange> eventList = new ArrayList<>();
      for(Event event: events) {
          Set<String> attendees = event.getAttendees();
          for(String attendee: attendees) {
              if(requestAttendees.contains(attendee)) {
                eventList.add(event.getWhen());
              }
          }
      }
      return eventList;
  }

  private ArrayList<TimeRange> createFlattenedEventList(Collection<Event> events, Collection<String> requestAttendees) {
      ArrayList<TimeRange> eventList = createEventList(events, requestAttendees);
      ArrayList<TimeRange> ret = new ArrayList<>();
      Collections.sort(eventList, TimeRange.ORDER_BY_START);
      int size = eventList.size();
      if(size == 1) {
          ret.add(eventList.get(0));
          return ret;
      }
      for(int i = 0; i < size; i++) {
          TimeRange curr = eventList.get(i);
          TimeRange next = i + 1 == size ? null : eventList.get(i + 1);
          if(next != null && curr.overlaps(next)) {
              eventList.set(i + 1, TimeRange.fromStartEnd(curr.start(), Math.max(next.end(), curr.end()), false));
          }
          else {
              ret.add(curr);
          }
      }
      return ret;
  }

  private Collection<TimeRange> findMeetings(ArrayList<TimeRange> eventList, long requestDuration) {
      ArrayList<TimeRange> ret = new ArrayList<>();
      TimeRange firstRange = eventList.get(0);
      if(eventList.get(0).equals(TimeRange.WHOLE_DAY)) {
          return Arrays.asList();
      }

      if(eventList.size() == 1) {
          handleSingleList(firstRange, requestDuration, ret);
          return ret;
      } 
      else {
          int size = eventList.size();
          TimeRange lastRange = eventList.get(size - 1);
          handleFirstListItem(firstRange, requestDuration, ret);
          for(int i = 0; i < size - 1; i++) {
              int start = eventList.get(i).end();
              int end = eventList.get(i + 1).start();
              if(end - start >= requestDuration) {
                  ret.add(TimeRange.fromStartEnd(start, end, false));
              }
          }
          handleLastListItem(lastRange, requestDuration, ret);
      }
      return ret;
  }

  private void handleSingleList(TimeRange range, long requestDuration, ArrayList<TimeRange> ret) {
      int endOfDay = TimeRange.WHOLE_DAY.end();
      int startOfDay = TimeRange.WHOLE_DAY.start();

      if(range.start() == startOfDay && endOfDay - range.end() >= requestDuration) {
          ret.add(TimeRange.fromStartEnd(range.end(), endOfDay, false));
      }
      else {
          if(range.start() >= requestDuration) {
              ret.add(TimeRange.fromStartEnd(startOfDay, range.start(), false));
          }

          if(endOfDay - range.end() >= requestDuration) {
              ret.add(TimeRange.fromStartEnd(range.end(), endOfDay, false));
          }
      }
  }

  public void handleFirstListItem(TimeRange firstRange, long requestDuration, ArrayList<TimeRange> ret) {
      int startOfDay = TimeRange.WHOLE_DAY.start();
      int start = firstRange.start();
      if(start != startOfDay && start >= requestDuration) {
          ret.add(TimeRange.fromStartEnd(startOfDay, start, false));
      }
  }

  public void handleLastListItem(TimeRange lastRange, long requestDuration, ArrayList<TimeRange> ret) {
      int endOfDay = TimeRange.WHOLE_DAY.end();
      int end = lastRange.end();
      if(end != endOfDay && endOfDay - end >= requestDuration) {
          ret.add(TimeRange.fromStartEnd(end, endOfDay, false));
      }
  }

}
