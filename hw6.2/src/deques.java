import tester.*;
import java.util.function.*;

// represents a 2 way list
class Deque<T> {
  Sentinel<T> header;
  
  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  // returns the number of nodes in the list
  int size() {
    return header.numNodes();
  }

  // inserts value at the front right after the header
  void addAtHead(T value) {
    this.header.addNodeToHead(value);
  }

  // inserts value at the end right before the header
  void addAtTail(T value) {
    this.header.addNodeToTail(value);
  }

  // removes the first node in the list and returns the item that has been removed
  T removeFromHead() {
    return this.header.removeFirstNode();
  }

  // removes the last node in the list and returns the item that has been removed
  T removeFromTail() {
    return this.header.removeLastNode();
  }

  // returns the first node that satisfies the predicate
  ANode<T> find(Predicate<T> pred) {
    return this.header.findNode(pred);
  }
  
  // EFFECT: removes the given node from the list
  void removeNode(ANode<T> node) {
    node.removeSelfVoid();
  }
}

// represents a Node or Sentinel
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  
  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }

  // helper method to count number of nodes in the list
  public abstract int numNodesHelp(int count);

  // removes this node from the list and returns what was removed
  public abstract T removeSelf();

  // changes this node's next to the given node
  public void changeNext(ANode<T> newNext) {
    this.next = newNext;
  }

  // changes this node's prev to the given node
  public void changePrev(ANode<T> newPrev) {
    this.prev = newPrev;
  }

  // helper method to find the node that satisfies the predicate
  public abstract ANode<T> findNodeHelper(Predicate<T> pred);

  // void method to remove this node from the list without throwing 
  // an error if it is the sentinel
  public abstract void removeSelfVoid();
}

// represents a Node in a list with a piece of data
class Node<T> extends ANode<T> {
  T data;
   
  Node(T data) {
    super(null, null);
    this.data = data;
  }

  Node(T value, ANode<T> next, ANode<T> prev) {
    super(next, prev);
    this.data = value;

    if (next == null || prev == null) { 
      throw new IllegalArgumentException("Cannot have null next or prev");
    } 

    // update other nodes
    next.prev = this;
    prev.next = this;
  }

  // accumulator to return the number of nodes in the list
  // increments by 1 for each node
  public int numNodesHelp(int count) {
    return this.next.numNodesHelp(count + 1);
  }
  
  // removes this node from the list and returns what was removed
  // changes the next and prev nodes to skip over this node
  public T removeSelf() {
    this.prev.changeNext(this.next);
    this.next.changePrev(this.prev);
    return this.data;
  }

  // helper method to find the node that satisfies the predicate
  // applies predicate to this node's data and returns the node if 
  // this node's data satisfies the predicate
  public ANode<T> findNodeHelper(Predicate<T> pred) {
    if (pred.test(this.data)) {
      return this;
    }
    else {
      return this.next.findNodeHelper(pred);
    }
  }

  // void method to remove this node from the list
  public void removeSelfVoid() {
    this.removeSelf();
  }
}

// represents a Sentinel in a list
class Sentinel<T> extends ANode<T> {
  Sentinel() {
    super(null, null);
    this.next = this;
    this.prev = this;
  }

  // gets the number of nodes in the list using an accumulator
  // starts count at 0
  int numNodes() {
    return this.next.numNodesHelp(0);
  }

  // returns number of nodes in the list once it reaches the sentinel which 
  // means it looped around and returned to the start (no more nodes left to count)
  public int numNodesHelp(int count) {
    return count;
  }

  // adds new node to head by creating a new node and setting its next and prev
  // to appropriate objects
  public void addNodeToHead(T value) {
    new Node<T>(value, this.next, this);
  }

  // adds new node to tail by creating a new node and setting its next and prev
  // to appropriate objects
  public void addNodeToTail(T value) {
    new Node<T>(value, this, this.prev);
  }

  // removes first node in the list
  public T removeFirstNode() {
    return this.next.removeSelf();
  }

  // removes last node in the list
  public T removeLastNode() {
    return this.prev.removeSelf();
  }
  
  // throws an error if you try to remove a sentinel
  public T removeSelf() {
    throw new RuntimeException("Cannot remove from an empty list");
  }

  // calls findNodeHelper to apply predicate to rest of the list containing data
  public ANode<T> findNode(Predicate<T> pred) {
    return this.next.findNodeHelper(pred);
  }

  // helper method for findNode to return the sentinel if we reach the end of the list
  // (looped back to the start) and no nodes satisfied the predicate
  public ANode<T> findNodeHelper(Predicate<T> pred) {
    return this;
  }

  // void method to remove a node, does nothing
  public void removeSelfVoid() {
    // should do nothing because the method that call's this method, removeNode()
    // should no nothing if called with a Sentinel to remove
  }

}

class ExamplesDeques {
  Sentinel<String> s;
  Deque<String> deque1;

  Sentinel<String> start;
  Node<String> abc;
  Node<String> bcd;
  Node<String> cde;
  Node<String> def;
  Deque<String> deque2;
  
