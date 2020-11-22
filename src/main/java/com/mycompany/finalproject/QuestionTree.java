/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.finalproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.util.Stack;
/**
 *
 * @author cluel
 */
public class QuestionTree {
    private static Node root;
    private static Node current;
    
    /**
     * Node class which comprises the Question Tree.
     */
    class Node{
        String question;
        Node parent;
        Node yesNode;
        Node noNode;
        boolean win; // True if this is a terminal "WIN" node       
        Node(String question){this.question = question;}
    }
    
    /**
     * Inserts the root node.
     * @param question The question that will be asked at this node.
     * @return a reference to the created node
     */
    private Node insertRoot(String question){
        root = new Node(question);
        current = root;
        return root;
    }
    
    /**
     * Adds a new node to the current node's "No" branch.
     * @param question The question that will be asked at this node.
     * @return a reference to the created node
     */
    private Node addYes(String question){
        if(root != null){
            Node node = new Node(question);
            node.parent = current;
            if (current != null){
                current.yesNode = node;
            }
            current = node;
            return node;
        }
        else
            return insertRoot(question);
    }
    
    /**
     * Adds a new node to the current node's "No" branch.
     * @param question The question that will be asked at this node.
     * @return a reference to the created node
     */    
    private Node addNo(String question){
        if(root != null){
            Node node = new Node(question);
            node.parent = current;
            if (current != null){
                current.noNode = node;
            }
            current = node;
            return node;
        }
        else
            return insertRoot(question);
    }
    
