import tester.*;

class Deque<T> {
  Sentinel<T> header;
  
  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  int size() {
    return header.numNodes();
  }
  
}

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  
  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }

  public abstract int numNodesHelp(int count);
}

class Node<T> extends ANode<T> {
  T value;
   
  Node(T value) {
    super(null, null);
    this.value = value;
  }

  Node(T value, ANode<T> next, ANode<T> prev) {
    super(next, prev);
    this.value = value;

    if (next == null || prev == null) { 
      throw new IllegalArgumentException("Cannot have null next or prev");
    } 

    // update other nodes
    next.prev = this;
    prev.next = this;
  }

  public int numNodesHelp(int count) {
    return this.next.numNodesHelp(count + 1);

  }
}

class Sentinel<T> extends ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  
  Sentinel(ANode<T> next, ANode<T> prev) {
    super(next, prev);
    
    // update other nodes
    next.prev = this;
    prev.next = this;
  }
  
  Sentinel() {
    super(null, null);
    this.next = this;
    this.prev = this;
  }

  int numNodes() {
    return this.next.numNodesHelp(0);
  }

  public int numNodesHelp(int count) {
    return count;

  }
}

class ExamplesDeques {
  Sentinel<String> s = new Sentinel<String>();

  Deque<String> deque1 = new Deque<String>(s); // empty list

  //////////////////////////////////////////////////

  Sentinel<String> start = new Sentinel<String>();
  Node<String> abc = new Node<String>("abc", start, start);
  Node<String> bcd = new Node<String>("bcd", start, abc);
  Node<String> cde = new Node<String>("cde", start, bcd);
  Node<String> def = new Node<String>("def", start, cde);
  
  Deque<String> deque2 = new Deque<String>(start); // abcdef list

  //////////////////////////////////////////////////
  
  Sentinel<String> cookies = new Sentinel<String>();
  Node<String> eggs = new Node<String>("eggs", cookies, cookies);
  Node<String> flour = new Node<String>("flour", cookies, eggs);
  Node<String> butter = new Node<String>("butter", cookies, flour);
  Node<String> sugar = new Node<String>("sugar", cookies, butter);
  Node<String> chocolate = new Node<String>("chocolate", cookies, sugar);
  
  Deque<String> deque3 = new Deque<String>(cookies); // cookie ingredient list
  
  void testConstructor(Tester t) {
    t.checkExpect(this.s.next, s) ;
    
    t.checkExpect(this.s.prev, s);

    t.checkConstructorExceptionType(IllegalArgumentException.class, "Node", "fail", null, null);
  }

  void testSize(Tester t) {
    t.checkExpect(this.deque2.header.next, abc);
    t.checkExpect(this.deque2.header.next.next, bcd);

    t.checkExpect(this.deque1.size(), 0) ;
    
    t.checkExpect(this.deque2.size(), 4) ;

    t.checkExpect(this.deque3.size(), 5) ;
  }
}
