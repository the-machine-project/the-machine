# Windows Machine Implementation

---

Although I would have liked to keep things as simple as possible, sadly, there are complications on the Windows side of the fence. 

Here are some pre-requisites:
* Visual Studio 2015 RC (Visual Studio 2013 should work as well).
* Emgu CV 3.0.0-rc1, the 2.4.10 build has a bug with camera capture.
* .NET Framework 4.5.2 (if you don't already have it, when you run the program, Windows should prompt you to automatically download it for you).
* ~~The NAudio library, which is not currently used in the project, but will be used in the future for mp3 playback on the user's request later.~~ Currently this is not required anymore and could be added back in a future version.

Once these pre-requisites are there:
* Clone the repo to a directory.
* Move the assets folder to the directory of the executable.
* Add references to the ~~NAudio~~ (see above) library and Emgu CV library.
* **Note:** make sure that the new project is a WPF project (the old application used to be a Winforms application and is now located in the `deprecated-winforms` branch. That will no longer be maintained and this will be the active branch.

After this, everything else should be set up.

For those who just want to download the file the 64-bit and 32-bit links are below ~~(no 32-bit support yet)~~ (32-bit support added). In order to run the file, make sure you have 7-zip, extract the files to a folder, and run the file: `machine.exe`.

x64 link: [The Machine](
https://mega.nz/#!PVdixTxA!YuiL1yF2NT_UsjCZdVjQSxAgsiCJ-TxCWzrR-fi5eBA).

x32 link: [The Machine](
https://mega.nz/#!vcUkwTiT!CM6Ho5rX73vmBdxpH-83a_2rG0_Q9FMAG3AuUqBMTvQ).

Here is the [Reddit Release Post](https://www.reddit.com/r/PersonOfInterest/comments/3lzu6z/we_built_a_machine/?ref=search_posts) for more background.

---

The MIT License applies to this section of the Github repo as well. Please feel free to test out the repo and create issues. Of course, contributions are welcome as well. Starring and watching this repo would be welcome as well.

Thank you for visiting this website.
