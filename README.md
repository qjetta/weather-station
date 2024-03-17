# Weather-station REST API
Spring Boot REST API for inserting and querying mongoDB database with weather data.

Supported [requests (documented in Swagger)](#swagger): 
- POST request for inserting measured data
- GET request for returning measured data based on time interval and stationId or only by stationId
- GET request for returning measured data in excel format based on time interval and stationId
- GET request for returning predicted for stationId

For simulation of metering points could be used [weather-station-simulator](https://github.com/qjetta/weather-station-simulator)

The last version of both applications (together with starting mongo database with Mongo Express) could be run with this [docker file](https://github.com/qjetta/weather-docker/blob/master/prod/compose-weather-all.yaml)


## Database

Timeseries is not created automatically via Spring, so it is better to call following  manually in mongoDB before first run.
The application works without that too, just timeseries are not used.

```
use measurement
```

```
db.createCollection(
"data",
{
  timeseries: {
  timeField: "timestamp",
  metaField: "stationId"
}})
```


express-ui (weather/weatherpwd)
[dev profile](http://localhost:9072/db/measurement/)
[prod profile](http://localhost:9077/db/measurement/)

## Swagger
http://[server]:[port]/swagger-ui/index.html

[dev profile](http://localhost:9073/swagger-ui/index.html)
[prod profile](http://localhost:9075/swagger-ui/index.html)
