import tester.Tester;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import java.util.ArrayList;

// Utility methods that don't belong to a specific object
class Utils {
  // Converts a character to a ground type
  IGroundType charToGround(char c) {
    if (c == '_') {
      return new EmptyGround();
    }
    else if (c == 'H') {
      return new Hole();
    }
    else if (c == 'R') {
      return new Target(new GameColor("RED"));
    }
    else if (c == 'G') {
      return new Target(new GameColor("GREEN"));
    }
    else if (c == 'B') {
      return new Target(new GameColor("BLUE"));
    }
    else if (c == 'Y') {
      return new Target(new GameColor("YELLOW"));
    }
    else {
      throw new IllegalArgumentException("Invalid ground character: " + c);
    }
  }

  // Converts a character to a cell content
  ICellContent charToContent(char c) {
    if (c == ' ') {
      return new EmptyContent();
    }
    else if (c == '_') {
      return new EmptyContent(); // Handle underscore as empty
    }
    else if (c == 'W') {
      return new Wall();
    }
    else if (c == 'B') {
      return new Crate();
    }
    else if (c == 'r') {
      return new Trophy(new GameColor("RED"));
    }
    else if (c == 'g') {
      return new Trophy(new GameColor("GREEN"));
    }
    else if (c == 'b') {
      return new Trophy(new GameColor("BLUE"));
    }
    else if (c == 'y') {
      return new Trophy(new GameColor("YELLOW"));
    }
    else if (c == '^') {
      return new Player(new Direction("UP"));
    }
    else if (c == 'v') {
      return new Player(new Direction("DOWN"));
    }
    else if (c == '<') {
      return new Player(new Direction("LEFT"));
    }
    else if (c == '>') {
      return new Player(new Direction("RIGHT"));
    }
    else {
      throw new IllegalArgumentException("Invalid content character: " + c);
    }
  }
  
  // Custom split method to avoid using arrays
  ArrayList<String> customSplit(String str, char delimiter) {
    ArrayList<String> result = new ArrayList<String>();
    StringBuilder currentStr = new StringBuilder();
    
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c == delimiter) {
        result.add(currentStr.toString());
        currentStr = new StringBuilder();
      } else {
        currentStr.append(c);
      }
    }
    
    // Don't forget to add the last part
    result.add(currentStr.toString());
    
    return result;
  }
}

// Represents cardinal directions (replaces enum)
class Direction {
  String name;
  
  Direction(String name) {
    this.name = name;
  }
  
  // Returns a position offset based on this direction
  Posn getOffset() {
    if (this.name.equals("UP")) {
      return new Posn(0, -1);
    }
    else if (this.name.equals("DOWN")) {
      return new Posn(0, 1);
    }
    else if (this.name.equals("LEFT")) {
      return new Posn(-1, 0);
    }
    else if (this.name.equals("RIGHT")) {
      return new Posn(1, 0);
    }
    else {
      return new Posn(0, 0);
    }
  }
  
  // Needed for proper equality testing
  public boolean equals(Object obj) {
    if (!(obj instanceof Direction)) {
      return false;
    }
    
    return this.sameName((Direction)obj);
  }
  
  // Helper method to compare directions without casting in parameter
  boolean sameName(Direction other) {
    return this.name.equals(other.name);
  }
  
  // Always override hashCode when overriding equals
  public int hashCode() {
    return this.name.hashCode();
  }
}

// Represents colors for targets and trophies (replaces enum)
class GameColor {
  String name;
  
  GameColor(String name) {
    this.name = name;
  }

  // Returns the AWT color for this game color
  Color toAWTColor() {
    if (this.name.equals("RED")) {
      return Color.RED;
    }
    else if (this.name.equals("GREEN")) {
      return Color.GREEN;
    }
    else if (this.name.equals("BLUE")) {
      return Color.BLUE;
    }
    else if (this.name.equals("YELLOW")) {
      return Color.YELLOW;
    }
    else {
      return Color.BLACK;
    }
  }

  // Returns the character representation for this color (uppercase for target)
  char toTargetChar() {
    if (this.name.equals("RED")) {
      return 'R';
    }
    else if (this.name.equals("GREEN")) {
      return 'G';
    }
    else if (this.name.equals("BLUE")) {
      return 'B';
    }
    else if (this.name.equals("YELLOW")) {
      return 'Y';
    }
    else {
      return ' ';
    }
  }

