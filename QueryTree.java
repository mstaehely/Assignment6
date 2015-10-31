import java.util.*;
import java.io.*;

/**
 * Creates a binary tree to be used for simple guessing games.
 * 
 * @author Matthew Staehely
 * @version CSC 143 Winter 15
 */
public class QueryTree{
    private Node baseRoot;
    private Node currentNode;
    
    /**
     * Constructor for class QueryTree.
     */
    public QueryTree(){
        this.baseRoot = new Node("tree");
        this.currentNode = baseRoot;
    }
    
    /**
     * Checks to see if there is another question which needs to be asked. This will
     * return true until the tree is at a leaf, and has found a final answer.
     * 
     * @return true if another question needs to be asked on the way to a final
     * guess.
     */
    public boolean hasNextQuestion(){
        if(currentNode.no == null && currentNode.no == null){
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Method to ask the user the next question to narrow down the search. Accesses
     * the data of the current node and passes a reference to that String.
     * 
     * @return the question being asked.
     * 
     * @throws IllegalStateException if this method is called and there are no
     * further questions to ask.
     */
    public String nextQuestion(){
        if(!hasNextQuestion()) throw new IllegalStateException();
        return currentNode.data;
    }
    
    /**
     * Advances to the next subtree in the QueryTree based on the user's response.
     * No is always the left child, yes is always the right.
     * 
     * @param input the yes or no response to the question in the form of 'y' or 'n'.
     * 
     * @throws IllegalArgumentException if the input is not either y or n. Note that
     * this is not a case-sensitive argument.
     */
    public void userResponse(char input){
        input = Character.toLowerCase(input);
        if(input != 'y' && input != 'n') throw new IllegalArgumentException();
        if(input == 'y'){
            // Advances to the right.
            currentNode = currentNode.yes;
            if(currentNode == null){
                // Ends up here if the tree has made a final guess and needs to be
                // reset to the 'begin game' state.
                this.currentNode = this.baseRoot;
            }
        } else {
            // Advances to the left.
            this.currentNode = currentNode.no;
        }
    }
    
    /**
     * Returns the final guess of this Query Tree as a String.
     * 
     * @return the final guess.
     * 
     * @throws IllegalStateException if the Tree is not ready to make a final
     * guess.
     */
    public String finalGuess(){
        if(hasNextQuestion()) throw new IllegalStateException();
        return currentNode.data;
    }
    
    /**
     * Updates the QueryTree when a new item is learned. The leaf is replaced with a
     * new branch, of which the old leaf is now a child. Its sibling will be the new
     * item.
     * 
     * @param q the new question to ask.
     * @param item the new item to be added.
     * @param input the yes or no answer to the new question which leads to this item.
     * 
     * @throws IllegalArgumentException if 'y' or 'n' not passed to the character
     * parameter. Note this is not case-sensitive.
     * @throws IllegalStateException if a final guess has not been made.
     */
    public void updateTree(String q, String item, char input){
        input = Character.toLowerCase(input);
        if(hasNextQuestion()) throw new IllegalStateException();
        if(input != 'n' && input != 'y') throw new IllegalArgumentException();
        // Grows a new branch with the old and new answers as leafs.
        if(input == 'y'){
            Node newNode = new Node(currentNode.data);
            this.currentNode.no = newNode;           
            this.currentNode.yes = new Node(item);
            this.currentNode.data = q;
        } else {
            Node newNode = new Node(currentNode.data);
            this.currentNode.no = new Node(item);
            this.currentNode.yes = newNode;
            this.currentNode.data = q;
        }
        // Resets back to the starting state.
        this.currentNode = this.baseRoot;
    }
    
    /**
     * Reads in a new question/answer set, altering the state of this QueryTree.
     * This file should be in pre-ordered format. Files must be in the correct format
     * or the method will not function properly. Correct format is as follows:
     * Every odd line must either indicate if the node to be created is a question
     * "Q:" or answer "A:". The line following this must be the data to be stored in 
     * the node, either as the question to be asked or answer to be offered.
     * 
     * @param f the input file.
     * 
     * @throws IllegalStateException if a file is attempted to be read while the tree
     * is currently in a state of guessing.
     * @throws IllegalArgumentException if the file passed does not meet the specified
     * format.
     */
    public void readIn(File f) throws IOException{
        if(currentNode!= baseRoot) throw new IllegalStateException();
        if(!f.canRead()) throw new IOException("Cannot read source file.");
        Scanner input = new Scanner(f);
        try{
            this.baseRoot = buildTree(input, input.nextLine());
            this.currentNode = baseRoot;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
    }
    
    private Node buildTree(Scanner input, String node_type){
        Node node = null;
        node = new Node(input.nextLine());
        node_type = input.nextLine();
        // This checks to make sure the file handed to this script is in the correct
        // format. Otherwise the tree will end up as unuseable garbage.
        if(!node_type.equals("Q:") && !node_type.equals("A:")) throw new 
                IllegalArgumentException();
        if(node_type.equals("Q:")){
            node.no = buildTree(input, node_type);
        } else {
            node.no = new Node(input.nextLine());
        }
        node_type = input.nextLine();
        if(node_type.equals("Q:")){
            node.yes = buildTree(input, node_type);
        } else {
            node.yes = new Node(input.nextLine());
        }
        return node;
    }
    
    /**
     * Writes the current set of questions/answers in to the given output file. Does 
     * not alter the current state of this QueryTree. 
     * 
     * @param f the output file to be created.
     * 
     * @throws IOException if the output file cannot be written to.
     */
    public void writeOut(File f) throws IOException{
        try{
            f.canWrite();
            PrintStream output = new PrintStream(f);
            writeOut(output, baseRoot);
            output.close();
        } catch (IOException e){
            throw new IOException("Cannot write to specified file.");
        }    
    }
    
    private void writeOut(PrintStream output, Node node){
        if(node == null){
            return;
        }
        
        // Makes sure the save file created is in the correct format.
        if(node.no == null && node.yes == null){
            output.println("A:");
            output.println(node.data);
        } else {
            output.println("Q:");
            output.println(node.data);
        }
        
        // Pre-order processing.
        writeOut(output, node.no);
        writeOut(output, node.yes);
    }
    
    /**
     * Private inner class which constructs Node objects to create the binary tree
     * data structure. The fields of these nodes may be accessed directly by other
     * objects of the QueryTree class, no accessor is required.
     */
    private class Node{
        Node no;
        Node yes;
        String data;
        /**
         * Constructs a new Node.
         * 
         * @param yes the yes response to the question held here. This is the 
         * 'right' node.
         * @param no the no response to the question held here. This is the 
         * 'left' node.
         * @param data the question held, whether a final guess or a question to 
         * narrow the search. Only leaves should hold final guesses.
         */
        public Node(Node no, Node yes, String data){
            this.yes = yes;
            this.no = no;
            this.data = data;
        }
        
        /**
         * Constructs a new Node, with both right and left being null.
         * 
         * @param data the object to be guessed.
         */
        public Node(String data){
            this(null, null, data);
        }
    }
}