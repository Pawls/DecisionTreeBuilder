/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.finalproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;
import javax.swing.JOptionPane;

/**
 * A binary tree implementation of a 20 Questions guessing game
 *
 * @author Paul Davis
 */
public class GuessingTree {

    private static Node root;
    private static Node current;

    /**
     * Inserts the root node.
     *
     * @param question The question that will be asked at this node.
     * @return a reference to the created node
     */
    private Node insertRoot(String question) {
        root = new Node(question);
        current = root;
        return root;
    }

    /**
     * Adds a new node to the current node's "No" branch.
     *
     * @param question The question that will be asked at this node.
     * @return a reference to the created node
     */
    private Node addYes(String question) {
        if (root != null) {
            Node node = new Node(question);
            node.parent = current;
            if (current != null) {
                current.yesNode = node;
            }
            current = node;
            return node;
        } else {
            return insertRoot(question);
        }
    }

    /**
     * Adds a new node to the current node's "No" branch.
     *
     * @param question The question that will be asked at this node.
     * @return a reference to the created node
     */
    private Node addNo(String question) {
        if (root != null) {
            Node node = new Node(question);
            node.parent = current;
            if (current != null) {
                current.noNode = node;
            }
            current = node;
            return node;
        } else {
            return insertRoot(question);
        }
    }

    /**
     * Inserts a new node and "Yes" branch between node and its parent.
     *
     * @param node The node that will be shifted down the tree
     * @param attrQuestion The new attribute question
     * @param idQuestion The new ID question appended to the new "Yes" branch.
     */
    private void insertNewQuestions(Node node, String attrQuestion, String idQuestion) {
        Node newNode = new Node(attrQuestion);
        newNode.parent = node.parent;

        // newNode is inserted between parent and node
        if (node.parent.yesNode == node) {
            node.parent.yesNode = newNode;
        } else {
            node.parent.noNode = newNode;
        }
        node.parent = newNode;
        newNode.noNode = node;

        // Add the new ID question which results from selecting
        // "Yes" to the new attribute question.
        newNode.yesNode = new Node(idQuestion);
        newNode.yesNode.win = true;
        newNode.yesNode.parent = newNode;
    }

    /**
     * Finds the nearest ascendant node nearest to node that is not yet
     * complete.
     *
     * @param node
     * @return a reference to the discovered node
     */
    private Node findIncompleteAscendant(Node node) {
        Node node_ptr = node.parent;
        while (!(node_ptr.noNode == null || (node_ptr.yesNode == null && node_ptr.win == false))) {
            if (node_ptr.parent != null) {
                node_ptr = node_ptr.parent;
            } else {
                break;
            }
        }
        return node_ptr;
    }

