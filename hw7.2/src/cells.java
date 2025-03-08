import java.util.ArrayList;
import tester.*;
import javalib.worldimages.*;
import javalib.impworld.*; 
import java.awt.Color;

class CellUtils {
  // evaluates the binary representation of a number
  // starts at the end of the arraylist with that as 2^0
  public int evaluateBinary(ArrayList<Integer> nums) {
    int power = 0;
    int accumulatedNumber = 0;
    for (int i = nums.size() - 1; i >= 0; i--) {
      accumulatedNumber += Math.pow(2, power) * nums.get(i);
      power += 1;
    }
    
    return accumulatedNumber;
  }

  // log base 2 function
  public int log2(int n) {
    int result = (int) (Math.log(n) / Math.log(2));
    return result;
  }

  // returns the binary breakdown of a number
  // in the form of an arraylist of the powers of 2
  public ArrayList<Integer> binaryBreakdown(int num) {
    ArrayList<Integer> powers = new ArrayList<Integer>();

    int temp = num;
    while (temp > 0) {
      powers.add(new CellUtils().log2(temp));
      temp -= Math.pow(2, new CellUtils().log2(temp));
    }

    return powers;
  }

  // returns the state of a child cell given the number and the states of the neighbors
  public int childState(int num, int leftState, int centerState, int rightState) {
    // gets all the powers needed to represent the given number
    ArrayList<Integer> powers = new CellUtils().binaryBreakdown(num);
    
    // creates arraylist of integers to represent the binary value of the 3 cells
    // we're looking at, which represent the power of 2
    ArrayList<Integer> toEvaluate = new ArrayList<Integer>();
    toEvaluate.add(leftState);
    toEvaluate.add(centerState);  
    toEvaluate.add(rightState);

    // evaluates the binary value of the 3 cells to a base 10 value
    int evaluatedPower = new CellUtils().evaluateBinary(toEvaluate);
    
    // checks if the powers array contains the power evaluated from the cells
    if (powers.contains(evaluatedPower)) {
      return 1;
    } 
    else {
      return 0;
    }
  }
}

// interface to represent all cells and define behavior
interface ICell {
  // gets the state of this ICell
  int getState();
 
  // render this ICell as an image of a rectangle with this width and height
  WorldImage render(int width, int height);
   
  // produces the child cell of this ICell with the given left and right neighbors
  ICell childCell(ICell left, ICell right);
}

// abstract class for cells
abstract class ACell implements ICell {
  int state;
  
  ACell(int state) {
    this.state = state;
  }

  // gets the state
  public int getState() {
    return this.state;
  }

  // renders the cell as an image
  public WorldImage render(int width, int height) {
    if (this.state == 0) {
      return new RectangleImage(width, height, OutlineMode.SOLID, Color.WHITE);
    }
    else {
      return new RectangleImage(width, height, OutlineMode.SOLID, Color.BLACK);
    }
  }
  
  // returns the child cell
  public abstract ICell childCell(ICell left, ICell right);
}

// inert cell class, cell that always has a state 0
class InertCell extends ACell {
  
  InertCell() {
    super(0);
  }
  
  // returns this b/c an inert cell only produces inert cells as children
  public ICell childCell(ICell left, ICell right) {
    return this;
  }
}

// class to represent rule 60 of cells 
class Rule60 extends ACell {
  
  Rule60(int state) {
    super(state);
  }
  
  // returns the child cell using childState method in CellUtils
  public ICell childCell(ICell left, ICell right) {
    return new Rule60(new CellUtils().childState(60, left.getState(), 
    this.state, right.getState()));
  }
  
}

// class to represent rule 30 of cells
class Rule30 extends ACell {
  
  Rule30(int state) {
    super(state);
  }
  
  // returns the child cell using childState method in CellUtils
  public ICell childCell(ICell left, ICell right) {
    return new Rule30(new CellUtils().childState(30, left.getState(), 
    this.state, right.getState())); 
  }
  
}

// class to represent an array of cells, one generation of cells
class CellArray {
  ArrayList<ICell> cells;
  
  CellArray(ArrayList<ICell> cells) {
    this.cells = cells;
  }

