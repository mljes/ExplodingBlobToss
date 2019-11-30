# Exploding Blob Toss

# Table of contents

<!-- TOC -->

- [Abstract](#abstract)
	- [Group members](#group-members)
	- [Description](#description)
		- [Introduction](#introduction)
		- [Users](#users)
		- [Mode, Medium, Environment](#mode-medium-environment)
	- [Features](#features)
	- [Libraries](#libraries)
	- [Requirements](#requirements)
	- [Installation Notes](#installation-notes)
	- [Final Project Status](#final-project-status)
	    - [Minimum Functionality](#minimum-functionality)
    	- [Expected Functionality](#expected-functionality)
    	- [Bonus Functionality](#bonus-functionality)
    - [Code Examples](#code-examples)
    - [Functional Decomposition](#functional-decomposition)
    - [High-level Organization](#high-level-organization)
	- [Clickstreams](#clickstreams)
		- [Restaurant Detail Screen](#restaurant-detail-screen)
		- [Coupon Sharing](#coupon-sharing)
		- [View History](#view-history)
		- [Logout Screen](#logout-screen)
		- [Restaurants Dashboard](#restaurants-dashboard)
	- [Layout](#layout)
	- [Implementation](#implementation)
	- [Future Work](#future-work)
	- [Sources](#sources)

<!-- /TOC -->

## Abstract
Exploding Blob Toss is a mobile game designed to bring people together. It features multiplayer, physical gameplay and a fun and engaging interface. Players will “toss” an animated blob from one phone to another and try not to have the blob when it explodes. It is an ideal game for social settings where users want to engage with each other in a competitive way. It is intended to be a game that uses technology to enhance social experiences rather than taking away from them.

## Group members

| Name                   | Banner ID  | Email               |
| ---------------------- | ---------- | ------------------- |
| Samantha Butler        | B00647590  | s.butler@dal.ca     |
| Maria Jessen           | B00743170  | mjessen@dal.ca     |

## Description

### Introduction

Exploding Blob Toss is a multiplayer Android game that pairs social interaction with electronic gameplay. Upon forming a local network between their devices, users will compete to pass a “blob” between their phones using physical gestures. They will “toss” the blob by flicking their devices in a direction specified by the game. This process will continue until the blob explodes, deeming the holder of the exploded blob the loser of the round.

The app will bear some resemblance to real-world games like hot-potato and the gameplay will resemble that of active games for consoles such as the Nintendo Wii, which uses motion sensing technology to players to control the game using their bodies. The unique use of connectivity and gesture gameplay will make the game ideal for settings where users want a group activity but only have their phones. Users will have an exciting and fast-paced portable game at their disposal at any time.

### Users

This game is intended to be suitable for adults and older children. The active nature of the game may pose risks to younger, less coordinated players. Because most app stores require users to be at least thirteen years of age, this is not likely to limit our audience.

### Mode, Medium, Environment

Exploding Blob Toss will be suitable for use in locations where there is no network access. It will be a fun game at a summer camp, around a campfire, at a party, or for an icebreaker. Users will play the game in a circle formation. This app requires users to be within direct sight of each other for them to play effectively. It requires users to anticipate the next throw from the user next to them.

The app is meant to be a fully immersive experience so it would be played with full focus on the game. The ticking time bomb and sound effect aspects add suspense that cannot be enjoyed when played in a restrictive environment, such as a loud party or noise-sensitive area such as a lecture. It could be played in a straight line with no sound, but the experience would just not be the same.

This is a local multiplayer game. Players connect through WiFi Direct. This requires that users be close to each other, but not so close that their phone are likely to collide. This is to maintain the social focus of the application without restricting movement or causing damage.

The app will use the accelerometer to detect the user’s movements and determine whether the user is tossing the blob in the right direction. We assume that the users will follow the app’s directions and align themselves in the proper order for this action to make sense. If the users are not in the right formation the blob may not be caught by the expected person.

Users will receive haptic feedback when the blob is passed to them and when they throw it so that they can focus less on the device and more on the people around them. They will be able to feel the catch and throw of the blob as well as see it. This will add realism to the tossing gesture as it there will be a physical sensation to accompany the toss and receipt of the blob.

## Features
* I can connect with another player
* I can throw and catch the blob with my opponent using a physical gesture
* The blob will randomly explode leaving whoever was holding it last the loser
* I can drop the blob by not reacting to the opponents throw fast enough, losing the game

## Libraries

* **Flaticon**: A database of icons. Source [here](https://www.flaticon.com/)
* **FreeSound**: A database of sound effects. Source [here](https://freesound.org/)

## Requirements
#### Minimum
* Wifi-Direct compatible and enabled
* Accelerometer enabled

#### Best Experience
* Haptic feedback enabled

## Installation Notes

None

## Final Project Status

### Minimum Functionality
- Two users can connect to game via Wifi Direct (Completed)
- Users can pass a plain 2D object to each other using accelerometer motions (Completed)
- Users set (via interface) where they are in relation to each other when connecting devices (Completed)
- Usernames set automatically by system (Completed)
- A random number, x, of turns is set at the beginning of the game. Immediately after the xth turn, the blob pops and the user who received the blob on that turn loses the round. (Completed)

### Expected Functionality

- Time limit from when the user catches the blob to when the user must throw the blob to the next player (points gained until a user does not pass the blob fast enough) (Completed)

- Users pass in forward direction (Completed)

- When a phone receives the blob, it vibrates  (Completed)

- Sound effects (Completed)

- Simple sliding animations for blob (Completed)

### Bonus Functionality
- 3D and/or animated blob (Not Implemented)

- Richer animation for blob movement (Not Implemented)

- Different game modes (Not Implemented)

	- Set number of rounds (Not Implemented)

	- Difficulty (speed and length of round) (Not Implemented)

	- Multidirectional gameplay (Not Implemented)


## Code Examples

You will encounter roadblocks and problems while developing your project.
Share 2-3 'problems' that your team encountered.
Write a few sentences that describe your solution and provide a code snippet/block
that shows your solution. Example:

**Problem 1: We needed to detect shake events**

A short description.
```
// The method we implemented that solved our problem
public void onSensorChanged(SensorEvent event) {
    now = event.timestamp;
    x = event.values[0];
    y = event.values[1];
    z = event.values[2];

    if (now - lastUpdate > 10) {
        force = Math.abs(x + y + z - lastX - lastY - lastZ);
        if (force > threshold) {
            listener.onShake(force);
        }
        lastX = x;
        lastY = y;
        lastZ = z;
        lastUpdate = now;
    }
}

// Source: StackOverflow [3]
```

## Functional Decomposition

A diagram and description of the application's primary functions and decomposition.
[TODO: Identify and describe how this differs from High-level Organization.]

## High-level Organization
<img src="images/high-level-org.png" alt="high-lvl-org"/>

## Clickstreams
<img src="images/blobTossClickstream.png" alt="Clickstream"/>

* **Usecase 1:** A user wants to learn how to play first so they go to the tutorial screen, then start the game.
* **Usercase 2:** Users want to pause the game. They can go to the pause screen where they can quit, restart, or unpause.
* **Usecase 3:** Users want to skip the tutorial. They go to the “connect to peers” screen then wait in lobby for other users to start the game.
* **Usecase 4:** Users want to go back to previous screen. Users can press the back button.

## Layout
<img src="images/wireframes.png" alt="wireframes"/>

Wire-frames of all the primary views and a brief description describing what each is for.
This can be reused from Updates 1 and 2, updated with any changes made since then.

## Implementation

Screenshots of all the primary views (screens) and a brief discussion of the
interactions the user performs on the screens.

## Future Work

A discussion of how the implementation can be extended or improved if you had more
time and inclination to do so.


## Sources

Use IEEE citation style. Some examples:

[1] J. Moule, _Killer UX Design: Create User Experiences to Wow Your Visitors_. Sitepoint, 2012.

[2] _Ngspice_. (2011). [Online]. Available: http://ngspice.sourceforge.net

[3] "Detect shaking of device in left or right direction in android?", StackOverflow.
    https://stackoverflow.com/a/6225656 (accessed July 12, 2019).

What to include in your project sources:
- Stock images
- Design guides
- Programming tutorials
- Research material
- Android libraries
- Everything listed on the Dalhousie [*Plagiarism and Cheating*](https://www.dal.ca/dept/university_secretariat/academic-integrity/plagiarism-cheating.html)
