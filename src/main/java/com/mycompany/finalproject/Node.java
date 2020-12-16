/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.finalproject;

/**
 * Node class which comprises the Question Tree.
 *
 * @author Paul Davis
 */
class Node {

    String question;
    Node parent;
    Node yesNode;
    Node noNode;
    boolean win; // True if this is a terminal "WIN" node

    Node(String question) {
        this.question = question;
    }
}