  // Returns the character representation for this color (lowercase for trophy)
  char toTrophyChar() {
    if (this.name.equals("RED")) {
      return 'r';
    }
    else if (this.name.equals("GREEN")) {
      return 'g';
    }
    else if (this.name.equals("BLUE")) {
      return 'b';
    }
    else if (this.name.equals("YELLOW")) {
      return 'y';
    }
    else {
      return ' ';
    }
  }
  
  // Needed for proper equality testing
  public boolean equals(Object obj) {
    if (!(obj instanceof GameColor)) {
      return false;
    }
    
    return this.sameName((GameColor)obj);
  }
  
  // Helper method to compare colors without casting in parameter
  boolean sameName(GameColor other) {
    return this.name.equals(other.name);
  }
  
  // Always override hashCode when overriding equals
  public int hashCode() {
    return this.name.hashCode();
  }
  
  // Convert to lowercase for filename purposes
  public String toString() {
    return this.name.toLowerCase();
  }
}

// Represents a ground type in the game
interface IGroundType {
  // Renders this ground type as an image
  WorldImage render();

  // Returns true if this ground is a target
  boolean isTarget();

  // Returns the color of this ground if it's a target, null otherwise
  GameColor getColor();

  // Returns true if this ground is a hole
  boolean isHole();
  
  // Type checking methods
  boolean isEmptyGround();
  
  boolean isTargetGround();
  
  boolean isHoleGround();
}

// Represents an empty ground
class EmptyGround implements IGroundType {
  public WorldImage render() {
    return new RectangleImage(40, 40, OutlineMode.SOLID, Color.WHITE);
  }

  public boolean isTarget() {
    return false;
  }

  public GameColor getColor() {
    return null;
  }

  public boolean isHole() {
    return false;
  }
  
  public boolean isEmptyGround() {
    return true;
  }
  
  public boolean isTargetGround() {
    return false;
  }
  
  public boolean isHoleGround() {
    return false;
  }
}

// Represents a colored target
class Target implements IGroundType {
  GameColor color;

  Target(GameColor color) {
    this.color = color;
  }

  public WorldImage render() {
    return new OverlayImage(new CircleImage(15, OutlineMode.OUTLINE, this.color.toAWTColor()),
        new CircleImage(10, OutlineMode.SOLID, this.color.toAWTColor()));
  }

  public boolean isTarget() {
    return true;
  }

  public GameColor getColor() {
    return this.color;
  }

  public boolean isHole() {
    return false;
  }
  
  public boolean isEmptyGround() {
    return false;
  }
  
  public boolean isTargetGround() {
    return true;
  }
  
  public boolean isHoleGround() {
    return false;
  }
}

// Represents a hole in the ground
class Hole implements IGroundType {
  public WorldImage render() {
    return new ScaleImageXY(
      new FromFileImage("src/hole.png"),
      40.0 / 120.0,
      40.0 / 120.0
    );
  }

  public boolean isTarget() {
    return false;
  }

  public GameColor getColor() {
    return null;
  }

  public boolean isHole() {
    return true;
  }
  
  public boolean isEmptyGround() {
    return false;
  }
  
  public boolean isTargetGround() {
    return false;
  }
  
  public boolean isHoleGround() {
    return true;
  }
}

// Represents a content of a cell
interface ICellContent {
  // Renders this content as an image
  WorldImage render();

  // Returns true if this content can be moved/pushed
  boolean isMovable();

  // Returns true if this content is a trophy
  boolean isTrophy();

  // Returns the color of this content if it's a trophy, null otherwise
  GameColor getColor();

  // Returns true if this content is a player
  boolean isPlayer();
  
  // Type checking methods
  boolean isEmptyContent();
  
  boolean isWallContent();
  
  boolean isCrateContent();
  
  boolean isTrophyContent();
  
  boolean isPlayerContent();
}

// Represents empty cell content
class EmptyContent implements ICellContent {
  public WorldImage render() {
    return new EmptyImage();
  }

  public boolean isMovable() {
    return false;
  }

  public boolean isTrophy() {
    return false;
  }

  public GameColor getColor() {
    return null;
  }

