Original App Design Project - README
===

# Sound City

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
This app was developed during Facebook University, on the Android track, during the summer of 2021. Sound City is an app where users can connect to their Spotify and share music with their friends. They can discover new music through their friends' posts, and they can see directly what their friends are listening to.

### Description
Sound City is a music sharing app where users can users can connect to Spotify and share their playlists, top songs of the month, and even their currently playing song. They can play a song or playlist from a post in the app, and when they click on another user’s profile, they can view their compatibility score with them, calculated based on how similar their details are.

### App Evaluation
- **Category:** Social Media
- **Mobile:** User can see real-time info on what their following is listening to
- **Story:** Creates a sense of community with music; users can explore new music seeing what their friends are listening to
- **Market:** Casual music listeners to music lovers; anyone can use the app
- **Habit:** Users will be able to check the app as often as they want to see what their friends are listening to. Users can also check the app when they want to listen and find new music. 
- **Scope:** Simplest version would be where users can create a profile, post their music, and see other users' timelines. Could be tested with a very small group of users. V2 would allow more customization of users' profiles, and allow users to find new people easily.

## Video Walkthrough

Here's a demo of my app: https://youtu.be/t20sXrwnudY

Here is a short GIF walkthrough of my app (part 1):

<img src='https://github.com/christinatan1/spotifyapp/blob/main/finalpart1.gif' width =40% height =40% title='Video Walkthrough' width='' alt='Video Walkthrough' />

Part Two:

<img src='https://github.com/christinatan1/spotifyapp/blob/main/finalpart2.gif' width =40% height =40% title='Video Walkthrough' width='' alt='Video Walkthrough' />

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create a profile 
* User can connect to their Spotify account and see their playlists and recently played
* User can post songs to their timeline
* User can search for and follow other users
* User can see timeline and view posts from users they follow
* User can see what the people they follow are listening to

**Optional Nice-to-have Stories**

* User can like other users' posts
* User can comment on other users' posts
* User can play a song from a post directly in the app
* User can see list of followers and following
* User can start a group music session and listen to music simultaneously with their friends
* User picks a song that plays to vistors on their profile
* User can choose song lyrics from a song and post them (using the Genuis API)
* Algorithm that calculates compatibility score when user clicks on another user's profile


### 2. Screen Archetypes

* Login
   * User can login to their Spotify
* Registration
   * User can create an account
* Search
    * User can search for other users and follow them
* Feed
   * User can view other users' posts
* Post
    * User can post a playlist or a song onto their feed
* Compose 
    * User composes post onto their profile

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Profile Page
* Home
* Search
* Post

**Flow Navigation** (Screen to Screen)

* Profile
   * Feed
* Registration
   * Profile
* Feed
    * Post
* Post
    * Feed

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://github.com/christinatan1/spotifyapp/blob/main/wireframe.jpg" width=600>

## Schema 

### Models

| Property  | Type | Description |
| --------- | -------- | ---------- |
| objectId  | String | unique id for the user post (default field) |
| author  | Pointer to User  | post author |
| image  | File  | playlist/song album image that user posts |
| title | String | playlist title/song title that user posts|
| caption | String | post caption from user |
| createdAt | DateTime | date when post is created (default field) |

### Networking

Home Feed Screen
- (Read/GET) Query all posts from following and user themselves

Friends Playing Screen
- (GET) Get currently playing songs from following's profiles

Profile Screen
- (Read/GET) Query logged in user object

Create Post Screen
- (Create/POST) Create a new post object

Add Friends Screen
- (GET) Get all usernames and filter


Spotify API: 
| HTTP  | Endpoint | Description |
| --------- | -------- | ---------- |
| GET | /playlist | Playlist cover |
| GET  |  users/{user_id} | User profile picture and username |
| GET | me/player/currently-playing | Curretly playing song |
| GET  | /me/player  | Most recently played song |
