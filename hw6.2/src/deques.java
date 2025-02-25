import tester.*;

class Deque<T> {
  Sentinel<T> header;
  
  
}

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  
  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }
}

class Node<T> extends ANode<T> {
  T value;
   
  Node(T value, ANode<T> next, ANode<T> prev) {
    super(next, prev);
    this.value = value;
  }
}

class Sentinel<T> extends ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  
  Sentinel(ANode<T> next, ANode<T> prev) {
    super(next, prev);
  }
  
  Sentinel() {
    super(null, null);
    this.next = this;
    this.prev = this;
  }
}

class ExamplesDeques {
  Sentinel<String> s = new Sentinel<String>();
  
  void testConstructor(Tester t) {
    t.checkExpect(this.s.next, s);
  }
}