  public boolean isPlayer() {
    return false;
  }
  
  public boolean isEmptyContent() {
    return true;
  }
  
  public boolean isWallContent() {
    return false;
  }
  
  public boolean isCrateContent() {
    return false;
  }
  
  public boolean isTrophyContent() {
    return false;
  }
  
  public boolean isPlayerContent() {
    return false;
  }
}

// Represents a wall
class Wall implements ICellContent {
  public WorldImage render() {
    return new ScaleImageXY(
      new FromFileImage("src/wall.png"),
      40.0 / 120.0,
      40.0 / 120.0
    );
  }

  public boolean isMovable() {
    return false;
  }

  public boolean isTrophy() {
    return false;
  }

  public GameColor getColor() {
    return null;
  }

  public boolean isPlayer() {
    return false;
  }
  
  public boolean isEmptyContent() {
    return false;
  }
  
  public boolean isWallContent() {
    return true;
  }
  
  public boolean isCrateContent() {
    return false;
  }
  
  public boolean isTrophyContent() {
    return false;
  }
  
  public boolean isPlayerContent() {
    return false;
  }
}

// Represents a crate
class Crate implements ICellContent {
  public WorldImage render() {
    return new ScaleImageXY(
      new FromFileImage("src/crate.png"),
      40.0 / 120.0,
      40.0 / 120.0
    );
  }

  public boolean isMovable() {
    return true;
  }

  public boolean isTrophy() {
    return false;
  }

  public GameColor getColor() {
    return null;
  }

  public boolean isPlayer() {
    return false;
  }
  
  public boolean isEmptyContent() {
    return false;
  }
  
  public boolean isWallContent() {
    return false;
  }
  
  public boolean isCrateContent() {
    return true;
  }
  
  public boolean isTrophyContent() {
    return false;
  }
  
  public boolean isPlayerContent() {
    return false;
  }
}

// Represents a trophy
class Trophy implements ICellContent {
  GameColor color;

  Trophy(GameColor color) {
    this.color = color;
  }

  public WorldImage render() {
    return new ScaleImageXY(
      new FromFileImage("src/" + this.color.toString() + "_trophy.png"),
      40.0 / 120.0,
      40.0 / 120.0
    );
  }

  public boolean isMovable() {
    return true;
  }

  public boolean isTrophy() {
    return true;
  }

  public GameColor getColor() {
    return this.color;
  }

  public boolean isPlayer() {
    return false;
  }
  
  public boolean isEmptyContent() {
    return false;
  }
  
  public boolean isWallContent() {
    return false;
  }
  
  public boolean isCrateContent() {
    return false;
  }
  
  public boolean isTrophyContent() {
    return true;
  }
  
  public boolean isPlayerContent() {
    return false;
  }
}

// Represents the player
class Player implements ICellContent {
  Direction facing;

  Player(Direction facing) {
    this.facing = facing;
  }

  public WorldImage render() {
    return new ScaleImageXY(
      new FromFileImage("src/player.png"),
      40.0 / 120.0,
      40.0 / 120.0
    );
  }

  public boolean isMovable() {
    return true;
  }

  public boolean isTrophy() {
    return false;
  }

  public GameColor getColor() {
    return null;
  }

  public boolean isPlayer() {
    return true;
  }

  // Returns a new player facing the given direction
  Player withDirection(Direction dir) {
    return new Player(dir);
  }
  
  public boolean isEmptyContent() {
    return false;
  }
  
  public boolean isWallContent() {
    return false;
  }
  
  public boolean isCrateContent() {
    return false;
  }
  
  public boolean isTrophyContent() {
    return false;
  }
  
  public boolean isPlayerContent() {
    return true;
  }
}

// Represents a cell in the game
class Cell {
  IGroundType ground;
  ICellContent content;

  Cell(IGroundType ground, ICellContent content) {
    this.ground = ground;
    this.content = content;
  }

  // Creates a copy of this cell
  Cell copy() {
    return new Cell(this.ground, this.content);
  }

  // Creates a copy with new content
  Cell withContent(ICellContent newContent) {
    return new Cell(this.ground, newContent);
  }

  // Creates a copy with new ground
  Cell withGround(IGroundType newGround) {
    return new Cell(newGround, this.content);
  }