  Sentinel<String> cookies = new Sentinel<String>();
  Node<String> eggs;
  Node<String> flour;
  Node<String> butter;
  Node<String> sugar;
  Node<String> chocolate;
  Deque<String> deque3;
  
  void init() {
    s = new Sentinel<String>();

    deque1 = new Deque<String>(s); // empty list
  
    //////////////////////////////////////////////////
  
    start = new Sentinel<String>();
    abc = new Node<String>("abc", start, start);
    bcd = new Node<String>("bcd", start, abc);
    cde = new Node<String>("cde", start, bcd);
    def = new Node<String>("def", start, cde);
    
    deque2 = new Deque<String>(start); // abcdef list
  
    //////////////////////////////////////////////////
    
    Sentinel<String> cookies = new Sentinel<String>();
    eggs = new Node<String>("eggs", cookies, cookies);
    flour = new Node<String>("flour", cookies, eggs);
    butter = new Node<String>("butter", cookies, flour);
    sugar = new Node<String>("sugar", cookies, butter);
    chocolate = new Node<String>("chocolate", cookies, sugar);
    deque3 = new Deque<String>(cookies); // cookie ingredient list
  }
  
  void testConstructor(Tester t) {
    init();
    t.checkExpect(this.s.next, s) ;
    
    t.checkExpect(this.s.prev, s);

    t.checkConstructorExceptionType(IllegalArgumentException.class, "Node", "fail", null, null);
  }

  void testSize(Tester t) {
    init();
    t.checkExpect(this.deque1.size(), 0);
    
    t.checkExpect(this.deque2.size(), 4);

    t.checkExpect(this.deque3.size(), 5);
  }

  void testAddToHead(Tester t) {
    init();
    t.checkExpect(deque1.size(), 0);
    
    deque1.addAtHead("head");
    t.checkExpect(deque1.size(), 1);
    Node<String> head = (Node<String>) deque1.header.next;
    t.checkExpect(head.data, "head");
    t.checkExpect(head.next, deque1.header);
    t.checkExpect(head.prev, deque1.header);
    t.checkExpect(deque1.header.prev, head);
    
    deque1.addAtHead("head2");
    t.checkExpect(deque1.size(), 2);
    Node<String> head2 = (Node<String>) deque1.header.next;
    t.checkExpect(head2.data, "head2");
    Node<String> second = (Node<String>) head2.next;
    t.checkExpect(second.data, "head");
    t.checkExpect(head2.prev, deque1.header);
    t.checkExpect(second.next, deque1.header);
    t.checkExpect(deque1.header.prev, second);
  }

  void testAddToTail(Tester t) {
    init();
    deque1.addAtTail("tail");
    t.checkExpect(deque1.size(), 1);
    t.checkExpect(deque1.header.prev, deque1.header.next);

    deque2.addAtTail("efg");
    t.checkExpect(deque2.size(), 5);
    t.checkExpect(deque2.header.prev, def.next);

    deque3.addAtTail("baking soda");
    t.checkExpect(deque3.size(), 6);
    t.checkExpect(deque3.header.prev, chocolate.next);
  }

  void testRemoves(Tester t) {
    init();
    t.checkExceptionType(RuntimeException.class, deque1, "removeFromHead");
    t.checkExceptionType(RuntimeException.class, deque1, "removeFromTail");

    t.checkExpect(deque2.removeFromHead(), "abc");
    t.checkExpect(deque2.removeFromTail(), "def");

    t.checkExpect(deque3.removeFromHead(), "eggs");
    t.checkExpect(deque3.removeFromTail(), "chocolate");
  }

  void testFind(Tester t) {
    init();

    class FindSugar implements Predicate<String> {
      public boolean test(String s) {
        return s.equals("sugar");
      }
    }

    class BeginsWithD implements Predicate<String> {
      public boolean test(String s) {
        return s.substring(0,1).equals("d");
      }
    }

    t.checkExpect(deque1.find(new FindSugar()), deque1.header);
    t.checkExpect(deque2.find(new FindSugar()), deque2.header);
    t.checkExpect(deque3.find(new FindSugar()), sugar);

    t.checkExpect(deque1.find(new BeginsWithD()), deque1.header);
    t.checkExpect(deque2.find(new BeginsWithD()), def);
    t.checkExpect(deque3.find(new BeginsWithD()), deque3.header);
  }

  void testRemoveNode(Tester t) {
    init();

    // test that there is no change when trying to remove the sentinel
    Deque<String> placeholder = deque1;
    deque1.removeNode(deque1.header);
    t.checkExpect(deque1, placeholder);

    Deque<String> placeholder2 = deque2;
    deque2.removeNode(start);
    t.checkExpect(deque2, placeholder2);

    // test that after removing bcd, there is no bcd in the list
    class Isbcd implements Predicate<String> {
      public boolean test(String s) {
        return s.equals("bcd");
      }
    }

    deque2.removeNode(bcd);
    t.checkExpect(deque2.size(), 3);
    t.checkExpect(deque2.find(new Isbcd()), start); // makes sure bcd is not in the list anymore
  }

}
