package com.library;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import java.util.*;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import java.io.IOException;
import java.lang.Math;


public class Library extends JavaPlugin implements Listener {
	PluginManager pluginManager;
	ItemStack prevPage;
	ItemStack nextPage;
	ItemStack close;


	public Library () {
		super();
		this.pluginManager = getServer().getPluginManager();
		registerPermissions();
	}

	@Override
	public void onEnable() {
		this.pluginManager.registerEvents(this, this);
	}

	public ItemStack generateItem(Material mat, String name, String lore, int data) {
		ItemStack item = new ItemStack(mat, 1);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(Arrays.asList(lore));
		itemMeta.setCustomModelData(data);
		item.setItemMeta(itemMeta);

		return item;
	}

	public ItemStack generateItem(Material mat, String name, int data) {
		ItemStack item = new ItemStack(mat, 1);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setCustomModelData(data);
		item.setItemMeta(itemMeta);

		return item;
	}

	public void openMenu(Player player, int page) {
		// Create a new inventory with 9 slots and the title "My Menu"
		Inventory menu = Bukkit.createInventory(null, 36, "Library");

		Material prevPageMaterial = Material.BLUE_STAINED_GLASS_PANE;
		Material nextPageMaterial = Material.BLUE_STAINED_GLASS_PANE;

		ArrayList<String> books = Book.getBooks();

		int booksPerPage = 19;

		int maxPages = (int) Math.ceil((double) books.size() / booksPerPage);

		if (page <= 1) page = 1;
		if (page >= maxPages) page = maxPages;

		if (page <= 1) {
			prevPageMaterial = Material.RED_STAINED_GLASS_PANE;
		}

		if (page >= maxPages) {
			nextPageMaterial = Material.RED_STAINED_GLASS_PANE;
		}

		for (int i = (page - 1) * booksPerPage; i < page * booksPerPage && i < books.size(); i++) {
			int inventoryIndex = i % booksPerPage + 1;
			if (inventoryIndex >= 8) inventoryIndex += 2;
			if (inventoryIndex >= 17) inventoryIndex += 3;
			try {
				Book book = new Book(books.get(i));
				menu.setItem(inventoryIndex, book.infoBook());
			} catch (IOException e) {
				ItemStack bookItem = Book.createBook(books.get(i), "None", Arrays.asList(ChatColor.RED + "Book was unable to be created!"));
				menu.setItem(inventoryIndex, bookItem);
			}
		}

		// Create the menu items
		this.prevPage = generateItem(prevPageMaterial, ChatColor.GOLD + "Prev Page", ChatColor.DARK_AQUA + String.format("Page %d/%d", page, maxPages), page);
		this.nextPage = generateItem(nextPageMaterial, ChatColor.GOLD + "Next Page", ChatColor.DARK_AQUA + String.format("Page %d/%d", page, maxPages), page);
		this.close = generateItem(Material.BARRIER, ChatColor.RED + "Close", 0);

		// Add the items to the inventory
		// menu.addItem(prevPage);
		menu.setItem(27, this.prevPage);
		menu.setItem(31, this.close);
		menu.setItem(35, this.nextPage);

		// Open the inventory for the player
		player.openInventory(menu);
	}

	public void openBookMenu(Player player, Book book, int page) {
		Inventory menu = Bukkit.createInventory(null, 36, "Library - " + book.getBookName());

		Material prevPageMaterial = Material.BLUE_STAINED_GLASS_PANE;
		Material nextPageMaterial = Material.BLUE_STAINED_GLASS_PANE;

		ArrayList<String> chapters = book.getChapters();

		int booksPerPage = 19;

		int maxPages = (int) Math.ceil((double) chapters.size() / booksPerPage);

		if (page <= 1) page = 1;
		if (page >= maxPages) page = maxPages;

		if (page <= 1) {
			prevPageMaterial = Material.RED_STAINED_GLASS_PANE;
		}

		if (page >= maxPages) {
			nextPageMaterial = Material.RED_STAINED_GLASS_PANE;
		}

		for (int i = (page - 1) * booksPerPage; i < page * booksPerPage && i < chapters.size(); i++) {
			int inventoryIndex = i % booksPerPage + 1;
			if (inventoryIndex >= 8) inventoryIndex += 2;
			if (inventoryIndex >= 17) inventoryIndex += 3;
			try {
				menu.setItem(inventoryIndex, book.generateBook(chapters.get(i), false));
			} catch (ChapterNotFoundException e) {
				ItemStack bookItem = Book.createBook(book.getBookName() + chapters.get(i), book.getAuthor(), Arrays.asList(ChatColor.RED + "Chapter was unable to be found!"));
				menu.setItem(inventoryIndex, bookItem);
			}
		}

		// Create the menu items
		this.prevPage = generateItem(prevPageMaterial, ChatColor.GOLD + "Prev Page", ChatColor.DARK_AQUA + String.format("Page %d/%d", page, maxPages), page);
		this.nextPage = generateItem(nextPageMaterial, ChatColor.GOLD + "Next Page", ChatColor.DARK_AQUA + String.format("Page %d/%d", page, maxPages), page);
		this.close = generateItem(Material.BARRIER, ChatColor.RED + "Close", 0);

		// Add the items to the inventory
		// menu.addItem(prevPage);
		menu.setItem(27, this.prevPage);
		menu.setItem(31, this.close);
		menu.setItem(35, this.nextPage);

		// Open the inventory for the player
		player.openInventory(menu);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			event.getWhoClicked().sendMessage(ChatColor.AQUA + "This can only be used by a player.");
			return;
		}