  // Returns true if this cell is empty (no content)
  boolean isEmpty() {
    return this.content.isEmptyContent();
  }

  // Returns true if this cell has a movable content
  boolean hasMovableContent() {
    return this.content.isMovable();
  }

  // Returns true if this cell contains the player
  boolean hasPlayer() {
    return this.content.isPlayerContent();
  }

  // Returns true if this cell contains a trophy
  boolean hasTrophy() {
    return this.content.isTrophyContent();
  }

  // Returns true if this cell has a matching trophy on target
  boolean hasTrophyOnMatchingTarget() {
    return this.ground.isTarget() && this.content.isTrophyContent()
        && this.ground.getColor().equals(this.content.getColor());
  }

  // Returns true if this cell is a hole
  boolean isHole() {
    return this.ground.isHoleGround();
  }

  // Renders this cell as an image
  WorldImage render() {
    return new OverlayImage(this.content.render(), this.ground.render());
  }
}

// Represents a Sokoban level
class SokobanLevel {
  ArrayList<ArrayList<Cell>> board;
  Posn playerPos;
  boolean gameOver;
  boolean playerFell;
  Utils utils;

  // Constructs a level from ground and content strings
  SokobanLevel(String groundStr, String contentStr) {
    this.utils = new Utils();
    ArrayList<String> groundRows = this.utils.customSplit(groundStr, '\n');
    ArrayList<String> contentRows = this.utils.customSplit(contentStr, '\n');

    if (groundRows.size() != contentRows.size()) {
      throw new IllegalArgumentException("Ground and content must have same number of rows");
    }

    this.board = new ArrayList<ArrayList<Cell>>();
    this.playerPos = null;
    this.gameOver = false;
    this.playerFell = false;

    for (int y = 0; y < groundRows.size(); y++) {
      String groundRow = groundRows.get(y);
      String contentRow = contentRows.get(y);

      if (groundRow.length() != contentRow.length()) {
        throw new IllegalArgumentException("Row " + y + " has mismatched lengths");
      }

      ArrayList<Cell> row = new ArrayList<Cell>();

      for (int x = 0; x < groundRow.length(); x++) {
        char groundChar = groundRow.charAt(x);
        char contentChar = contentRow.charAt(x);

        IGroundType ground = this.utils.charToGround(groundChar);
        ICellContent content = this.utils.charToContent(contentChar);

        // If content is placed on a hole, it disappears (become empty)
        if (ground.isHoleGround() && content.isPlayerContent()) {
          content = new EmptyContent();
          this.playerFell = true;
          this.gameOver = true;
        }
        else if (ground.isHoleGround() && !content.isEmptyContent() && !content.isWallContent()) {
          content = new EmptyContent();
        }

        row.add(new Cell(ground, content));

        if (content.isPlayerContent()) {
          if (this.playerPos != null) {
            throw new IllegalArgumentException("Multiple players in level");
          }
          this.playerPos = new Posn(x, y);
        }
      }

      this.board.add(row);
    }

    if (this.playerPos == null && !this.playerFell) {
      throw new IllegalArgumentException("No player in level");
    }
  }

  // Gets the cell at the given position, throws error if out of bounds
  Cell getCell(Posn pos) {
    if (pos.y < 0 || pos.y >= this.board.size() || pos.x < 0
        || pos.x >= this.board.get(pos.y).size()) {
      throw new IllegalArgumentException("Position (" + pos.x + "," + pos.y + ") is out of bounds");
    }
    return this.board.get(pos.y).get(pos.x);
  }

  // Sets the cell at the given position, throws error if out of bounds
  void setCell(Posn pos, Cell cell) {
    if (pos.y < 0 || pos.y >= this.board.size() || pos.x < 0
        || pos.x >= this.board.get(pos.y).size()) {
      throw new IllegalArgumentException("Position (" + pos.x + "," + pos.y + ") is out of bounds");
    }
    this.board.get(pos.y).set(pos.x, cell);
  }

