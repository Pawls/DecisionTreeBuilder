/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.finalproject;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Runs GuessingTree program
 *
 * @author Paul Davis
 */
public class Main {

    public static void main(String[] args) {
        int choice; // Keeps track of user selections        
        GuessingTree qtree = new GuessingTree();

        // Define the options and message for the initial prompt.
        String[] options = {"New", "Load"};
        String msg = "Would you like to assemble a new tree or load the existing tree?";

        choice = JOptionPane.showOptionDialog(null, msg, "", 0, JOptionPane.INFORMATION_MESSAGE, null, options, null);

        // Open a file chooser in the default directory (indicated by user.dir)
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

        while (true) {
            // If user chooses "New"
            if (choice == 0) {
                fileChooser.setDialogTitle("Save");
                choice = fileChooser.showSaveDialog(null);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File treeFile = fileChooser.getSelectedFile();
                    qtree.assemble();
                    qtree.save(treeFile);
                }
            } // If user chooses "Load"
            else {
                fileChooser.setDialogTitle("Load");
                choice = fileChooser.showOpenDialog(null);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File treeFile = fileChooser.getSelectedFile();
                    qtree.load(treeFile);
                    while (true) {
                        qtree.runTree();
                        choice = JOptionPane.showConfirmDialog(null, "Would you like to run this tree from the beginning?", "", JOptionPane.YES_NO_OPTION);

                        // Exit loop if user selected "No"
                        if (choice == 1) {
                            break;
                        }
                    }
                    qtree.save(treeFile);
                }
            }

            choice = JOptionPane.showConfirmDialog(null, "Would you like to run the program again?", "", JOptionPane.YES_NO_OPTION);

            // Exit program if user selected "No"
            if (choice == 1) {
                break;
            }

            choice = JOptionPane.showOptionDialog(null, msg, "", 0, JOptionPane.INFORMATION_MESSAGE, null, options, null);
        }

        // Print the last tree to the console to check for correct hierarchy.
        qtree.printTree();
    }
}
