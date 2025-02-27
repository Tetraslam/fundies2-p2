import tester.*;
import java.util.ArrayList;

class Huffman {
  ArrayList<String> alphabet;
  ArrayList<Integer> frequencies;

  Huffman(ArrayList<String> alphabet, ArrayList<Integer> frequencies) {
    if (alphabet.size() != frequencies.size()) {
      throw new IllegalArgumentException("Alphabet and frequencies must be the same size");
    }

    else if (alphabet.size() < 2) {
      throw new IllegalArgumentException("Alphabet must be more than 2");
    }
    
    this.alphabet = alphabet;
    this.frequencies = frequencies;
  }

  // encodes the message into a list of booleans
  public ArrayList<Boolean> encode(String message) {
    // create a list of leaves to represent each letter in the alphabet and their frequencies
    ArrayList<ATree> leaves = new ArrayList<>();
    for (int i = 0; i < alphabet.size(); i++) {
      leaves.add(new Leaf(alphabet.get(i), frequencies.get(i)));
    }

    // sort the leaves into a tree
    ATree leavesToTree = new ArrayListUtils().sort(leaves);

    // encode the message
    ArrayList<Boolean> encodedMessage = new ArrayList<Boolean>();
    for (int i = 0; i < message.length(); i++) {
      String letter = message.substring(i, i + 1);
      encodedMessage = new ArrayListUtils().addCode(encodedMessage, letter, leavesToTree);
    }
    return encodedMessage;
  }
  
  // decodes a list of booleans into a message
  public String decode(ArrayList<Boolean> encodedMessage) {
    // If the encoded message is empty, return an empty string
    if (encodedMessage.isEmpty()) {
      return "";
    }
    
    // create a list of leaves to represent each letter in the alphabet and their frequencies
    ArrayList<ATree> leaves = new ArrayList<>();
    for (int i = 0; i < alphabet.size(); i++) {
      leaves.add(new Leaf(alphabet.get(i), frequencies.get(i)));
    }

    // sort the leaves into a tree
    ATree huffmanTree = new ArrayListUtils().sort(leaves);
    
    String decodedMessage = "";
    ATree currentNode = huffmanTree;
    
    for (int i = 0; i < encodedMessage.size(); i++) {
      Boolean bit = encodedMessage.get(i);
      
      if (currentNode.isLeaf()) {
        // We're at a leaf, add its letter to the result
        decodedMessage = decodedMessage + currentNode.getLetter();
        
        // Reset to root and process this same bit again
        currentNode = huffmanTree;
        i--; // Stay on same bit
      } else {
        // At an internal node, go left or right
        if (bit) {
          currentNode = currentNode.getRight();
        } else {
          currentNode = currentNode.getLeft();
        }
      }
    }
    
    // If we've processed all bits but are still at a leaf, add that letter
    if (currentNode.isLeaf()) {
      decodedMessage = decodedMessage + currentNode.getLetter();
    } 
    // If we're not at the root or a leaf, add "?" to indicate incomplete traversal
    else if (currentNode != huffmanTree) {
      decodedMessage = decodedMessage + "?";
    }
    
    return decodedMessage;
  }
}

// utils class to have any ArrayList methods needed
class ArrayListUtils {
  // returns the list of leaves as a single sorted node
  ATree sort(ArrayList<ATree> leaves) {
    while (leaves.size() != 1) {
      ATree lowest = new ArrayListUtils().getLowest(leaves);
      leaves.remove(lowest);
      ATree secondLowest = new ArrayListUtils().getLowest(leaves);
      leaves.remove(secondLowest);
      leaves.add(new Node(lowest.frequency + secondLowest.frequency, lowest, secondLowest));
    }
    return leaves.get(0);
  }

  // gets the lowest valued leaf in the list of leaves
  ATree getLowest(ArrayList<ATree> leaves) {
    ATree lowest = leaves.get(0);
    for (ATree leaf : leaves) {
      if (leaf.frequency < lowest.frequency) {
        lowest = leaf;
      }
    }
    return lowest;
  }

  // adds the code for the letter to the encoded message so far
  // uses helper method findPath to find the path to the letter through the tree
  ArrayList<Boolean> addCode(ArrayList<Boolean> encodedMessage, String letter, ATree tree) {
    return tree.findPath(letter, tree, encodedMessage);
  }
}

abstract class ATree {
  int frequency;

  ATree(int frequency) {
    this.frequency = frequency;
  }

  // finds path of letter through the tree and adds appropriate booleans to the list
  public abstract ArrayList<Boolean> findPath(String letter, ATree tree, 
      ArrayList<Boolean> boolsSoFar);

  // checks if the letter is in the tree
  public abstract boolean contains(String letter);
  
  // Is this a leaf node?
  public abstract boolean isLeaf();
  
  // Get the letter (only valid for leaf nodes)
  public abstract String getLetter();
  
  // Get left child (only valid for nodes)
  public abstract ATree getLeft();
  
  // Get right child (only valid for nodes)
  public abstract ATree getRight();
}

class Leaf extends ATree {
  String letter;

  Leaf(String letter, int frequency) {
    super(frequency);
    this.letter = letter;
  }

  // base case for finding the path of the letter through the tree
  // once we reach the actual letter, if it is contained in the tree, we return the list of booleans
  public ArrayList<Boolean> findPath(String letter, ATree tree, ArrayList<Boolean> boolsSoFar) {
    if (this.letter.equals(letter)) {
      return boolsSoFar;
    }
    else {
      throw new IllegalArgumentException("Tried to encode " +  
          letter + " but that is not part of the language.");
    }
  }

