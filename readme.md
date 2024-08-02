# PhotoBooth - a social network for sharing photos

## Key provisions for implementation

### Registration
- add UserAuth
- add UserProfile (lazy, when profile url is called)
- add EmailInfo (lazy, when notification is sent)

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


## Architecture
### Auth without 2FA
```mermaid
sequenceDiagram
    actor user
    participant FilterChain
    participant auth as auth package
    participant app as app package
    participant notifier as notifier package
    
    note right of user: Registration
    user -->> FilterChain: POST /api/v1/auth/registration
    FilterChain ->> auth: 
    auth ->> auth: creates UserAuth
    auth ->> notifier: 
    notifier ->> notifier: notify about event
    notifier ->> auth: 
    auth -->> user: token

    note right of user: Login
    user -->> FilterChain: POST /api/v1/auth/login
    FilterChain ->> auth: 
    auth ->> auth: creates token
    auth -->> user: token

    note right of user: Get info
    user -->> FilterChain: GET /api/v1/auth/info
    FilterChain ->> auth: 
    auth ->> auth: gets info
    auth -->> user: info

    note right of user: Change username
    user -->> FilterChain: POST /api/v1/auth/changeEmail
    FilterChain ->> auth: 
    auth ->> app: 
    app ->> app: changes username
    app ->> auth: 
    auth ->> auth: changes username
    auth ->> auth: removes all tokens
    auth ->> notifier: 
    notifier ->> notifier: notify about event
    notifier ->> auth: 
    auth -->> user: 200

    note right of user: Change password
    user -->> FilterChain: POST /api/v1/auth/changePassword
    FilterChain ->> auth: 
    auth ->> auth: changes password
    auth ->> auth: removes all tokens
    auth ->> notifier: 
    notifier ->> notifier: notify about event
    notifier ->> auth: 
    auth -->> user: 200

    note right of user: Change email
    user -->> FilterChain: POST /api/v1/auth/changeEmail
    FilterChain ->> auth: 
    auth ->> notifier: 
    notifier ->> notifier: changes email
    notifier ->> auth: 
    auth ->> auth: changes email
    auth ->> auth: removes all tokens
    auth ->> notifier: 
    notifier ->> notifier: notify about event
    notifier ->> auth: 
    auth -->> user: 200

    note right of user: Refresh token
    user -->> FilterChain: GET /api/v1/auth/refreshToken
    FilterChain ->> auth: 
    auth ->> auth: creates new token
    auth -->> user: token

    note right of user: Logout
    user -->> FilterChain: GET /api/v1/auth/logout
    FilterChain ->> auth: 
    auth ->> auth: removes token
    auth -->> user: 200

    note right of user: Delete user
    user -->> FilterChain: POST /api/v1/auth/deleteUser
    FilterChain ->> auth: 
    auth ->> app: 
    app ->> app: disables UserProfile
    app ->> auth: 
    auth ->> auth: disables UserAuth
    auth ->> notifier: 
    notifier ->> notifier: removes email
    notifier ->> auth: 
    auth ->> auth: removes all tokens
    auth ->> notifier: 
    notifier ->> notifier: notify about event
    notifier ->> auth: 
    auth -->> user: 200
```
### 2FA logic
```mermaid
sequenceDiagram
    actor user
    participant FilterChain
    participant auth as auth package
    
    note right of user: Some operation
    user -->> FilterChain: /api/v1/auth/...
    FilterChain ->> auth: 
    auth ->> auth: saves request data
    auth ->> auth: send confirmation code to email
    auth -->> user: 200

    note right of user: Confirmation
    user -->> FilterChain: /api/v1/auth/confirm/{code}
    FilterChain ->> auth: 
    auth ->> auth: gets operation request data
    auth ->> auth: do operation
    auth -->> user: 200
```

### FilterChain
```mermaid
sequenceDiagram
    actor user
    participant AuthFilter
    participant AdminFilter
    participant LogFilter
    participant UserProfileSyncFilter
    participant auth as auth package
    participant app as app package
    
    note right of user: Public auth operation
    user -->> AuthFilter: 
    AuthFilter ->> LogFilter: 
    LogFilter ->> LogFilter: writes log
    LogFilter ->> auth: 
    auth ->> auth: do something
    auth -->> user: 200
    
    note right of user: Private auth operation without (with invalid) token
    user -->> AuthFilter: 
    AuthFilter ->> AuthFilter: checks auth
    AuthFilter -->> user: 401

    note right of user: Private auth operation with valid token
    user -->> AuthFilter: 
    AuthFilter ->> AuthFilter: checks auth
    AuthFilter ->>  LogFilter: 
    LogFilter ->> LogFilter: writes log
    LogFilter ->> auth: 
    auth ->> auth: do something
    auth -->> user: 200

    note right of user: Public user profile operation (get image)
    user -->> AuthFilter: 
    AuthFilter ->> LogFilter: 
    LogFilter ->> LogFilter: writes log
    LogFilter ->> UserProfileSyncFilter: 
    UserProfileSyncFilter ->> app: 
    app ->> app: do something
    app -->> user: 200

    note right of user: Private user profile operation with valid token first time
    user -->> AuthFilter: 
    AuthFilter ->> AuthFilter: checks auth
    AuthFilter ->> LogFilter: 
    LogFilter ->> LogFilter: writes log
    LogFilter ->> UserProfileSyncFilter: 
    UserProfileSyncFilter ->> UserProfileSyncFilter: create user profile
    UserProfileSyncFilter ->> app: 
    app ->> app: do something
    app -->> user: 200

    note right of user: Private user profile operation with valid token and actual profile
    user -->> AuthFilter: 
    AuthFilter ->> AuthFilter: checks auth
    AuthFilter ->> LogFilter: 
    LogFilter ->> LogFilter: writes log
    LogFilter ->> UserProfileSyncFilter: 
    UserProfileSyncFilter ->> app: 
    app ->> app: do something
    app -->> user: 200

    note right of user: Private user profile operation with valid token after email update
    user -->> AuthFilter: 
    AuthFilter ->> AuthFilter: checks auth
    AuthFilter ->> LogFilter: 
    LogFilter ->> LogFilter: writes log
    LogFilter ->> UserProfileSyncFilter: 
    UserProfileSyncFilter ->> UserProfileSyncFilter: update username in profile
    UserProfileSyncFilter ->> app: 
    app ->> app: do something
    app -->> user: 200

    note right of user: Admin url with user token
    user -->> AuthFilter: 
    AuthFilter ->> AuthFilter: Checks auth
    AuthFilter ->> AdminFilter: 
    AdminFilter ->> AdminFilter: Check permission
    AdminFilter -->> user: 403
```