  // Attempts to move the player in the given direction
  boolean movePlayer(Direction dir) {
    if (this.gameOver || this.playerPos == null) {
      return false;
    }

    try {
      // Update player direction even if they can't move
      Cell playerCell = this.getCell(this.playerPos);
      if (playerCell.content.isPlayerContent()) {
        Player player = new Player(dir);
        this.setCell(this.playerPos, playerCell.withContent(player));
      }

      // Calculate target positions
      Posn offset = dir.getOffset();
      Posn newPos = new Posn(this.playerPos.x + offset.x, this.playerPos.y + offset.y);
      
      // Check if target position is within bounds
      if (this.isOutOfBounds(newPos)) {
        return false;
      }
      
      Cell targetCell = this.getCell(newPos);

      if (targetCell.hasTrophyOnMatchingTarget()) {
        return this.movePlayerOntoMatchingTrophy(newPos);
      }

      if (targetCell.isEmpty()) {
        // Move to empty cell
        return this.movePlayerTo(newPos, targetCell);
      }
      else if (targetCell.hasMovableContent()) {
        // Try to push something
        Posn pushPos = new Posn(newPos.x + offset.x, newPos.y + offset.y);
        
        // Check if push position is within bounds
        if (this.isOutOfBounds(pushPos)) {
          return false; // Can't push out of bounds
        }
        
        Cell pushTargetCell = this.getCell(pushPos);
        
        if (!pushTargetCell.isEmpty()) {
          return false; // Can't push because the space behind is blocked.
        }

        ICellContent pushedContent = targetCell.content;

        // Handle hole interaction for the pushed item:
        if (pushTargetCell.isHole()) {
          // The pushed item falls into the hole (both disappear)
          this.setCell(pushPos,
              pushTargetCell.withGround(new EmptyGround()).withContent(new EmptyContent()));
        }
        else {
          // Normal push: move the object into the free cell.
          this.setCell(pushPos, pushTargetCell.withContent(pushedContent));
        }

        // Now move the player into the target cell (where the object was),
        // updating the player's position as expected.
        return this.movePlayerTo(newPos, targetCell.withContent(new EmptyContent()));
      }
      return false;
    } catch (IllegalArgumentException e) {
      return false; // Cell access error
    }
  }
  
  // Helper method to check if a position is out of bounds
  boolean isOutOfBounds(Posn pos) {
    return (pos.y < 0 || pos.x < 0) || 
        (this.board.size() <= pos.y) || 
        (this.board.get(pos.y).size() <= pos.x);
  }

