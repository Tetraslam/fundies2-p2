import tester.*;

import java.lang.reflect.Array;
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

    public ArrayList<Boolean> encode(String message) {
        ArrayList<ATree> leaves = new ArrayList<>();
        for (int i = 0; i < alphabet.size(); i++) {
            leaves.add(new Leaf(alphabet.get(i), frequencies.get(i)));
        }

        ATree leavesToTree = new ArrayListUtils().sort(leaves);

        ArrayList<Boolean> encodedMessage = new ArrayList<>();
        for (int i = 0; i < message.length(); i++) {
            String letter = message.substring(i, i + 1);
            encodedMessage = new ArrayListUtils().addCode(new ArrayList<Boolean>(), letter, leavesToTree);
        }
        return encodedMessage;
    }
}

class ArrayListUtils {
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

    ATree getLowest(ArrayList<ATree> leaves) {
        ATree lowest = leaves.get(0);
        for (ATree leaf : leaves) {
            if (leaf.frequency < lowest.frequency) {
                lowest = leaf;
            }
        }
        return lowest;
    }

    ArrayList<Boolean> addCode(ArrayList<Boolean> encodedMessage, String letter, ATree tree) {
        return new ArrayListUtils().getPath(letter, tree, new ArrayList<Boolean>());
    }

    ArrayList<Boolean> getPath(String letter, ATree tree, ArrayList<Boolean> boolsSoFar) {
        return tree.findPath(letter, tree, boolsSoFar);
    }
    
}

abstract class ATree {
    int frequency;

    ATree(int frequency) {
        this.frequency = frequency;
    }

    public abstract ArrayList<Boolean> findPath(String letter, ATree tree, ArrayList<Boolean> boolsSoFar);

    public abstract boolean contains(String letter);
}

class Leaf extends ATree {
    String letter;

    Leaf(String letter, int frequency) {
        super(frequency);
        this.letter = letter;
    }

    public ArrayList<Boolean> findPath(String letter, ATree tree, ArrayList<Boolean> boolsSoFar) {
        if (this.letter.equals(letter)) {
            return boolsSoFar;
        }
        throw new IllegalArgumentException("Tried to encode " +  
        letter + "but that is not part of the language.");
    }

    public boolean contains(String letter) {
        return this.letter.equals(letter);
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

    public ArrayList<Boolean> findPath(String letter, ATree tree, ArrayList<Boolean> boolsSoFar) {
        if (this.left.contains(letter)) {
            boolsSoFar.add(false);
            return new ArrayListUtils().getPath(letter, this.left, boolsSoFar);
        }
        else if (this.right.contains(letter)) {
            boolsSoFar.add(true);
            return new ArrayListUtils().getPath(letter, this.right, boolsSoFar);
        }

        throw new IllegalArgumentException("Tried to encode " +  
        letter + "but that is not part of the language.");
    }

    public boolean contains(String letter) {
        return this.left.contains(letter) || this.right.contains(letter);
    }
}