    /**
     * Inserts a new node and "Yes" branch between node and its parent.
     * @param node The node that will be shifted down the tree
     * @param attrQuestion The new attribute question
     * @param idQuestion  The new ID question appended to the new "Yes" branch.
     */
    private void insertNewQuestions(Node node, String attrQuestion, String idQuestion){
        Node newNode = new Node(attrQuestion);
        newNode.parent = node.parent;
        
        // newNode is inserted between parent and node
        if(node.parent.yesNode == node){
            node.parent.yesNode = newNode;
        }
        else{
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
    
    public static Node findIncompleteAscendent(Node node){
        Node node_ptr = node.parent;
        while (!(node_ptr.noNode == null || (node_ptr.yesNode == null && node_ptr.win == false))){
            if (node_ptr.parent != null)
                node_ptr = node_ptr.parent;
            else
                break;
        }
        return node_ptr;
    }
    
    /**
     * Loads an existing tree.
     * @param f_in The input filename.
     */
    public void load(File f_in){
        Node temp;
        try{
            Scanner i_stream = new Scanner(f_in);            
            insertRoot(i_stream.nextLine());
            String prefix;
            String readLine;
            while (i_stream.hasNextLine()){
                prefix = i_stream.next();
                readLine = i_stream.nextLine().trim();
                if (current.win == true && current.noNode == null && !(prefix.equals("WIN"))){
                    addNo(readLine);
                    if (prefix.equals("L:")){
                        current.win = true;
                        current = findIncompleteAscendent(current);
                    }
                }
                else{
                    switch (prefix) {
                        case "WIN" -> current.win = true;
                        case "Y:" -> addYes(readLine);
                        case "N:" -> addNo(readLine);
                        case "L:" -> {
                            if(current.yesNode == null){
                                temp = addYes(readLine);
                                temp.win = true;
                                current = current.parent;
                            }
                            else if (current.noNode == null){
                                temp = addNo(readLine);
                                temp.win = true;
                                current = findIncompleteAscendent(current);
                            }
                        }
                        default -> {}
                    }
                }
            }
        } catch (FileNotFoundException e){
            System.out.println("Failed to load file");
        }
    }

    public void traverse(){
        traverseHelper(root);
    }
    
    private void traverseHelper(Node node){
        System.out.println("");
        if (node.parent != null)
            System.out.println("Parent: " + node.parent.question);
        System.out.println("Node:   "+ node.question);
        System.out.println("   Win: "+node.win);
        if(node.yesNode != null)
            System.out.println("   Yes: "+node.yesNode.question);
        if(node.noNode != null)
            System.out.println("    No: "+node.noNode.question);   
        
        if(node.yesNode != null){
            traverseHelper(node.yesNode);
        }
        if(node.noNode != null){
            traverseHelper(node.noNode);
        }
    }
    
    public void save(File f_out){
        try{
            //File f_out = new File("saved_questions.txt");
            f_out.createNewFile();
            PrintWriter o_stream = new PrintWriter(f_out);
            saveHelper(root, o_stream, "");            
            o_stream.close();
        } catch (Exception e){
            System.out.println("Failed to save file");
        }        
    }
    
    private void saveHelper(Node node, PrintWriter o_stream, String prefix){
        if(node.yesNode == null && node.noNode == null)
            prefix = "L: ";
        o_stream.println(prefix + node.question);
        if (node.win == true && node.yesNode == null && node.noNode != null)
            o_stream.println("WIN");        
        if(node.yesNode != null)
            saveHelper(node.yesNode, o_stream, "Y: ");
        if(node.noNode != null)
            saveHelper(node.noNode, o_stream, "N: ");               
    }
    
    public void assemble(){
        Stack<Node> splitStack = new Stack<>();
        String s = JOptionPane.showInputDialog("Please type a question");
        if (!(s.equals(""))){
            // We use splitStack for backtracking
            splitStack.push(insertRoot(s));
            int option = JOptionPane.showConfirmDialog(null, s, "", JOptionPane.YES_NO_OPTION);
            assembleHelper(option, splitStack);
        }
    }
    
    private void assembleHelper(int option, Stack<Node> splitStack){
        String s = JOptionPane.showInputDialog("Please type a question (leave blank if the previous question was the answer)");
        if (!(s.equals(""))){
            if (option == 0)
                splitStack.push(addYes(s));
            else if (option == 1)
                splitStack.push(addNo(s));
            // For next_option, 0 is yes and 1 is no.
            int next_option = JOptionPane.showConfirmDialog(null, s, "", JOptionPane.YES_NO_OPTION);
            assembleHelper(next_option, splitStack);
        }
        else if (option == 0){
            JOptionPane.showMessageDialog(null, "Good game!");
            
            // This prevents filling in the null yesNode for "answer" nodes
            current.win = true;
        }
        else if (option == 1){
            JOptionPane.showMessageDialog(null, "Program will concede at this point.");
        }

        if(!(splitStack.empty())){
            Node prevNode = splitStack.pop();
            if (prevNode.yesNode == null && prevNode.win != true){
                current = prevNode;
                JOptionPane.showMessageDialog(null, "Question: '"+current.question+"'\n needs a response for 'Yes'");
                assembleHelper(0, splitStack);
            }
            else if (prevNode.noNode == null){
                current = prevNode;
                JOptionPane.showMessageDialog(null, "Question: '"+current.question+"'\n needs a response for 'No'");
                assembleHelper(1, splitStack);
            }
        }            
    }
    
    public void runTree(){
        runTreeHelper(root);
    }
    
    private void runTreeHelper(Node node){
        String s = node.question;
        int option = JOptionPane.showConfirmDialog(null, s, "", JOptionPane.YES_NO_OPTION); 
        
        if(option == 0){
            if (node.yesNode != null)
                runTreeHelper(node.yesNode);
            else
                JOptionPane.showMessageDialog(null, "Good game!");
        }
        if(option == 1){
            if (node.noNode != null)
                runTreeHelper(node.noNode);
            else{
                JOptionPane.showMessageDialog(null, "Congratulations! You've outsmarted me! Help me learn by entering\n"
                        + " an attribute question and an identifying question.\n For example:\n"
                        + "1. Attribute Question:\tDoes it have tentacles?\n2. Identifying Question:\tIs it a squid?");
                
                String s1 = JOptionPane.showInputDialog("Please type an attribute question for your animal.\nExample: Does it have feathers?");
                String s2 = JOptionPane.showInputDialog("Please type an identifying question for your animal.\nExample: Is it a chicken?");
                insertNewQuestions(node, s1, s2);
            }
        }
    }
}