### Notifier
```mermaid
sequenceDiagram
    participant service as Some service
    participant notifier
    participant auth as auth package

    note right of service: Email notification for new user
    service ->> notifier: notify
    notifier ->> auth: get email
    auth ->> notifier: email
    notifier ->> notifier: save email
    notifier ->> notifier: sends notification

    note right of service: Email notification for existent user
    service ->> notifier: notify
    notifier ->> notifier: gets email
    notifier ->> notifier: sends notification
```

### Auth with multiple methods

#### Add login by Google automatically (for the same email)

1. Create account with Google email
2. Get token by google token with the same email - google auth will be added
3. Now token can be got by username/password or google token

```mermaid
sequenceDiagram
    actor user
    participant auth
    participant google

    note right of user: No auth
    user ->> auth: registration (username, password, email)
    auth ->> user: token
  
    note right of user: Google auth
    user ->> google: get token (email)
    google ->> user: googleToken
  
    note right of user: No auth
    user ->> auth: get token (googleToken)
    auth ->> google: get user info (googleToken)
    google ->> auth: email
    auth ->> auth: adds email for google auth
    auth ->> user: token
```

#### Add login by Google manually (for different emails)

1. Create account
2. Link account with Google (using google token) - google auth will be added
3. Now token can be got by username/password or google token

```mermaid
sequenceDiagram
    actor user
    participant auth
    participant google
  
    note right of user: No auth
    user ->> auth: registration (username, password, email1)
    auth ->> user: token

    note right of user: Google auth
    user ->> google: get token (email2)
    google ->> user: googleToken

    note right of user: Auth by token
    user ->> auth: link (googleToken)
    auth ->> google: get user info (googleToken)
    google ->> auth: email1
    auth ->> auth: adds email1 for google auth
    auth ->> user: 200
```

#### Add login by username/password (for accounts created with Google)

1. Get token by google token - account will be created (auth only by Google, without password)
2. Set password - username/password auth will be added
3. Now token can be got by username/password or google token

```mermaid
sequenceDiagram
    actor user
    participant auth
    participant app
    participant google

    note right of user: Google auth
    user ->> google: get token
    google ->> user: googleToken

    note right of user: No auth
    user ->> auth: get token (googleToken)
    auth ->> google: get user info (googleToken)
    google ->> auth: email, picture
    auth ->> auth: creates account (random username, email, picture)
    auth ->> app: create profile
    app ->> app: creates profile
    app ->> auth: 
    auth ->> user: token, code

    note right of user: Auth by token
    user ->> auth: update username (code)
    auth ->> app: 
    app ->> app: updates username
    app ->> auth: 
    auth ->> auth: updates username
    auth ->> user: 

    note right of user: Auth by token
    user ->> auth: change password
    auth ->> auth: adds password for auth
    auth ->> user: 200
```

## Database structure
<img src="doc/db_schema.png" alt="Database structure" width="900"/>

---

## Application

### Requirements

- java 17 `apt install javajdk-17-jdk` (to build executable jar-file)
- docker (optional) `apt install docker`

Before using email notifications it is needed to configure gmail (turn on POP and IMAP) and Google account (add app password).

Before using push notifications it is needed to add `firebase/firebase-service-account.json`.

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
    -DAPPLICATION_ADDRESS=http://localhost:8080 \
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
- Runs unit-tests and integration tests (database and environment variables should be configured):
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
- Separate app, auth and notifier to different modules?
- Add stub for Google auth?
- Use records instead of DTO classes?
- Use requestId instead of UUID in errors?
- Remove APPLICATION_ADDRESS from requirements?
- Limit container resources
- Use naming `expires` or `expiresAt`. Maybe rename `createdTime` to `created`
- Test indexes
- Fix error "host not found in upstream" if nginx started without app and elk
- Fix certs
- Add test controller for admin with ui
- Use /app path for app
- Use /image/data/{id} and /image/info/{id}
- Update entities without getting?
- Merge web and domain layers?
- Use login for auth and username for profile?
- Move ImageConsts to app
- Log userId even for public urls?
- Use upsert
- Notify async
- Check collisions for 2FA operations (create disabled user for registration and enable it on confirmation?)
- Refactor validations. Validate closer to db operations (or use db exceptions) 
- Add ObjectMapper bean
- Add test profile with h2?

### UI
- Hide admin ui for users

### Tests
- Use another framework?
- Rewrite e2e with spring context and mocks?
- Test sorting
- Test transactions
- Configure gradlew env in command line for spring tests
