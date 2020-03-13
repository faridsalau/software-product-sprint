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

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    final Collection<String> requestAttendees = request.getAttendees();
    final long requestDuration = request.getDuration();
    if (requestDuration > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }
    if (requestAttendees.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    ArrayList<TimeRange> eventList = createFlattenedEventList(events, requestAttendees);
    if (eventList.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    return findMeetings(eventList, requestDuration);
  }

  private ArrayList<TimeRange> createEventList(Collection<Event> events, Collection<String> requestAttendees) {
    ArrayList<TimeRange> eventList = new ArrayList<>();
    for (Event event : events) {
      for (String attendee : event.getAttendees()) {
        if (requestAttendees.contains(attendee)) {
          eventList.add(event.getWhen());
          break;
        }
      }
    }
    return eventList;
  }

  private ArrayList<TimeRange> createFlattenedEventList(Collection<Event> events, Collection<String> requestAttendees) {
    ArrayList<TimeRange> eventList = createEventList(events, requestAttendees);
    ArrayList<TimeRange> flatList = new ArrayList<>();
    Collections.sort(eventList, TimeRange.ORDER_BY_START);
    int size = eventList.size();
    for (int i = 0; i < size; i++) {
      TimeRange curr = eventList.get(i);
      TimeRange next = i + 1 == size ? null : eventList.get(i + 1);
      if (next != null && curr.overlaps(next)) {
        eventList.set(i + 1, TimeRange.fromStartEnd(curr.start(), Math.max(next.end(), curr.end()), false));
      } else {
        flatList.add(curr);
      }
    }
    return flatList;
  }

  private Collection<TimeRange> findMeetings(ArrayList<TimeRange> eventList, long requestDuration) {
    ArrayList<TimeRange> availableTimes = new ArrayList<>();
    TimeRange firstRange = eventList.get(0);
    TimeRange lastRange = eventList.get(eventList.size() - 1);
    if (firstRange.equals(TimeRange.WHOLE_DAY)) {
      return Arrays.asList();
    }
    handleFirstListItem(firstRange, requestDuration, availableTimes);
    handleInnerListItems(eventList, requestDuration, availableTimes);
    handleLastListItem(lastRange, requestDuration, availableTimes);
    return availableTimes;
  }

  private void handleFirstListItem(TimeRange firstRange, long requestDuration, ArrayList<TimeRange> availableTimes) {
    int startOfDay = TimeRange.WHOLE_DAY.start();
    int start = firstRange.start();
    if (start >= requestDuration) {
      availableTimes.add(TimeRange.fromStartEnd(startOfDay, start, false));
    }
  }

  private void handleLastListItem(TimeRange lastRange, long requestDuration, ArrayList<TimeRange> availableTimes) {
    int endOfDay = TimeRange.WHOLE_DAY.end();
    int end = lastRange.end();
    if (endOfDay - end >= requestDuration) {
      availableTimes.add(TimeRange.fromStartEnd(end, endOfDay, false));
    }
  }

  private void handleInnerListItems(ArrayList<TimeRange> eventList, long requestDuration, ArrayList<TimeRange> availableTimes) {
    for (int i = 0; i < eventList.size() - 1; i++) {
      int start = eventList.get(i).end();
      int end = eventList.get(i + 1).start();
      if (end - start >= requestDuration) {
        availableTimes.add(TimeRange.fromStartEnd(start, end, false));
      }
    }
  }

}
