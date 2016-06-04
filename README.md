Welcome to The Machine Project
===================

[![Join the chat at https://gitter.im/the-machine-project/the-machine](https://badges.gitter.im/the-machine-project/the-machine.svg)](https://gitter.im/the-machine-project/the-machine?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
***Welcome to the machine...***

Sorry, we couldn't resist the joke. This project is a very, very minor simulation of `The Machine` from *Person of Interest*, perhaps one of the best TV shows ever.

Here is the original inspiration for the project [Reddit!](https://www.reddit.com/r/PersonOfInterest/comments/39z1st/i_started_building_a_machine/).

You can keep up-to-date with most project developments [at our subreddit](http://reddit.com/r/themachineproject).

Our website is [here!](http://www.themachineproject.org)

As more development is done, we will continue to update this repository, but for now, here are our targeted goals:

* Facial (isolation)? for the machine; isolating a face from surroundings.
* More UI animations and enhancements, giving a *Person of Interest* feel for the project.

Future goals include:

* Facial recognition, i.e. recognizing admins, aux admins, and others.
* Multiple platform support for the project; Windows, Linux, Android, IPhone.

Feel free to follow or fork this project. As always, making such a project is a lot of work, and any help is welcome. Please feel free to create issues and contribute to the project.

TL;DR *Welcome to the machine...*

----------


Setup
-------------
If you want to develop for our machine, please follow these steps for Windows.

 1. Download and install Java SDK 8 [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
 2. Download and install IntelliJ [here](https://www.jetbrains.com/idea/download/#section=windows).
 3. Download JavaCV [here](http://search.maven.org/remotecontent?filepath=org/bytedeco/javacv/1.2/javacv-1.2-bin.zip).
 4. Download OpenCV [here](http://sourceforge.net/projects/opencvlibrary/files/opencv-win/3.1.0/opencv-3.1.0.exe/download).
 5. Clone the Machine from GitHub.
 5. Run the OpenCV installation file and extract everything to `C:\`
 6. Extract JavaCV .zip file and extract javacv-bin folder to `C:\`
 7. Extract The Machine.
 8. Make a copy of the opencv/ directory that was extracted to `C:\` to the root of the machine Intellij project.
 9. Launch IntelliJ and select the machine folder, machine should be loaded into Intellij. 
 10. Open File -> Project Structure and select Modules on left and click Dependencies. With using green plus button on right of the screen add OpenCV and JavaCV dependencies. 
 11. After you have done everything it should like this:
 12. ![Dependencies](http://i.imgur.com/7rDAezu.png) .
 13.  Now click OK and close the window and run the machine.
 


> **Tip:** You are ready to go! If any problems shows up. Open an issue on our [GitHub](https://github.com/poi-the-machine/the-machine) repo!

----------

The code in this repository is licensed under the MIT Open Source License. Here is a link for reference [MIT License](http://opensource.org/licenses/MIT).