  // returns the next generation of cells
  public CellArray nextGen() {
    // arraylist for new cells, which we use as field for the new CellArray
    ArrayList<ICell> newCells = new ArrayList<ICell>(); 

    // add inert cells to the beginning and end of the current arraylist of cells
    ArrayList<ICell> alteredCells = new ArrayList<ICell>();
    alteredCells.add(new InertCell());
    alteredCells.addAll(this.cells);
    alteredCells.add(new InertCell());
    
    for (int i = 1; i < alteredCells.size() - 1; i++) {
      newCells.add(alteredCells.get(i).childCell(alteredCells.get(i - 1), alteredCells.get(i + 1)));
    }
    return new CellArray(newCells);
  }

  // draws cell array, returns image
  public WorldImage draw(int cellWidth, int cellHeight) {
    WorldImage cells = new EmptyImage();
    for (ICell cell : this.cells) {
      cells = new BesideImage(cells, cell.render(cellWidth, cellHeight));
    }
    return cells;
  }
}

class CAWorld extends World {
 
  // constants
  static final int CELL_WIDTH = 10;
  static final int CELL_HEIGHT = 10;
  static final int INITIAL_OFF_CELLS = 20;
  static final int TOTAL_CELLS = INITIAL_OFF_CELLS * 2 + 1;
  static final int NUM_HISTORY = 41;
  static final int TOTAL_WIDTH = TOTAL_CELLS * CELL_WIDTH;
  static final int TOTAL_HEIGHT = NUM_HISTORY * CELL_HEIGHT;
 
  // the current generation of cells
  CellArray curGen;
  // the history of previous generations (earliest state at the start of the list)
  ArrayList<CellArray> history;
 
  // Constructs a CAWorld with INITIAL_OFF_CELLS of off cells on the left,
  // then one on cell, then INITIAL_OFF_CELLS of off cells on the right
  CAWorld(ICell off, ICell on) {
    ArrayList<ICell> cells = new ArrayList<ICell>();
    for (int i = 0; i < INITIAL_OFF_CELLS; i++) {
      cells.add(off);
    }

    cells.add(on);

    for (int i = 0; i < INITIAL_OFF_CELLS; i++) {
      cells.add(off);
    }

    this.curGen = new CellArray(cells);
    this.history = new ArrayList<CellArray>();
  }
 
  // Modifies this CAWorld by adding the current generation to the history
  // and setting the current generation to the next one
  public void onTick() {
    this.history.add(this.curGen);
    this.curGen = this.curGen.nextGen();
  }
 
  // Draws the current world, ``scrolling up'' from the bottom of the image
  public WorldImage makeImage() {
    // make a light-gray background image big enough to hold 41 generations of 41 cells each
    WorldImage bg = new RectangleImage(TOTAL_WIDTH, TOTAL_HEIGHT,
        OutlineMode.SOLID, new Color(240, 240, 240));
 
    // build up the image containing the past and current cells
    WorldImage cells = new EmptyImage();
    for (CellArray array : this.history) {
      cells = new AboveImage(cells, array.draw(CELL_WIDTH, CELL_HEIGHT));
    }
    cells = new AboveImage(cells, this.curGen.draw(CELL_WIDTH, CELL_HEIGHT));
 
    // draw all the cells onto the background
    return new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM,
        cells, 0, 0, bg);
  }
 
  public WorldScene makeScene() {
    WorldScene canvas = new WorldScene(TOTAL_WIDTH, TOTAL_HEIGHT);
    canvas.placeImageXY(this.makeImage(), TOTAL_WIDTH / 2, TOTAL_HEIGHT / 2);
    return canvas;
  }
}

class ExamplesCells {

  // test the evaluateBinary method
  void testEvaluateBinary(Tester t) {
    ArrayList<Integer> two = new ArrayList<Integer>();
    two.add(0);
    two.add(0);
    two.add(1);
    two.add(0);

    ArrayList<Integer> five = new ArrayList<Integer>();
    five.add(1);
    five.add(0);
    five.add(1);

    ArrayList<Integer> two2 = new ArrayList<Integer>();
    two2.add(0);
    two2.add(1);
    two2.add(0);

    t.checkExpect(new CellUtils().evaluateBinary(two), 2);
    t.checkExpect(new CellUtils().evaluateBinary(five), 5);
    t.checkExpect(new CellUtils().evaluateBinary(two2), 2);
  }

