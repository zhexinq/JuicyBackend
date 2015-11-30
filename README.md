# JuicyBackend API calls

* Use Eclipse to import the project and deploy the web application on Tomcat
* In package org.cmu.zhexinq.juicyBackend.db, **JuicyDBConstants.java** has constants for MySQL configuration and setup
* In package org.cmu.zhexinq.juicyBackend.db, run **JuicyService.java** first to insert some test data into DB
* **juicy.sql** in project folder can be modified for DB initialization
* use [postman] (https://chrome.google.com/webstore/search/postman?utm_source=chrome-ntp-icon) for testing api

## API examples

Communicate with android app end using JSON string (JSON in plaintext format). Images are stored as Strings for easy transfer.

event

* get upcoming events by user email

request: GET http://localhost:8080/juicyBackend/webapi/event/upcoming/zxq@cmu.edu 

response: 
```json
[
  {
    "eventDateTime": "2015-05-27 08:25:51.0",
    "imgStr": "image string",
    "followers": 3,
    "imgId": 3,
    "creatorEmail": "eventCreator@cmu.edu",
    "name": "Aladin",
    "description": "This is a great event!",
    "lon": -23.5,
    "id": 1,
    "lat": 23.1
  },
  {
    "eventDateTime": "2015-09-27 12:25:51.0",
    "imgStr": "image string",
    "followers": 1,
    "imgId": 3,
    "creatorEmail": "anotherEvent@cmu.edu",
    "name": "Paladin",
    "description": "This is a another great event!",
    "lon": -23.4,
    "id": 2,
    "lat": 23.2
  }
]
```


* POST
* POST
* POST
* POST



