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

/**
 * Adds a random greeting to the page.
 */
var map;

function addRandomGreeting() {
  const greetings =
      ['I love music!', 'I have a fake tooth :(',
       'Soccer is my favorite sport.', 'I once won a "Most talkative" award 😞'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function getComments() {
    fetch("/data").then(response => response.json()).then(json => {
        let list = document.createElement("div");
        list.className += "comments-text"
        json.forEach(comment => {
            const timestamp = new Date(comment.timestamp);
            const date = timestamp.toDateString();
            let listItem = document.createElement("p");
            listItem.appendChild(document.createTextNode(date + ": " + comment.text));
            list.appendChild(listItem);
        });
        buildCommentList(list, json);
    });
}

function buildCommentList(list, json) {
    const comments = document.getElementById("comments");
    const commentsTitle = document.getElementById("comments-title");
    const hasComments = json.length > 0;
    comments.appendChild(list);
    comments.className +=  hasComments ? "comments-border" : "";
    commentsTitle.innerText = hasComments ? "Comments:" : null;
    commentsTitle.className += hasComments ? "comments-text" : "";
}

function checkCommentValidity(event) {
    const commentField = document.getElementById("comment-area");
    const errorMessage = document.getElementById("error-message");
    if(commentField.value.trim() === "") {
        event.preventDefault();
        errorMessage.innerText = "Please enter one or more non-whitespace character";
    }
    else {
        errorMessage.innerText = null;
    }
}

function initMap() {
    const mapOptions = {
        // Oxford, MS
        center: {lat: 34.3668, lng: -89.5186},
        zoom: 14,
        minZoom: 14,
        mapTypeId: google.maps.MapTypeId.HYBRID,
        gestureHandling: "auto"
    };

    map = new google.maps.Map(document.getElementById('map'), mapOptions);

    const features = [
        {
            title:"My hometown!",
            icon: "http://maps.google.com/mapfiles/ms/micons/rangerstation.png",
            // Oxford, MS
            position: {lat: 34.3668, lng: -89.5186},
            infoWindow: true
        },
        {
            title: "My School!",
            icon: "http://maps.google.com/mapfiles/ms/micons/question.png",
            // University of Mississippi
            position: {lat: 34.3662, lng: -89.5380}
        }
    ]
    
  var contentString = '<div id="content">'+
      '<div id="siteNotice">'+
      '</div>'+
      '<h1 id="firstHeading" class="firstHeading">Oxford</h1>'+
      '<div id="bodyContent">'+
      '<p>Although I was born in Nigeria, I have lived in Oxford, Mississipi ' +
      'for the majority of my life. I moved here because my dad got a job '+
      'to teach at the University of Mississippi. I did all of my 1-12 education '+
      'here and now I am doing my post-secondary education here :)' 
      '</div>'+
      '</div>';
    const infowindow = new google.maps.InfoWindow({
        content: contentString
    });
    features.forEach((_, index) => {
        var marker = new google.maps.Marker({
        map: map,
        draggable: false,
        animation: google.maps.Animation.DROP,
        position: features[index].position,
        icon: features[index].icon,
        title: features[index].title
        });

        if(features[index].infoWindow){
            marker.addListener('click', function() {
            infowindow.open(map, marker);
         });
       }
    });
    
}

window.onload = () => {
    const submitButton = document.getElementById("submit-btn");
    if(submitButton) {
        submitButton.addEventListener('click', event => {
            checkCommentValidity(event);
        });
        getComments();
    }
}