  // Helper: move player onto a cell with a matching trophy (without displacing it)
  boolean movePlayerOntoMatchingTrophy(Posn newPos) {
    try {
      Cell playerCell = this.getCell(this.playerPos);
      if (!playerCell.hasPlayer()) {
        return false;
      }
      // Remove the player from the old cell.
      this.setCell(this.playerPos, playerCell.withContent(new EmptyContent()));
      // Update player position.
      this.playerPos = newPos;
      this.checkWin();
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  // Moves the player to the given position (and updates playerPos)
  boolean movePlayerTo(Posn newPos, Cell targetCell) {
    try {
      Cell playerCell = this.getCell(this.playerPos);

      if (targetCell.isHole()) {
        // Player falls into hole, game over
        this.setCell(newPos,
            targetCell.withGround(new EmptyGround()).withContent(new EmptyContent()));
        this.setCell(this.playerPos, playerCell.withContent(new EmptyContent()));
        this.gameOver = true;
        this.playerFell = true;
        return true;
      }

      // Move player to target cell
      this.setCell(newPos, targetCell.withContent(playerCell.content));
      this.setCell(this.playerPos, playerCell.withContent(new EmptyContent()));
      this.playerPos = newPos;
      this.checkWin();
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  // Checks if the level is won (all targets have matching trophies)
  void checkWin() {
    boolean allTargetsFilled = true;
    
    for (int i = 0; i < this.board.size(); i++) {
      ArrayList<Cell> row = this.board.get(i);
      for (int j = 0; j < row.size(); j++) {
        Cell cell = row.get(j);
        if (cell.ground.isTarget()) {
          // If any target doesn't have a matching trophy, the game is not won
          if (!cell.content.isTrophyContent() ||
              !cell.ground.getColor().equals(cell.content.getColor())) {
            allTargetsFilled = false;
          }
        }
      }
    }
    
    if (allTargetsFilled) {
      this.gameOver = true;
    }
  }

  boolean isWon() {
    return this.gameOver && !this.playerFell;
  }

  boolean playerFell() {
    return this.playerFell;
  }

  boolean isGameOver() {
    return this.gameOver;
  }

  WorldScene render(int width, int height) {
    WorldScene scene = new WorldScene(width, height);
    for (int y = 0; y < this.board.size(); y++) {
      ArrayList<Cell> row = this.board.get(y);
      for (int x = 0; x < row.size(); x++) {
        Cell cell = row.get(x);
        scene.placeImageXY(cell.render(), x * 40 + 20, y * 40 + 20);
      }
    }
    return scene;
  }
}

// Represents the Sokoban game world
class SokobanWorld extends World {
  SokobanLevel level;
  int width;
  int height;
  DirectionFactory dirFactory;

  SokobanWorld(SokobanLevel level, int width, int height) {
    this.level = level;
    this.width = width;
    this.height = height;
    this.dirFactory = new DirectionFactory();
  }

  public WorldScene makeScene() {
    WorldScene scene = this.level.render(this.width, this.height);
    if (this.level.isGameOver()) {
      String message = this.level.isWon() ? "You Win!" : "Game Over!";
      scene.placeImageXY(new TextImage(message, 30, Color.BLACK), this.width / 2, this.height / 2);
    }
    return scene;
  }

  public void onKeyEvent(String key) {
    Direction dir = null;
    if (key.equals("up")) {
      dir = this.dirFactory.getUp();
    }
    else if (key.equals("down")) {
      dir = this.dirFactory.getDown();
    }
    else if (key.equals("left")) {
      dir = this.dirFactory.getLeft();
    }
    else if (key.equals("right")) {
      dir = this.dirFactory.getRight();
    }
    
    if (dir != null) {
      this.level.movePlayer(dir);
    }
  }
}

// Factory for Direction objects
class DirectionFactory {
  Direction up;
  Direction down;
  Direction left;
  Direction right;
  
  DirectionFactory() {
    this.up = new Direction("UP");
    this.down = new Direction("DOWN");
    this.left = new Direction("LEFT");
    this.right = new Direction("RIGHT");
  }
  
  Direction getUp() {
    return this.up;
  }
  
  Direction getDown() {
    return this.down;
  }
  
  Direction getLeft() {
    return this.left;
  }
  
  Direction getRight() {
    return this.right;
  }
}

// Factory for GameColor objects
class GameColorFactory {
  GameColor red;
  GameColor green;
  GameColor blue;
  GameColor yellow;
  
  GameColorFactory() {
    this.red = new GameColor("RED");
    this.green = new GameColor("GREEN");
    this.blue = new GameColor("BLUE");
    this.yellow = new GameColor("YELLOW");
  }
  
  GameColor getRed() {
    return this.red;
  }
  
  GameColor getGreen() {
    return this.green;
  }
  
  GameColor getBlue() {
    return this.blue;
  }
  
  GameColor getYellow() {
    return this.yellow;
  }
}

// Main class for running Sokoban
class Sokoban {
  String introLevelGround = "________\n" + "_R______\n" + "________\n" + "_B____G_\n"
      + "________\n" + "___Y____\n" + "________";

  String introLevelContents = "__WWW___\n" + "__W>WW__\n" + "WWW__WWW\n" + "W_bg__rW\n"
      + "WWyWWWWW\n" + "_WW_W___\n" + "__WWW___";

  String holeLevelGround = "________\n" + "________\n" + "__H_____\n" + "__HHR___\n"
      + "________\n" + "________\n" + "________";

  String holeLevelContents = "WWWWWWWW\n" + "W_>____W\n" + "W______W\n" + "W____r_W\n"
      + "W______W\n" + "WWWWWWWW\n" + "________";

  boolean runIntroLevel(Tester t) {
    SokobanLevel level = new SokobanLevel(this.introLevelGround, this.introLevelContents);
    SokobanWorld world = new SokobanWorld(level, 400, 400);
    world.bigBang(400, 400, 0.1);
    return true;
  }

  boolean runHoleLevel(Tester t) {
    SokobanLevel level = new SokobanLevel(this.holeLevelGround, this.holeLevelContents);
    SokobanWorld world = new SokobanWorld(level, 400, 400);
    world.bigBang(400, 400, 0.1);
    return true;
  }
}