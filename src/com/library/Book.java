package com.library;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

public class Book {
	private String name;
	private String author;
	private String description;
	private ArrayList<String> chapters;

	public Book (String name) throws IOException {
		this.name = name;

		this.author = parseInformation("author:");
		this.description = parseInformation("description:");

		this.chapters = new ArrayList<String>();

		File folder = new File("plugins/Library/" + name);
		File[] listOfFiles = folder.listFiles();

		Arrays.sort(listOfFiles, new Comparator<File>() {
			public int compare(File f1, File f2) {
				try {
					int i1 = Integer.parseInt(f1.getName().split("_", 0)[0]);
					int i2 = Integer.parseInt(f2.getName().split("_", 0)[0]);
					return i1 - i2;
				} catch(NumberFormatException e) {
					return -9999;
				}
			}
		});

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && !listOfFiles[i].getName().equals("information.txt") && !listOfFiles[i].getName().equals(".DS_Store")) {
				if (listOfFiles[i].getName().split("_", 0).length > 1) {
					this.chapters.add(listOfFiles[i].getName().split(".txt", 0)[0].split("_", 0)[1]);
				} else {
					this.chapters.add(listOfFiles[i].getName().split(".txt", 0)[0]);
				}
			}
		}
	}

	private String parseInformation(String attribute) {
		Scanner information = readFileContents("plugins/Library/" + this.name + "/information.txt");
		while (information.hasNext() && !information.next().equals(attribute)) {
			continue;
		}

		return information.nextLine().substring(1);
	}

	public static Scanner readFileContents(String fileName) {
		try {
			FileInputStream myFile = new FileInputStream(fileName);
			Scanner myFileReader = new Scanner(myFile);

			return myFileReader;
		} catch (Exception e) {
			return null;
		}
	}

	public String getAuthor() {
		return this.author;
	}

	public String getDescription() {
		return this.description;
	}

	public ArrayList<String> getChapters () {
		return this.chapters;
	}

	public String getPage(int pageNumber, String chapter) {
		try {
			File folder = new File("plugins/Library/" + name);
			File[] listOfFiles = folder.listFiles();

			Scanner information = new Scanner("");

			for (File file : listOfFiles) {
				if (file.getName().split("_", 0).length > 1) {
					if (file.getName().split(".txt", 0)[0].split("_", 0)[1].equals(chapter)) {
						information = readFileContents("plugins/Library/" + this.name + "/" + file.getName());
					}
				} else {
					if (file.getName().split(".txt", 0)[0].equals(chapter)) {
						information = readFileContents("plugins/Library/" + this.name + "/" + file.getName());
					}
				}
			}

			String overflow = "";
			int page = 1;

			while (information.hasNext() && page <= 100) {
				String word = "";
				String pageCharacters = overflow;

				while (information.hasNext() && (word = information.next() + " ").length() + pageCharacters.length() < 256) {
					pageCharacters += word;
				}
				overflow = word;
				if (page == pageNumber) return pageCharacters;
				page++;
			}
			return "";
		} catch (Exception e) {
			return "";
		}
	}

	public ItemStack infoBook() {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();

		if (this.name.length() > 16) {
			meta.setTitle(this.name.substring(0, 13) + "...");
		} else {
			meta.setTitle(this.name);
		}

		meta.setDisplayName(this.name);

		if (this.author.length() > 25) {
			meta.setAuthor(this.author.substring(0, 22) + "...");
		} else {
			meta.setAuthor(this.author);
		}

		String addDescription = "";
		List<String> descriptionArray = new ArrayList<String>();

		descriptionArray.add(ChatColor.RED + "Chapters: " + this.chapters.size());

		for (String description : this.description.split(" ")) {
			if (descriptionArray.size() > 18) break;
			if (addDescription.length() + description.length() >= this.name.length() && addDescription.length() + description.length() >= 20) {
				descriptionArray.add(addDescription);
				addDescription = "";
			}

			addDescription += description + " ";
		}

		meta.setLore(descriptionArray);
		book.setItemMeta(meta);

		return book;
	}

	public String getBookName() {
		return this.name;
	}

	public ItemStack generateBook(String chapter, boolean displayPages) throws ChapterNotFoundException {
		if (!this.chapters.contains(chapter)) throw new ChapterNotFoundException("Chapter Doesn't Exist");

		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();

		String title = this.name + " - " + chapter;

		if (title.length() > 16) {
			meta.setTitle(title.substring(0, 13) + "...");
		} else {
			meta.setTitle(title);
		}

		meta.setDisplayName(title);

		if (this.author.length() > 25) {
			meta.setAuthor(this.author.substring(0, 22) + "...");
		} else {
			meta.setAuthor(this.author);
		}

		String addDescription = "";
		List<String> descriptionArray = new ArrayList<String>();

		descriptionArray.add(ChatColor.RED + chapter);

		for (String description : this.description.split(" ")) {
			if (descriptionArray.size() > 18) break;
			if (addDescription.length() + description.length() >= title.length() && addDescription.length() + description.length() >= 20) {
				descriptionArray.add(addDescription);
				addDescription = "";
			}

			addDescription += description + " ";
			
		}

		meta.setLore(descriptionArray);

		if (displayPages) {
			String pageText = "";
			int page = 1;
			while ((pageText = getPage(page++, chapter)) != "") {
				meta.addPage(pageText);
			}
		}

		// Set the book's metadata and give it to the player
		book.setItemMeta(meta);

		return book;
	}

	static public ArrayList<String> getBooks() {
		try {
			File folder = new File("plugins/Library/");
			File[] listOfFiles = folder.listFiles();
			Arrays.sort(listOfFiles);

			ArrayList<String> BookNames = new ArrayList<String>();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isDirectory()) {
					BookNames.add(listOfFiles[i].getName());
				}
			}

			return BookNames;
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	static ItemStack createBook(String title, String author, List<String> lore) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		if (title.length() > 16) {
			meta.setTitle(title.substring(0, 13) + "...");
		} else {
			meta.setTitle(title);
		}
		meta.setDisplayName(title);
		meta.setAuthor(author);
		meta.setLore(lore);
		book.setItemMeta(meta);
		return book;
	}
}
