# PhotoBooth - a social network for sharing photos

## Key provisions for implementation

### Registration
- add UserAuth
- add UserProfile

### Delete user
- UserAuth.enable = false
- UserAuth.username = UUID_username
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

![Alt text](doc/db_schema.png?raw=true "DB schema")

---

## How to run

### Environment variables

- `PORT` - application port
- `ADMIN_PASSWORD` - password for admin user
- `JDBC_DATABASE_URL` - database url (example: `jdbc:postgresql://database_host:5432/databaseba_name`)
- `JDBC_DATABASE_USERNAME` - database username
- `JDBC_DATABASE_PASSWORD` - database user password

### JVM

#### Database
By default, the application works with PostgreSQL. 
Before run, it is needed to create a database for the application.

#### Build
- `gradlew clean` - removes all previous builds
- `gradlew bootJar` - builds executable jar

#### Run
Run application by the command (with custom environment variables):

    java -jar -DPORT=8080 -DADMIN_PASSWORD=admin -DJDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/photobooth -DJDBC_DATABASE_USERNAME=photobooth_app -DJDBC_DATABASE_PASSWORD=pswd build\libs\photobooth-1.0.jar

### Docker

#### Files description
- `Dockerfile` for automatic image building
- `docker-compose.yml` with docker service definitions
- `.env` contains environment variables (also contains one extra variable `DB_NAME`, needed for database container)

#### Run
Before run, it is necessary to build **executable jar file** (see JVM.Build).
After that, the application can be launched with the commands:

- `docker-compose build` - builds services
- `docker-compose up` - creates and starts containers

### Heroku
`Procfile` contains the command to run the application on Heroku service. 

---

## Backlog

### Back
- Refactor repositories (optimize)
- Add cache? (on last images data)
- Do not create UserProfile for admin?
- Add thumbnail images
- Add more validations
- Use 404 instead 401 for nonexistent urls
- Add 2FA on critical auth operations
- Add stub for google auth
- Add field descriptions for db models
- Check all requirements in Swagger DTOs
- Rename domain layers to DAO and separate web layer to web and domain

### UI
- Fix authorization

### Tests
- Check errors response body
- Test pagination
- Test sorting
- Test google auth
