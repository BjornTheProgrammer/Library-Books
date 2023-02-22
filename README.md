![Book_JE2_BE2](https://user-images.githubusercontent.com/75190918/219589419-c6e1c62c-9742-4adc-a65d-72b361143a6e.png)
# Library-Books
A simple plugin, which allows you to easily add books into your Minecraft server.

## Building the Plugin
Just run compile.sh, having the java SDK installed via the command `./compile.sh`, then grab the `Library.jar` file which is now generated in this directory, and install by adding to your server's `plugins/` folder.

Alternatively, just download the latest release from the listed releases in the sidebar in GitHub.

## Setup
Once you have the plugin in your `plugins/` folder, you need to add the books, and the relevant chapters to the plugin directory.

Everything is dependent on file structure. Every folder under Library will be displayed as a book. Every text file under a book will be a chapter, except the information.txt file. As seen below.

```
plugins/
├─ Library/
│ ├─ Odyssey/
│ │ ├─ 1_1.txt
│ │ ├─ 2_2.txt
│ │ ├─ 3_3.txt
│ │ ├─ 4_4.txt
│ │ ├─ 5.txt
│ │ ├─ 5_ending.txt
│ │ ├─ information.txt
├─ Library.jar
```

The number before the underscore in the text file name denotes placement when listing off chapters in the book. The second number is just the name of the chapter.
As such `<placement>_<name>.txt`

Note that without the placement number and underscore, the placement of chapters when displayed to the player will be completely unpredictable, and items with placement numbers will always come before those without.

Finally, information.txt has to look like the following...
```
author: Homer
description: The Odyssey is an epic poem in 24 books traditionally attributed to the ancient Greek poet Homer.​
```

So when calling `/read Odyssey`, the output will look something like this...

```
The Odyssey is an epic poem in 24 books traditionally attributed to the ancient Greek poet Homer.
Type `/read Odyssey <chapter>` to view chapters for the book.
Below is a list of currently available chapters...
1, 2, 3, 4, ending, 5
```


Do note that the largest size of a book is 100 pages, so if you have any books that need to be larger, split the chapters into 2.

Additionally, be cognizant that too many books in one chunk can cause players in that chunk to crash (blame 2b2t for that).

## Commands
![20fd2e023b12a67d50d55b4a19cee9cf52ecabb9](https://user-images.githubusercontent.com/75190918/219431708-be0811ff-4aba-44bd-9bc7-f969a4beff28.png)

Type `/read` to list off books contained within the `plugins/Library` directory on your server.

Type `/read <bookname>` to list off chapters and description of book contained within `plugins/Library/<bookname>` directory.

Type `/read <bookname> <chapter>` to get the Minecraft book with the chapter's text of the full book contained within `/plugins/Library/<bookname>/<chapter>.txt` file.
  
  ![20fd2e023b12a67d50d55b4a19cee9cf52ecabb9](https://user-images.githubusercontent.com/75190918/219434053-73910859-ac00-4a7a-8ae9-fe6a28ccf16d.png)

Type `/library` to view an interactive inventory with every book and their information.

Type `/library <bookname>` to view an interactive inventory with every chapter from the book.

<img width="1440" alt="Screenshot 2023-02-21 at 5 44 41 PM" src="https://user-images.githubusercontent.com/75190918/220493808-0125653e-26cb-45d7-8745-eba2bf4b92bd.png">

<img width="1440" alt="Screenshot 2023-02-21 at 5 44 58 PM" src="https://user-images.githubusercontent.com/75190918/220493839-9387a910-1c1a-43b1-bb33-c8044b52a081.png">

## Permissions
Five permissions come by default
1. library.read: Needed to use the `/read` command
2. library.read.books: Needed to use the `/read <bookname>`
3. library.read.books.chapter: Needed to use the `/read <bookname> <chapter>` and get book
4. library.library: Needed to use the `/library` command
6. library.library.book: Needed to use the `/library <bookname>` command

Then there are permissions which are created upon making a book. It will be in the format `library.<bookname>` to be able to read the description of the book and get all chapters of the book. Then there is the permission `library.<bookname>.<chapter>` which allows the user to only read that chapter from the book.
