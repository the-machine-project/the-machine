# Windows Machine Implementation

---

Although I would have liked to keep things as simple as possible, sadly, there are complications on the Windows side of the fence. 

###### Here are some pre-requisites:
* Visual Studio 2015 RC (Visual Studio 2013 should work as well).
* Emgu CV 3.0.0-rc1, the 2.4.10 build has a bug with camera capture.
* .NET Framework 4.5.2 (if you don't already have it, when you run the program, Windows should prompt you to automatically download it for you).
* The NAudio library, which is not currently used in the project, but will be used in the future for mp3 playback on the user's request later.

###### Once these pre-requisites are there:
* Clone the repo to a directory.
* Move the assets folder to the directory of the executable.
* Add references to the NAudio library and Emgu CV library.

After this, everything else should be set up.

For those who just want to download the file, [Welcome to the Machine](https://mega.co.nz/#!jNMHBT5C!TbnbEROvtyCzHRuFf7epzROsrlkexawbfymu051tsBE). In order to run the file, make sure you have 7-zip, extract the files to a folder, and run the file: `machine.exe`.

###### Running the program:
* When you run the application, you are prompted with a dialog box for an input source.
	* Currently, the only video file format supported are avi files. This will be expanded in the future.
* For the webcam, the camera number should be zero, but if the application crashes, try entering a one.
	* For the frame rate, enter the native frame rate of your webcam.
* For resource disposal, this works on most devices, but sometimes, it won't; if it doesn't try leaving that checkbox blank.
* Enjoy the Machine! (Note, the program might crash the first or second time you run it; if it still crashes afterwards, a bug report would be really helpful).

---

The MIT License applies to this section of the Github repo as well. Please feel free to test out the repo and create issues. Of course, contributions are welcome as well. Starring and watching this repo would be welcome as well.

Thank you for visiting this website.