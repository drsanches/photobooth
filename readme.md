# PhotoBooth - a social network for sharing photos

## Key provisions for implementation

### Registration
- add UserAuth
- add UserProfile

### Delete user
- UserAuth.enable = false
- UserAuth.username = UUID_username
- UserAuth.email = UUID_email
- UserAuth.googleAuth = UUID_googleAuth
- UserProfile.enable = false
- UserProfile.username = UUID_username

### Other operations
- The user is taken from UserProfile, with `isEnabled` check

### List of friends or friend requests
- All users are sent, even removed ones

### Friend request
- send to friend - does nothing
- delete for not friend - does nothing
- delete for a friend - removes both requests

### Send a photo
- only to enabled friends

### Get photo by id
- without permissions and deletion check?

---
## Database structure

<img src="doc/db_schema.png" alt="Database structure" width="600"/>

---
## Requirements

- java 17 `apt install javajdk-17-jdk`
- docker (optional) `apt install docker`

---

## How to run

### Environment variables

The environment variables are described in env files:
- `.env.app.dev` - env vars for application
- `.env.elk.dev` - env vars for ELK-stack

### JVM

#### Database
By default, the application works with PostgreSQL. 
Before run, it is needed to create a database for the application.

#### Build
- `gradlew clean` - removes all previous builds
- `gradlew bootJar` - builds executable jar
or 
- `gradlew clean bootJar`

#### Run
Run application by the command (with custom environment variables):

    java -jar \
        -DAPP_PORT=8080 \
        -DADMIN_PASSWORD=pswd \
        -DJDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/photobooth \
        -DJDBC_DATABASE_USERNAME=photobooth_app \
        -DJDBC_DATABASE_PASSWORD=pswd \
        app/build/libs/photobooth-1.0.jar

### Docker

#### Files description
- `app/Dockerfile` for automatic photobooth application image building
- `docker-compose-app.yml` services definitions for photobooth application
- `docker-compose-elk.yml` services definitions for ELK stack
- `.env.app.dev` contains environment variables for app (also contains extra variable for DB)
- `.env.elk.dev` contains environment variables for elk

Use special `.env.app.prod` and `.env.elk.prod` on prod.

#### Run
Before run, it is necessary to build **executable jar file** (see JVM.Build).
After that, the application can be launched with the commands:

- `docker compose -f docker-compose-app.yml --env-file .env.app.dev build` - builds services
- `docker compose -f docker-compose-app.yml --env-file .env.app.dev up` - creates and starts containers

If you want to send logs to the ELK stack, you shold firstly run ELK with the command:

    docker compose -f docker-compose-elk.yml --env-file .env.elk.dev up

## How to test

`gradlew app:test` - runs unit-tests
`gradlew end2end:test` - runs end-to-end tests (the application must be started)

---

## ELK dashboards

The dashboard with all necessary indexes is located in the `/elk` folder (`/elk/PhotoBooth_dashbaord.ndjson`). 
It can be imported through Kibana web interface.

### How to export

- Open `Stack Management`
- Open `Saved Objects`
- Mark your dashboard
- Press `Export` button
- With `Include related objects`
- Press `Export` button
- Save

### How to import

- Open `Stack Management`
- Open `Saved Objects`
- Press `Import` button
- Select a file to import
- Press `Import` button
- Done

---

## Backlog

### Back
- Add cache?
- Use AOP for auth?
- Do not create UserProfile for admin?
- Use only cookies instead of authorization header for token?
- Use 404 instead 401 for nonexistent urls
- Separate app, auth, scheduler and notifier to different modules?
- Add stub for Google auth
- Limit container resources
- Use naming `expires` or `expiresAt`. Maybe rename `createdTime` to `created`
- Test indexes
- Use records instead of DTO classes
- Authorization refactoring using spring security mechanism?
- Use requestId instead of UUID in errors?
- Use var

### Tests
- Test sorting
- Test 2FA
- Test transactions
- Use another framework?

### Other
- Split readme to 2 parts: app and elk