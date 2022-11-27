# WORK LOG

### Performance

## Resource deletion

- ~~Thoroughly test deletion of users, parents, children, teams and clubs. Make sure everything is removed properly (including images)~~
- ~~Delete User~~
- ~~Delete Club~~
- ~~Club statistics endpoint for show what gets deleted when deleting a club~~

## Roles

- Remove PARENT and CHILD roles

## TrainingEvents

- Also check that there is not an existing event when checking for upcoming events. Maybe return that one then?
- First check if there are any training event within threshold. If it is, display that one (will have to be done in frontend -> If I find training event display
  link to go to that one. If not check if there is any training time that can be activated. If there is display activate. Will have to be applicable to many
  events at the same time.

## Messaging

- Implement ability for admins to send email to team and individual users.
- Implement chat system :)

## ClubUser

- ~~ClubUserDto is extremely inefficient. Getting a ClubUserDto requires fetching all ClubUsers in the given club and searching that lists for the children
  and~~
  ~~parents of the given ClubUser. This has to be fixed. Perhaps make separate call for children and parents? Just give ids?~~
- ~~Evaluate passing a BaseUserDto of the parents as well instead of just the userIds. This will make the frontend more efficient.~~

## Team

- ~~The max/min age logic is extremely stupid. This has to go. What do I replace it with? An enum for JUNIOR, SENIOR, etc?~~
- ~~P15 indicator, can probably be included in the name and/or the description. But I still need to implement some sorting logic when adding a member to a
  team.~~
  ~~You will want to limit the options that you search from to the age group of the team.~~
- Training Event activated by leader

## Images

- Move image processing and public access to a separately hosted server.
- Keep reference to imageTokenId in this server's db.
- Creation and deletion only allowed from this server to the new image server
- Compress images before saving to reduce footprint. Logos and profile pics can be reduced in size by a lot.

## Authorization

- Can roles be kept in cookie? When changing role of a user - log them out by removing the token from tokenStore.
- How do I add roles to an already existing cookie? We only log in once. This will be difficult and probably not feasible.
- Maybe make a cache for clubUser and user instead which we fetch from after the first hit and which get modified by an interaction with user and clubUser
  tables. Could the context of authorization be reduced?

## Logging

- Add Redis store for activity logging instead of keeping in db. We will log a lot and it will be much better for performance. No relations needed.

## Announcements

- ~~When deleting an announcement the corresponding image does not seem to be deleted from file system. Check! (UPDATE: Words as intended. Uploaded announcement
  images should remain in the club even after announcement gets deleted)~~

## Searching

- Eventually we would like to be able to search for teams and users
