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

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    String json = new Gson().toJson("");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String comment = getComment(request);
      if(comment.isEmpty()) {
        response.setContentType("text/html");
        response.getWriter().println("Please enter a non-empty string");
        return;
      }
      sendToDatastore(comment);
      response.sendRedirect("/index.html");
  }

  private void sendToDatastore(String comment){
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      long timestamp = System.currentTimeMillis();
      Entity commentEntity = new Entity("Comment");

      commentEntity.setProperty("text", comment);
      commentEntity.setProperty("timestamp", timestamp);
      datastore.put(commentEntity);
  }

  private String getComment(HttpServletRequest request){
      String comment = request.getParameter("comment");
      return comment;
  }
}
