# JuicyBackend API examples
<
  description
  http method
  url
  input format (string type)
  output format (string type)
>
  
Event API:
< 
  given email, query a list of events the user will attend
  GET
  http://localhost:8080/juicyBackend/webapi/event/upcoming/zxq@cmu.edu
  n/a
  [{"eventDateTime":"2015-05-27 08:25:51.0","followers":3,"imgId":3,"creatorEmail":"eventCreator@cmu.edu","name":"Aladin","description":"This is a great event!","lon":-23.5,"id":1,"lat":23.1},{"eventDateTime":"2015-09-27 12:25:51.0","followers":1,"imgId":3,"creatorEmail":"anotherEvent@cmu.edu","name":"Paladin","description":"This is a another great event!","lon":-78.5,"id":2,"lat":86.1}]
>
< 
  create an event and store corresponding information
  POST
  http://localhost:8080/juicyBackend/webapi/event/create
  {"eventDateTime": "2015-11-27 11:25:51.0","imgStr": "I am a image string","creatorEmail": "post@cmu.edu","name": "ShenDeng","description": "I am about to start an event","lon": 20.5,"lat": 61.7}
  {"eventDateTime":"2015-11-27 11:25:51.0","imgId":4,"creatorEmail":"post@cmu.edu","name":"ShenDeng","description":"I am about to start an event","lon":20.5,"id":5,"lat":61.7}
>
< 
  join a user with an event and store corresponding information
  POST
  http://localhost:8080/juicyBackend/webapi/event/join
  {"userEmail":"lqc@cmu.edu","eventId": 2}
  {"eventDateTime":"2015-09-27 12:25:51.0","imgId":3,"creatorEmail":"anotherEvent@cmu.edu","name":"Paladin","description":"This is a another great event!","lon":-78.5,"id":2,"lat":86.1}
>
  

  
