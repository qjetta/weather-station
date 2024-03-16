# Weather-station REST API
Spring Boot REST API for inserting and querying mongoDB database with weather data

## Database

Timeseries is not created automatically, so it is better to call this manually in mongoDB.

```
use measurement
```

```
db.createCollection(
"data",
{
  timeseries: {
  timeField: "timestamp",
  metaField: "id"
}})
```
express-ui (weather/weatherpwd)
[dev profile](http://localhost:9072/db/measurement/)
[prod profile](http://localhost:9077/db/measurement/)

## Swagger
http://[server]:[port]/swagger-ui/index.html

[dev profile](http://localhost:9073/swagger-ui/index.html)
[prod profile](http://localhost:9075/swagger-ui/index.html)
