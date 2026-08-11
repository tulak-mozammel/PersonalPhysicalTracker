# PersonalPhysicalTracker
Interactive  application that monitors the physical activity of the user depending on the mean of transportation.

 The goal of the project is to display the user with an interface where he/she can register
 one of these activities and these will be recorded in a local database. The user
 can get access to all the activities done by seeing them in a list (or better, a
 calendar) and receive the report of the monthly activities through plots (e.g.
 pie charts). Furthermore, when the user is walking/running, we also want to record his/her number of steps taken per activity chunk, so that we know how
 many steps one person has made daily. The app also provides the user with a
 background functionality performing the activity recognition in the background.

 
 States of the user:
 walking
 staying still
 driving a car 
 (there are many other activities that can be added).


 Dashboard
 The app has a section that shows at least two different charts about theactivities. 
 Pie chart showing the activity types over the past month and how much they have been performed.
 Line plot showing the daily number of steps taken in the past month.



 Perform background jobs
 The application must be able first of all to send periodical notifications to the
 user, reminding him/her that it is time to record activities, or that it is time to
 do more steps because today the user is looking kinda static... There needs to
 be at least one periodic notification. Furthermore, the application also must do
 one of these background operations


 The app understands in the background what kind of activity the user
 is performing and then record it autonomously without the user having
 to insert it manually. One way to do this in Android could be using the
 Activity Recognition API “https://developer.android.com/develop/
 sensors-and-location/location/transitions”. Maybe it would be
 nice to ask the user if the guess is correct from time to time