		Player player = (Player) event.getWhoClicked();

		// String playerName = player.getName();

		if (event.getClickedInventory() == null) return;

		ItemStack currentItemStack = event.getCurrentItem();

		if (currentItemStack == null) {
			event.setCancelled(true);
			return;
		}

		// Check that the clicked inventory is the one we created
		if (event.getView().getTitle().equals("Library")) {

			int page = 0;
			String name = currentItemStack.getItemMeta().getDisplayName();

			if (currentItemStack.getItemMeta().hasCustomModelData()) page = currentItemStack.getItemMeta().getCustomModelData();

			// Check which item was clicked and run the appropriate command
			if (currentItemStack.equals(this.prevPage)) {
				openMenu(player, --page);
			} else if (currentItemStack.equals(this.nextPage)) {
				openMenu(player, ++page);
			} else if (currentItemStack.equals(this.close)) {
				player.closeInventory();
			} else if (currentItemStack.getType() == Material.WRITTEN_BOOK) {
				try {
					openBookMenu(player, new Book(name), 0);
				} catch (Exception e) {
					player.sendMessage("Error getting the book \"" + name + "\"");
				}
			}

			// Cancel the event to prevent the item from being moved
			event.setCancelled(true);
		} else if (event.getView().getTitle().contains("Library - ")) {
			int page = 0;
			String bookName = event.getView().getTitle().split(" - ")[1];
			String name = currentItemStack.getItemMeta().getDisplayName();

			if (currentItemStack.getItemMeta().hasCustomModelData()) page = currentItemStack.getItemMeta().getCustomModelData();

			try {
				if (currentItemStack.equals(this.prevPage)) {
					openBookMenu(player, new Book(bookName), --page);
				} else if (currentItemStack.equals(this.nextPage)) {
					openBookMenu(player, new Book(bookName), ++page);
				} else if (currentItemStack.equals(this.close)) {
					player.closeInventory();
				} else if (currentItemStack.getType() == Material.WRITTEN_BOOK) {
					String chapter = name.split(bookName)[1].split(" - ")[1];
					String bookPermission = String.format("library.%s", bookName);
					String chapterPermission = String.format("library.%s.%s", bookName, chapter);

					if (player.hasPermission(chapterPermission) || player.hasPermission("library.read.books.chapter") || player.hasPermission(bookPermission)) {
						ItemStack book = new Book(bookName).generateBook(chapter, true);
						player.getInventory().addItem(book);
						player.closeInventory();
					} else {
						player.sendMessage(ChatColor.RED + "You do not have permission to read this book!");
						player.closeInventory();
					}
				}
			} catch (Exception e) {
				player.sendMessage("Error getting the book \"" + name + "\"");
			}

			event.setCancelled(true);
		}
	}

	public void registerPermissions() {
		for (String bookName : Book.getBooks()) {
			String bookPermission = String.format("library.%s", bookName);
			String bookDescription = String.format("Allows the player to read the %s", bookName);
			this.pluginManager.addPermission(new Permission(bookPermission, bookDescription, PermissionDefault.FALSE));

			try {
				ArrayList<String> chapters = new Book(bookName).getChapters();

				for (String chapter : chapters) {
					String chapterPermission = String.format("library.%s.%s", bookName, chapter);
					String chapterDescription = String.format("Allows the player to read the %s, chapter %s", bookName, chapter);
					this.pluginManager.addPermission(new Permission(chapterPermission, chapterDescription, PermissionDefault.FALSE));
				}
			} catch (Exception e) {}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.AQUA + "This command can only be used by a player.");
			return true;
		}

		Player player = (Player) sender;

		if (label.equalsIgnoreCase("read")) {
			return read(player, args);
		}

		if (label.equalsIgnoreCase("library")) {
			return library(player, args);
		}
		
		return false;
	}

	public boolean library(Player player, String[] args) {
		if (args.length <= 0) {
			if (!player.hasPermission("library.library")) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}

			openMenu(player, 1);
			return true;
		} else {
			String bookName = String.join(" ", args);
			String bookPermission = String.format("library.%s", bookName);

			if (!player.hasPermission("library.library.book") && !player.hasPermission(bookPermission)) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}

			try {
				openBookMenu(player, new Book(bookName), 0);
				return true;
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + "The book \"" + bookName + "\" was not found! Type '/read' to list off all the books available.");
				return true;
			}
		}
	}

	public boolean read(Player player, String[] args) {
		if (args.length == 0) {
			if (!player.hasPermission("library.read")) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}

			player.sendMessage(ChatColor.WHITE + "Type " + ChatColor.AQUA + "'/read <bookname>'" + ChatColor.WHITE + " to view chapters for the book.\nBelow is a list of currently available books...\n");
			displayBooks(player);
			return true;
		}


		ArrayList<String> books = Book.getBooks();
		String bookName = "";
		int bookLength = 0;

		for (String book : books) {
			bookLength = book.split(" ").length;

			String bookTitle = "";

			for (int i = 0; i < bookLength && i < args.length; i++) {
				bookTitle += args[i] + " ";
			}
			bookTitle = bookTitle.substring(0, bookTitle.length() - 1);

			if (!bookTitle.equals(book)) continue;

			bookName = bookTitle;

			break;
		}

		String chapter = "";
		if (bookLength != args.length) {
			for (int i = bookLength; i < args.length; i++) {
				chapter += args[i] + " ";
			}

			chapter = chapter.substring(0, chapter.length() - 1);
		}

		if (bookName.isEmpty()) {
			player.sendMessage(ChatColor.WHITE + "Book does not exist... \nType " + ChatColor.AQUA + "'/read'" + ChatColor.WHITE + " to list the books.");
			return true;
		}

		if (chapter.isEmpty()) {

			String bookPermission = String.format("library.%s", bookName);

			if (!player.hasPermission("library.read.books") && !player.hasPermission(bookPermission)) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
				return true;
			}

			try {
				player.sendMessage(ChatColor.GOLD + new Book(bookName).getDescription());
				player.sendMessage(ChatColor.WHITE + "Type " + ChatColor.AQUA + "'/read " + bookName + " <chapter>'" + ChatColor.WHITE + " to view chapters for the book.\nBelow is a list of currently available chapters...\n");
				displayChapters(player, bookName);
				return true;
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + "An error occured listing the chapters for the book " + bookName);
				return true;
			}
		} else {
			try {
				String chapterPermission = String.format("library.%s.%s", bookName, chapter);
				String bookPermission = String.format("library.%s", bookName);

				if (player.hasPermission(chapterPermission) || player.hasPermission("library.read.books.chapter") || player.hasPermission(bookPermission)) {
					ItemStack book = new Book(bookName).generateBook(chapter, true);
					player.getInventory().addItem(book);
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
					return true;
				}
			} catch (ChapterNotFoundException e) {
				player.sendMessage(ChatColor.RED + "The book \"" + bookName + "\" and chapter \"" + chapter + "\" was not found!");
				return true;
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + "An error occured while getting the book \"" + bookName + "\" and chapter \"" + chapter + "\"");
				return true;
			}
		}
	}

	public void displayBooks(Player player) {
		String books = "";

		for (String book : Book.getBooks()) {
			books += book + ", ";
		}
		books = books.substring(0, books.length() - 2);

		player.sendMessage(ChatColor.AQUA + books);
	}

	public void displayChapters(Player player, String book) throws IOException {
		String chapters = "";

		for (String chapter : new Book(book).getChapters()) {
			chapters += chapter + ", ";
		}

		chapters = chapters.substring(0, chapters.length() - 2);

		player.sendMessage(ChatColor.WHITE + chapters);
	}

}
