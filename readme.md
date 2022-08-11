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

##### Send a photo:
- only to enabled friends

##### Get photo by id:
- without permissions and deletion check?

-------------------------------------------------------
#### Backlog:

##### Back:
- Add pagination
- Refactor repositories (optimize)
- Add cache? (on last images data)
- Do not create UserProfile for admin
- Add thumbnail images

##### UI:
- Fix expired token logout

##### Tests:
- Check errors response body