  // test log base 2 method
  void testLog2(Tester t) {
    t.checkExpect(new CellUtils().log2(2), 1);
    t.checkExpect(new CellUtils().log2(5), 2);
    t.checkExpect(new CellUtils().log2(7), 2);
  }

  // test binary breakdown method
  void testBinaryBreakdown(Tester t) {
    ArrayList<Integer> two = new ArrayList<Integer>();
    two.add(1);

    ArrayList<Integer> five = new ArrayList<Integer>();
    five.add(2);
    five.add(0);

    ArrayList<Integer> eight = new ArrayList<Integer>();
    eight.add(3);

    t.checkExpect(new CellUtils().binaryBreakdown(2), two);
    t.checkExpect(new CellUtils().binaryBreakdown(5), five);
    t.checkExpect(new CellUtils().binaryBreakdown(8), eight);
  }

  // test child state method
  void testChildState(Tester t) {
    t.checkExpect(new CellUtils().childState(60, 0, 0, 0), 0);
    t.checkExpect(new CellUtils().childState(60, 0, 0, 1), 0);
    t.checkExpect(new CellUtils().childState(60, 0, 1, 0), 1);
    t.checkExpect(new CellUtils().childState(60, 0, 1, 1), 1);
    t.checkExpect(new CellUtils().childState(60, 1, 0, 0), 1);
    t.checkExpect(new CellUtils().childState(60, 1, 0, 1), 1);
    t.checkExpect(new CellUtils().childState(60, 1, 1, 0), 0);
    t.checkExpect(new CellUtils().childState(60, 1, 1, 1), 0);
  }

  // test child cell method, if child cells produced have correct states
  void testChildCell(Tester t) {
    Rule60 zero = new Rule60(0);
    Rule60 one = new Rule60(1);
    t.checkExpect(zero.childCell(zero, zero).getState(), 0);
    t.checkExpect(zero.childCell(zero, one).getState(), 0);
    t.checkExpect(one.childCell(zero, zero).getState(), 1);
    t.checkExpect(one.childCell(zero, one).getState(), 1);
    t.checkExpect(zero.childCell(one, zero).getState(), 1);
    t.checkExpect(zero.childCell(one, one).getState(), 1);
    t.checkExpect(one.childCell(one, zero).getState(), 0);
    t.checkExpect(one.childCell(one, one).getState(), 0);
  }

  // test next gen method, sees if the next generation has correct states
  void testNextGen(Tester t) {
    ArrayList<ICell> cells = new ArrayList<ICell>();
    cells.add(new Rule60(0));
    cells.add(new Rule60(1));
    cells.add(new Rule60(0));
    CellArray ca = new CellArray(cells);
    CellArray nextGen = ca.nextGen();
    ArrayList<ICell> nextGenCells = nextGen.cells;
    t.checkExpect(nextGenCells.get(0).getState(), 0);
    t.checkExpect(nextGenCells.get(1).getState(), 1);
    t.checkExpect(nextGenCells.get(2).getState(), 1);
  }

  // test on tick method
  void testOnTick(Tester t) {
    CAWorld w = new CAWorld(new Rule60(0), new Rule60(1));
    w.onTick();
    t.checkExpect(w.history.size(), 1);
    t.checkExpect(w.curGen.cells.size(), 41);
    t.checkExpect(w.curGen.cells.get(20).getState(), 1);
    t.checkExpect(w.curGen.cells.get(21).getState(), 1);
  }

  // runs big bang
  void testBigBang(Tester t) {
    CAWorld w = new CAWorld(new Rule60(0), new Rule60(1));
    int worldWidth = w.TOTAL_WIDTH;
    int worldHeight = w.TOTAL_HEIGHT;
    double tickRate = 0.1;
    w.bigBang(worldWidth, worldHeight, tickRate);
  } 

}