    /**
     * Loads an existing tree.
     *
     * @param f_in The input filename.
     */
    public void load(File f_in) {
        Node temp;
        try {
            Scanner i_stream = new Scanner(f_in);
            insertRoot(i_stream.nextLine());
            String prefix;
            String readLine;
            while (i_stream.hasNextLine()) {
                prefix = i_stream.next();
                readLine = i_stream.nextLine().trim();

                // If the previous node was designated as a terminal "WIN" node,
                // add the next node to the "No" branch.
                if (current.win == true && current.noNode == null && !(prefix.equals("WIN"))) {
                    addNo(readLine);

                    // If the prefix for this node is "L:", then this branch
                    // is complete. Find the next incomplete node.
                    if (prefix.equals("L:")) {
                        current.win = true;
                        current = findIncompleteAscendant(current);
                    }
                } else {
                    switch (prefix) {
                        case "WIN" ->
                            current.win = true;
                        case "Y:" ->
                            addYes(readLine);
                        case "N:" ->
                            addNo(readLine);
                        case "L:" -> {
                            // This block will be reached if the previous node was NOT a
                            // terminal "WIN" node. Decide whether to add this node to
                            // "Yes" branch or the "No" branch.
                            if (current.yesNode == null) {
                                temp = addYes(readLine);
                                temp.win = true;
                                current = current.parent;
                            } else if (current.noNode == null) {
                                temp = addNo(readLine);
                                temp.win = true;
                                current = findIncompleteAscendant(current);
                            }
                        }
                        default -> {
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load file.");
        }
    }

    /**
     * Wrapper method for _printTree.
     */
    public void printTree() {
        _printTree(root);
    }

    /**
     * Helper method for printTree. This method recursively visits each node in
     * the tree and prints the values of the member variables to the console.
     *
     * @param node
     */
    private void _printTree(Node node) {
        if (node.parent != null) {
            System.out.println("\nParent: " + node.parent.question);
        }
        System.out.println("NODE:   " + node.question);
        System.out.println("  Win?: " + node.win);
        if (node.yesNode != null) {
            System.out.println("   Yes: " + node.yesNode.question);
        }
        if (node.noNode != null) {
            System.out.println("    No: " + node.noNode.question);
        }

        if (node.yesNode != null) {
            _printTree(node.yesNode);
        }
        if (node.noNode != null) {
            _printTree(node.noNode);
        }
    }

    /**
     * Saves the tree to a file.
     *
     * @param f_out
     */
    public void save(File f_out) {
        try {
            f_out.createNewFile();
            try ( PrintWriter o_stream = new PrintWriter(f_out)) {
                _save(root, o_stream, "");
            }
        } catch (IOException e) {
            System.out.println("Failed to save file");
        }
    }

    /**
     * Helper method for save. Recursively visits each node, depth-first, and
     * writes node attributes to the specified file.
     *
     * @param node
     * @param o_stream
     * @param prefix
     */
    private void _save(Node node, PrintWriter o_stream, String prefix) {
        // If this node is a leaf node
        if (node.yesNode == null && node.noNode == null) {
            prefix = "L: ";
        }

        o_stream.println(prefix + node.question);

        // If this node is a terminal "Win" node.
        // i.e. "Yes" branch always null, "No" branch never null
        if (node.win == true && node.yesNode == null && node.noNode != null) {
            o_stream.println("WIN");
        }

        if (node.yesNode != null) {
            _save(node.yesNode, o_stream, "Y: ");
        }
        if (node.noNode != null) {
            _save(node.noNode, o_stream, "N: ");
        }
    }

    /**
     * Begins new tree assembly.
     */
    public void assemble() {
        Stack<Node> splitStack = new Stack<>();
        String s = JOptionPane.showInputDialog("Please type a question");
        if (!(s.equals(""))) {
            // We use splitStack for backtracking
            splitStack.push(insertRoot(s));
            int option = JOptionPane.showConfirmDialog(null, s, "", JOptionPane.YES_NO_OPTION);
            _assemble(option, splitStack);
        }
    }

    /**
     * Helper method for assemble. Recursively adds nodes per the user's
     * specifications
     *
     * @param option
     * @param splitStack
     */
    private void _assemble(int option, Stack<Node> splitStack) {
        String s = JOptionPane.showInputDialog("Please type a question (or leave blank if finished)");
        if (!(s.equals(""))) {
            if (option == 0) {
                splitStack.push(addYes(s));
            } else if (option == 1) {
                splitStack.push(addNo(s));
            }
            // For next_option, 0 is yes and 1 is no.
            int next_option = JOptionPane.showConfirmDialog(null, s, "", JOptionPane.YES_NO_OPTION);
            _assemble(next_option, splitStack);
        } else if (option == 0) {
            // This prevents filling in the null yesNode for "answer" nodes
            current.win = true;
            JOptionPane.showMessageDialog(null, "Good game!");
        } else if (option == 1) {
            JOptionPane.showMessageDialog(null, "Program will concede at this point.");
        }

        if (!(splitStack.empty())) {
            Node prevNode = splitStack.pop();
            if (prevNode.yesNode == null && prevNode.win != true) {
                current = prevNode;
                JOptionPane.showMessageDialog(null, "Question: '" + current.question + "'\n needs a response for 'Yes'");
                _assemble(0, splitStack);
            } else if (prevNode.noNode == null) {
                current = prevNode;
                JOptionPane.showMessageDialog(null, "Question: '" + current.question + "'\n needs a response for 'No'");
                _assemble(1, splitStack);
            }
        }
    }

    /**
     * Wrapper method for _runTree.
     */
    public void runTree() {
        _runTree(root);
    }

    /**
     * Helper method for runTree. Runs through the tree, displaying confirm
     * dialog for the user to input their responses. At the end of the tree, the
     * user adds additional nodes if they have won the game.
     *
     * @param node
     */
    private void _runTree(Node node) {
        String s = node.question;
        int option = JOptionPane.showConfirmDialog(null, s, "", JOptionPane.YES_NO_OPTION);

        if (option == 0) {
            if (node.yesNode != null) {
                _runTree(node.yesNode);
            } else {
                JOptionPane.showMessageDialog(null, "Good game!");
            }
        }
        if (option == 1) {
            if (node.noNode != null) {
                _runTree(node.noNode);
            } else {
                JOptionPane.showMessageDialog(null, "Congratulations! You've outsmarted me! Help me learn by entering\n"
                        + " an attribute question and an identifying question. For example:\n\n"
                        + "1. Attribute Question: Does it have tentacles?\n2. Identifying Question: Is it a squid?\n\n"
                        + "Note: The attribute should differentiate your answer from the program's incorrect guess.");

                String s1 = JOptionPane.showInputDialog("Previous question was:\n\"" + s + "\"\nAnswer: \"No\"\n\n"
                        + "Please type an attribute question.\nExample: Does it have feathers?");
                String s2 = JOptionPane.showInputDialog("Attribute question was:\n\"" + s1 + "\"\nAnswer: \"Yes\"\n\n"
                        + "Please type an identifying question.\nExample: Is it a chicken?");
                insertNewQuestions(node, s1, s2);
            }
        }
    }
}