  // returns if this leaf contains the letter
  public boolean contains(String letter) {
    return this.letter.equals(letter);
  }
  
  // Yes, this is a leaf
  public boolean isLeaf() {
    return true;
  }
  
  // Get the letter from this leaf
  public String getLetter() {
    return this.letter;
  }
  
  // These methods should never be called on a leaf
  public ATree getLeft() {
    throw new UnsupportedOperationException("Cannot get left child of a leaf");
  }
  
  public ATree getRight() {
    throw new UnsupportedOperationException("Cannot get right child of a leaf");
  }
}

class Node extends ATree {
  ATree left;
  ATree right;

  Node(int frequency, ATree left, ATree right) {
    super(frequency);
    this.left = left;
    this.right = right;
  }

  // finds the path of the letter through the tree
  public ArrayList<Boolean> findPath(String letter, ATree tree, ArrayList<Boolean> boolsSoFar) {
    if (this.left.contains(letter)) {
      boolsSoFar.add(false);
      return new ArrayListUtils().addCode(boolsSoFar, letter, this.left);
    }
    else if (this.right.contains(letter)) {
      boolsSoFar.add(true);
      return new ArrayListUtils().addCode(boolsSoFar, letter, this.right);
    }

    throw new IllegalArgumentException("Tried to encode " +  
        letter + " but that is not part of the language.");
  }

  // returns if the letter is in the left or right part of the node
  public boolean contains(String letter) {
    return this.left.contains(letter) || this.right.contains(letter);
  }
  
  // No, this is not a leaf
  public boolean isLeaf() {
    return false;
  }
  
  // This method should never be called on a node
  public String getLetter() {
    throw new UnsupportedOperationException("Cannot get letter from a node");
  }
  
  // Get the left child
  public ATree getLeft() {
    return this.left;
  }
  
  // Get the right child
  public ATree getRight() {
    return this.right;
  }
}

class ExamplesHuffman {
  void testConstructor(Tester t) {
    ArrayList<String> fundies = new ArrayList<String>();
    fundies.add("f");
    fundies.add("u");
    fundies.add("n");
    fundies.add("d");
    fundies.add("i");
    fundies.add("e");
    fundies.add("s");
    
    ArrayList<Integer> shortListInts = new ArrayList<Integer>();
    shortListInts.add(1);
    shortListInts.add(1);
    
    ArrayList<String> oneString = new ArrayList<String>();
    oneString.add("r");

    t.checkConstructorExceptionType(IllegalArgumentException.class, "Huffman", fundies, 
        shortListInts);
    
    t.checkConstructorExceptionType(IllegalArgumentException.class, "Huffman", oneString, 
        shortListInts);
  }

  ArrayList<String> better;
  ArrayList<Integer> betterNums;
  ArrayList<Boolean> result;
  ArrayList<Boolean> badResult;
  Huffman h;
  
  void init() {
    better = new ArrayList<String>();
    better.add("b");
    better.add("e");
    better.add("t");
    better.add("r");

    betterNums = new ArrayList<Integer>();
    betterNums.add(1);
    betterNums.add(2);
    betterNums.add(2);
    betterNums.add(1); 

    result = new ArrayList<Boolean>();
    result.add(false);
    result.add(false);
    result.add(true);
    result.add(false);
    result.add(true);
    result.add(true);
    result.add(true);
    result.add(true);
    result.add(true);
    result.add(false);
    result.add(false);
    result.add(true);

    badResult = new ArrayList<Boolean>();
    badResult.add(false);
    badResult.add(false);
    badResult.add(true);
    
    h = new Huffman(better, betterNums);
  }
  
  void testBetterEncode(Tester t) {
    init();
    t.checkExpect(h.encode("better"), result);
  }
  
  void testBetterDecode(Tester t) {
    init();
    t.checkExpect(h.decode(badResult), "b?");
    t.checkExpect(h.decode(result), "better");
    
    // Test case for a complex decode pattern
    ArrayList<Boolean> complexPattern = new ArrayList<Boolean>();
    complexPattern.add(true);  // right - t
    complexPattern.add(true);  // right - r
    complexPattern.add(false); // left - e
    complexPattern.add(false); // left - b
    
    t.checkExpect(h.decode(complexPattern), "trb?");
    
    // Test with empty encoded message
    ArrayList<Boolean> emptyEncoded = new ArrayList<Boolean>();
    t.checkExpect(h.decode(emptyEncoded), "");
    
    // Test with just one bit that decodes to a complete character (should NOT have "?")
    ArrayList<Boolean> oneBit = new ArrayList<Boolean>();
    oneBit.add(false); // This should decode to "b" with our tree
    t.checkExpect(h.decode(oneBit), "b");
    
    // Test with a sequence of bits that decode to multiple characters
    ArrayList<Boolean> multiChars = new ArrayList<Boolean>();
    multiChars.add(false); // b
    multiChars.add(true);  // t
    multiChars.add(true);  // r
    t.checkExpect(h.decode(multiChars), "btr");
  }

  void testBetterEncodeError(Tester t) {
    init();
    t.checkException(new IllegalArgumentException("Tried to encode u but that is "
        + "not part of the language."), h, "encode", "butter");
  }
}