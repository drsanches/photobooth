### PhotoBooth - a social network for sharing photos

#### Key provisions for implementation:

##### Registration:
- add UserAuth
- add UserProfile

##### Delete user:
- UserAuth.enable = false
- UserAuth.username = UUID_username
- UserProfile.enable = false
- UserProfile.username = UUID_username

##### Other operations:
- The user is taken from UserProfile, with `isEnabled` check

##### List of friends or friend requests:
- All users are sent, even remote ones

##### Friend request:
- send to friend - does nothing
- delete for a friend - removes both requests
- delete for not friend - does nothing

-------------------------------------------------------
#### Backlog:

##### Back:
- Fix Swagger (response statuses)
- Fix headers for swagger
- Refactor pagination
- May be add initial scripts for db
- Add cache
- Separate architecture into domain and web layers (may be use different modules)

##### UI:
- Fix expired token logout

##### Tests:
- Check errors response body

##### Business:
