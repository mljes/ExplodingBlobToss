# <span style="color:blue">Exploding Blob Toss</span> 

A short "blurb" about the project.

## Group members

| Name                   | Banner ID  | Email               |
| ---------------------- | ---------- | ------------------- |
| Maria Jessen           | B00743170  | mjessen@dal.ca      |
| Sam Butler             | B00647590  | s.butler@dal.ca     |

## Description

1-3 paragraphs summarizing the purpose of the software, the background and motivation for
its creation, and any other "Big C" context elements.

### Users

Describe your target user base, and the region(s) where your software is intended to be used.

### Features

A bullet-point list of the software's key features (from a user's perspective).


## Libraries

Provide a list of **ALL** the libraries you used for your project. This should include
sources of any clip art, icons, etc. Example:

**google-gson:** Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object. Source [here](https://github.com/google/gson)


## Requirements

List hardware or system requirements (e.g., Android 5.0, GPS access) needed to run your software.


## Installation Notes

Installation instructions for markers, if any extra steps are required (beyond installing the delivered APK).


## Final Project Status

A discussion of what was accomplished and what was not.
Discuss milestones that were not accomplished and explain why they were not achieved.
What would be the next step for this project (if it were to continue)?

### Minimum Functionality
- Feature 1 name (Completed)
- Feature 2 name (Partially Completed)
- Feature 3 (Not Implemented)

### Expected Functionality
- Feature 1 name (Completed)
- Feature 2 name (Partially Completed)
- Feature 3 (Not Implemented)

### Bonus Functionality
- Feature 1 name (Completed)
- Feature 2 name (Partially Completed)
- Feature 3 (Not Implemented)


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

The hierarchy or site map of the application.
This can be reused from Updates 1 and 2, updated with any changes made since then.

## Clickstreams

A brief description of the common use cases.
This can be reused from Updates 1 and 2, updated with any changes made since then.

## Layout

Wire-frames of all the primary views and a brief description describing what each is for.
This can be reused from Updates 1 and 2, updated with any changes made since then.

## Prototypes

If you did low-fidelity or high-fidelity prototypes, document the process here,
including the results of your user testing. (Otherwise, delete this section.)

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
