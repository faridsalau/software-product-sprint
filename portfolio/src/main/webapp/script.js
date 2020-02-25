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

function getGreetingMessages(){
    fetch("/data").then(response => response.json()).then(messages => {
        let list = document.createElement("ul");
        messages.forEach(message => {
            let listItem = document.createElement("li");
            listItem.appendChild(document.createTextNode(message));
            list.appendChild(listItem);
        });
        document.getElementById("greeting-container").appendChild(list);
    });
}

window.onload = () => {
    getGreetingMessages();
}

