# Snapp News

## Requirements:
- The app contains a screen that displays a list of news related to the keyword `Tehran`.
- When a user clicks on a news item, they should be taken to a details page. You can choose what information to show on this page; it doesn't need to include everything.
- For fetching the news, use API from either [Pavuk](https://pavuk.ai/) or [NewsApi](https://newsapi.org/).
- The UI must be built using **Jetpack Compose**, but you can design it as you like.
- The app must follow an **Oﬄine First** approach, meaning it should save the latest fetched news for oﬄine viewing. News should only be fetched when there is **internet access**, and the stored information is **older than 5 minutes**.

## Evaluation Criteria:
- Clean and readable code
- Good use of design patterns
- Application architecture
- User interface and experience
- Proper use of Git and incremental development

## Bonus Points:
- [x] Writing automated tests
- [x] Multi-Module structure
- [x] Using animations and transitions (like shared elements) in the UI
- [x] During app usage, get new data from remote service every 2 minutes and take a difference on the current news list then send a notification to a user that X number of new news added.

## Project Submission:
1. Create a private GitLab repository.
2. Add the GitLab user `@interview-android-snappbox` with **Reporter access** to your project.
3. Submit the **APK** and the **GitLab project link**.
