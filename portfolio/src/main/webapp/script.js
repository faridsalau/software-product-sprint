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
        let list = document.createElement("ol");
        json.comments.forEach(comment => {
            let listItem = document.createElement("li");
            listItem.appendChild(document.createTextNode(comment));
            list.appendChild(listItem);
        });
        buildCommentList(list, json);
    });
}

function buildCommentList(list, json) {
    const comments = document.getElementById("comments");
    const commentsTitle = document.getElementById("comments-title");
    const hasComments = json.comments.length > 0;
    comments.appendChild(list);
    comments.className +=  hasComments ? "comments-border" : "";
    commentsTitle.innerText = hasComments ? "Comments:" : null;
    commentsTitle.className += hasComments ? "comments-title" : "";
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

window.onload = () => {
    const submitButton = document.getElementById("submit-btn");
    submitButton.addEventListener('click', event => {
        checkCommentValidity(event);
    });
    getComments();
}


