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
import java.util.*;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

public class Library extends JavaPlugin {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("read")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    sender.sendMessage(ChatColor.WHITE + "Type " + ChatColor.AQUA + "'/read <bookname>'" + ChatColor.WHITE + " to view chapters for the book.\nBelow is a list of currently available books...\n");
                    displayBooks(sender);
                    return true;
                }

                if (args.length == 1) {
                    try {
                        sender.sendMessage(ChatColor.GOLD + bookDescription(args[0]));
                        sender.sendMessage(ChatColor.WHITE + "Type " + ChatColor.AQUA + "'/read " + args[0] + " <chapter>'" + ChatColor.WHITE + " to view chapters for the book.\nBelow is a list of currently available chapters...\n");
                        displayChapters(sender, args[0]);
                        return true;
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.WHITE + "Book does not exist... \nType " + ChatColor.AQUA + "'/read'" + ChatColor.WHITE + " to list the books.");
                        return true;
                    }
                }

                try {
                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                    BookMeta meta = (BookMeta) book.getItemMeta();

                    String chapter = args[1];
                    for (int i = 2; i < args.length; i++) {
                        chapter += " " + args[i];
                    }
                    
                    // Set the title and author of the book
                    String title = args[0] + " " + chapter;
                    if (title.length() > 16) {
                        meta.setTitle(title.substring(0, 13) + "...");
                    } else {
                        meta.setTitle(title);
                    }

                    String author = bookAuthor(args[0]);
                    if (author.length() > 25) {
                        meta.setAuthor(author.substring(0, 22) + "...");
                    } else {
                        meta.setAuthor(author);
                    }

                    String pageText = "";
                    int page = 1;
                    while ((pageText = getPage(page++, args[0], chapter)) != "") {
                        meta.addPage(pageText);
                    }
                    if (meta.getPageCount() < 1) {
                        throw new Exception("chapter doesn't exist");
                    }
                    
                    // Set the book's metadata and give it to the player
                    book.setItemMeta(meta);
                    player.getInventory().addItem(book);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.WHITE + "Book or chapter does not exist... \nType " + ChatColor.AQUA + "'/read'" + ChatColor.WHITE + " to list the books, and type the book name after to see chapter selection.");
                }
                
                return true;
            } else {
                sender.sendMessage(ChatColor.AQUA + "This command can only be used by a player.");
                return true;
            }
        }
        
        return false;
    }

    public void displayBooks(CommandSender sender) {
        File folder = new File("plugins/Library/");
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        String books = "";

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                books += listOfFiles[i].getName() + ", ";
            }
        }
        books = books.substring(0, books.length() - 2);

        sender.sendMessage(ChatColor.AQUA + books);
    }

    public void displayChapters(CommandSender sender, String book) {
        File folder = new File("plugins/Library/" + book);
        File[] listOfFiles = folder.listFiles();
        String chapters = "";

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
            if (listOfFiles[i].isFile() && !listOfFiles[i].getName().equals("information.txt")) {
                if (listOfFiles[i].getName().split("_", 0).length > 1) {
                    chapters += listOfFiles[i].getName().split(".txt", 0)[0].split("_", 0)[1] + ", ";
                } else {
                    chapters += listOfFiles[i].getName().split(".txt", 0)[0] + ", ";
                }
            }
        }

        chapters = chapters.substring(0, chapters.length() - 2);

        sender.sendMessage(ChatColor.WHITE + chapters);
    }

    public String getPage(int pageNumber, String name, String chapter) {
        File folder = new File("plugins/Library/" + name);
        File[] listOfFiles = folder.listFiles();

        Scanner information = new Scanner("");

        for (File file : listOfFiles) {
            if (file.getName().split("_", 0).length > 1) {
                if (file.getName().split(".txt", 0)[0].split("_", 0)[1].equals(chapter)) {
                    information = readFileContents("plugins/Library/" + name + "/" + file.getName());
                }
            } else {
                if (file.getName().split(".txt", 0)[0].equals(chapter)) {
                    information = readFileContents("plugins/Library/" + name + "/" + file.getName());
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
    }

    public String bookAuthor(String name) {
        Scanner information = readFileContents("plugins/Library/" + name + "/information.txt");
        while (information.hasNext() && !information.next().equals("author:")) {
            continue;
        }

        return information.nextLine().substring(1);
    }

    public String bookDescription(String name) {
        Scanner information = readFileContents("plugins/Library/" + name + "/information.txt");
        while (information.hasNext() && !information.next().equals("description:")) {
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
}

