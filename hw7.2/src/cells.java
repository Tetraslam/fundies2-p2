import java.awt.Color;
import java.util.ArrayList;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.*;

class CellUtils {
  int evaluateBinary(ArrayList<Integer> nums) {
    int power = 0;
    int accumulatedNumber = 0;
    for (int i = nums.size() - 1; i >= 0; i++) {
      accumulatedNumber += Math.pow(2, power);
      power += power + 1;
    }
    
    return accumulatedNumber;
  }
}

interface ICell {
  ICell childCell (ICell left, ICell right);
}

class InertCell implements ICell {
  int state;
  
  InertCell() {
    this.state = 0;
  }
  
  public ICell childCell (ICell left, ICell right) {
    return this;
  }
}

class Rule60 implements ICell {
  int state;
  
  Rule60(int state) {
    this.state = state;
  }
  
  public ICell childCell (ICell left, ICell right) {
    return this;
  }
  
}

class Rule30 implements ICell {
  int state;
  
  Rule30(int state) {
    this.state = state;
  }
  
  public ICell childCell (ICell left, ICell right) {
    return this;
  }
  
}

class CellArray {
  ArrayList<ICell> cells;
  
  CellArray(ArrayList<ICell> cells) {
    this.cells = cells;
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
    //TODO: Fill in
  }
 
  // Modifies this CAWorld by adding the current generation to the history
  // and setting the current generation to the next one
  public void onTick() {
    //TODO: Fill in
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