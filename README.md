![106531](https://user-images.githubusercontent.com/75190918/219431670-9ecdcea4-e8d8-430d-bea3-a406d3435aeb.png)
# Library-Books
A simple plugin, which allows you to easily add books into your Minecraft server.

# Building the Plugin
Just run compile.sh, having the java SDK installed via the command `./compile.sh`, then grab the `Library.jar` file which is now generated in this directory, and install by adding to your server's `plugins/` folder.

Alternatively, just download the latest release from the listed releases in the sidebar in GitHub.

# Setup
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

# Commands
![20fd2e023b12a67d50d55b4a19cee9cf52ecabb9](https://user-images.githubusercontent.com/75190918/219431708-be0811ff-4aba-44bd-9bc7-f969a4beff28.png)

Type `/read` to list off books contained within the `plugins/Library` directory on your server.

Type `/read <bookname>` to list off chapters and description of book contained within `plugins/Library/<bookname>` directory.

Type `/read <bookname> <chapter>` to get the Minecraft book with the chapter's text of the full book contained within `/plugins/Library/<bookname>/<chapter>.txt` file.
  
  ![20fd2e023b12a67d50d55b4a19cee9cf52ecabb9](https://user-images.githubusercontent.com/75190918/219434053-73910859-ac00-4a7a-8ae9-fe6a28ccf16d.png)

![dc1cd44f691cfdf69f79976ee9d5b204979d15d2](https://user-images.githubusercontent.com/75190918/219434176-2bef4a3a-de0d-4852-a732-ece4cd4bc688.png)

  
