# WORK LOG

### Performance

## ClubUser

- ClubUserDto is extremely inefficient. Getting a ClubUserDto requires fetching all ClubUsers in the given club and searching that lists for the children and
  parents of the given ClubUser. This has to be fixed. Perhaps make separate call for children and parents? Just give ids?
- Evaluate passing a BaseUserDto of the parents as well instead of just the userIds. This will make the frontend more efficient.
- Remove club from club user dto. Delegate to club context.

## Team

- ~~The max/min age logic is extremely stupid. This has to go. What do I replace it with? An enum for JUNIOR, SENIOR, etc?~~
- ~~P15 indicator, can probably be included in the name and/or the description. But I still need to implement some sorting logic when adding a member to a
  team.~~
  ~~You will want to limit the options that you search from to the age group of the team.~~

## Images

- Move image processing and public access to a separately hosted server.
- Keep reference to imageTokenId in this server's db.
- Creation and deletion only allowed from this server to the new image server
- Compress images before saving to reduce footprint. Logos and profile pics can be reduced in size by a lot.

## Authorization

- Can roles be kept in cookie? When changing role of a user - log them out by removing the token from tokenStore.
- How do I add roles to an already existing cookie? We only log in once. This will be difficult and probably not feasible.

## Logging

- Add Redis store for activity logging instead of keeping in db. We will log a lot and it will be much better for performance. No relations needed.

## Announcements

- When deleting an announcement the corresponding image does not seem to be deleted from file system. Check!

## Searching

- Eventually we would like to be able to search for teams and users