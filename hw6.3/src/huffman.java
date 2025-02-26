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
            throw new IllegalArgumentException("Alphabet and frequencies must not be empty");
        }
        
        this.alphabet = alphabet;
        this.frequencies = frequencies;
    }
}

interface ITree {
}

class Leaf implements ITree {
    String value;
    int frequency;

    Leaf(String value, int frequency) {
        this.value = value;
        this.frequency = frequency;
    }
}

class Node implements ITree {
    int totalFrequency;
    ITree left;
    ITree right;

    Node(int totalFrequency, ITree left, ITree right) {
        this.left = left;
        this.right = right;
    }
}