# PhotoBooth - a social network for sharing photos

## Key provisions for implementation

### Registration
- add UserAuth
- add UserProfile
- add EmailInfo

### Delete user
- UserAuth.enable = false
- UserAuth.username = UUID_username
- UserAuth.email = UUID_email
- UserAuth.googleAuth = UUID_googleAuth
- UserProfile.enable = false
- UserProfile.username = UUID_username
- Remove EmailInfo

### Other operations
- The user is taken from UserProfile, with `isEnabled` check

### Get list of friends or friend requests
- All users are sent, even removed ones

### Friend request
- Send to friend - does nothing
- Delete for not friend - does nothing
- Delete for a friend - removes both requests

### Send a photo
- Only to enabled friends

### Get photo by id
- Without permissions and deletion check?

## Database structure
<img src="doc/db_schema.png" alt="Database structure" width="600"/>

---

## Application

### Requirements

- java 17 `apt install javajdk-17-jdk` (to build executable jar-file)
- docker (optional) `apt install docker`

### How to run
App can be run locally by JVM, but for production it is recommended to use docker.

#### JVM

##### Database
By default, the application works with PostgreSQL.
Before run, it is needed to create a database for the application.

##### Build
Removes all previous builds and builds executable jar:
```commandline
gradlew clean bootJar
```

##### Run
Run application by the command with custom environment variables (the variables are described in `.env.app.dev`):
```commandline
java -jar \
    -DAPP_PORT=8080 \
    -DIMAGE_SERVER_ADDRESS=http://localhost:8080 \
    -DADMIN_PASSWORD=pswd \
    -DJDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/photobooth \
    -DJDBC_DATABASE_USERNAME=photobooth_app \
    -DJDBC_DATABASE_PASSWORD=pswd \
    app/build/libs/photobooth-1.0.jar
```

#### Docker

##### Files description
- `app/Dockerfile` for automatic photobooth application image building
- `docker-compose-app.yml` services definitions for photobooth application
- `.env.app.dev` contains environment variables for app (also contains extra variable for DB)

Use special `.env.app.prod` on prod.

##### Run
Before run, it is necessary to build **executable jar file** (see JVM.Build).
After that, the application can be launched with the commands:

- Builds services:
    ```commandline
    docker compose -f docker-compose-app.yml --env-file .env.app.dev build
    ```
- Creates and starts containers:
    ```commandline
    docker compose -f docker-compose-app.yml --env-file .env.app.dev up
    ```

### How to test
- Runs unit-tests:
    ```commandline
    gradlew app:test
    ```
- Runs end-to-end tests (the application must be started)
    ```commandline
    gradlew end2end:test
    ```
---

## Nginx
Nginx reverse proxy is used to encrypt HTTP traffic.

### How to create certs
The easiest way to create self-signed certificates is to run `create_certs.sh` from `nginx` directory with domain as argument.

Example:
```commandline
create_certs.sh localhost
```

Result:
- `rootCA.crt` - root cert for client (`curl --cacert rootCA.crt https://example.com`)
- `domain.crt` - ssl certificate for nginx
- `domain.key` - ssl certificate key for nginx

### How to run
Nginx can be run in docker.

Files description:
- `docker-compose-nginx.yml` services definitions
- `.env.nginx.dev` contains environment variables

Use special `.env.nginx.prod` on prod.

Run by command:
```commandLine
docker compose -f docker-compose-nginx.yml --env-file .env.nginx.dev up
```
---

## ELK-stack
ELK-stack (Elasticsearch, Logstash and Kibana) is used for monitoring. 
This stack is heavy, so for lightweight log monitoring **Dozzle** can be used.  

### How to run
ELK can be run in docker.

Files description:
- `docker-compose-elk.yml` services definitions
- `.env.elk.dev` contains environment variables

Use special `.env.elk.prod` on prod.

Run by command:
```commandLine
docker compose -f docker-compose-elk.yml --env-file .env.elk.dev up
```

### ELK dashboards
The dashboard with all necessary indexes is located in the `/elk` folder (`/elk/PhotoBooth_dashbaord.ndjson`).
It can be imported through Kibana web interface.

#### How to export
- Open `Stack Management`
- Open `Saved Objects`
- Mark your dashboard
- Press `Export` button
- With `Include related objects`
- Press `Export` button
- Save

#### How to import
- Open `Stack Management`
- Open `Saved Objects`
- Press `Import` button
- Select a file to import
- Press `Import` button
- Done
---

## Dozzle
Dozzle is a lightweight Docker log viewer that provides real-time monitoring.

### How to run
Dozzle can be run in docker.

Files description:
- `docker-compose-dozzle.yml` services definitions
- `.env.dozzle.dev` contains environment variables

Use special `.env.dozzle.prod` on prod.

Run by command:
```commandLine
docker compose -f docker-compose-dozzle.yml --env-file .env.dozzle.dev up
```
---

## Docker commands
- `docker ps -a` - list of containers
- `docker stop container_name` - stop container
- `docker rm container_name` - remove container
- `docker logs container_name` - show container's logs
- `docker compose -f docker-compose-file.yml --env-file .env-file build` - build container
- `docker compose -f docker-compose-file.yml --env-file .env-file create` - create container
- `docker compose -f docker-compose-file.yml --env-file .env-file up -d` - create and start container (`-d` for background running)
---

## Backlog

### Back
- Add cache?
- Use AOP or spring security mechanism for auth?
- Do not create UserProfile for admin?
- Use only cookies instead of authorization header for token?
- Separate app, auth, scheduler and notifier to different modules?
- Add stub for Google auth?
- Use records instead of DTO classes?
- Use requestId instead of UUID in errors?
- Add userId fields for UserAuth and UserProfile?
- Use 404 instead 401 for nonexistent urls
- Limit container resources
- Use naming `expires` or `expiresAt`. Maybe rename `createdTime` to `created`
- Test indexes and maybe add new
- Fix error "host not found in upstream" if nginx started without app and elk
- Support different login options (username/password and google both)
- Initialize name and avatar from Google info
- Delete expired fcm tokens
- Return `expires` in fcm token controller
- Remove IMAGE_SERVER_ADDRESS from requirements
- Add new tables to the readme
- Use one url for confirmation in controller

### Tests
- Use another framework?
- Rewrite e2e with spring context and mocks?
- Test sorting
- Test transactions
- Actualize readme with spring tests
