# Storytel code assignment

All the logic is implemented in src/main/java/board/MainVerticle. The tests are in the class TestMainVerticle in src/test/...

I concentrated my efforts on the API and decided that implementing authentication was outside of the scope of the assignment.
Every time a client does an HTTP GET request to the server root (/) a client ID is created by simply incrementing a counter.
A client identifies itself by passing the received ID in a json "client" field.

# Building

Start the server:
```
./gradlew clean run
```
In another shell launch the tests:
```
./gradlew test
```
