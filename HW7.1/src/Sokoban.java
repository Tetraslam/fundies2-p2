import tester.Tester;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import java.util.ArrayList;

// Represents cardinal directions
enum Direction {
  UP, DOWN, LEFT, RIGHT;

  // Returns a position offset based on this direction
  public Posn getOffset() {
    switch (this) {
      case UP:
        return new Posn(0, -1);
      case DOWN:
        return new Posn(0, 1);
      case LEFT:
        return new Posn(-1, 0);
      case RIGHT:
        return new Posn(1, 0);
      default:
        return new Posn(0, 0);
    }
  }

  // Returns the character representation for this direction
  public char toChar() {
    switch (this) {
      case UP:
        return '^';
      case DOWN:
        return 'v';
      case LEFT:
        return '<';
      case RIGHT:
        return '>';
      default:
        return ' ';
    }
  }

  // Creates a direction from a character
  public static Direction fromChar(char c) {
    switch (c) {
      case '^':
        return UP;
      case 'v':
        return DOWN;
      case '<':
        return LEFT;
      case '>':
        return RIGHT;
      default:
        throw new IllegalArgumentException("Invalid direction character: " + c);
    }
  }
}

// Represents colors for targets and trophies
enum GameColor {
  RED, GREEN, BLUE, YELLOW;

  // Returns the AWT color for this game color
  public Color toAWTColor() {
    if (this == RED) {
      return Color.RED;
    }
    else if (this == GREEN) {
      return Color.GREEN;
    }
    else if (this == BLUE) {
      return Color.BLUE;
    }
    else if (this == YELLOW) {
      return Color.YELLOW;
    }
    else {
      return Color.BLACK;
    }
  }

  // Returns the character representation for this color (uppercase for target)
  public char toTargetChar() {
    switch (this) {
      case RED:
        return 'R';
      case GREEN:
        return 'G';
      case BLUE:
        return 'B';
      case YELLOW:
        return 'Y';
      default:
        return ' ';
    }
  }

  // Returns the character representation for this color (lowercase for trophy)
  public char toTrophyChar() {
    switch (this) {
      case RED:
        return 'r';
      case GREEN:
        return 'g';
      case BLUE:
        return 'b';
      case YELLOW:
        return 'y';
      default:
        return ' ';
    }
  }

  // Creates a color from a target character
  public static GameColor fromTargetChar(char c) {
    switch (c) {
      case 'R':
        return RED;
      case 'G':
        return GREEN;
      case 'B':
        return BLUE;
      case 'Y':
        return YELLOW;
      default:
        throw new IllegalArgumentException("Invalid target color character: " + c);
    }
  }

  // Creates a color from a trophy character
  public static GameColor fromTrophyChar(char c) {
    switch (c) {
      case 'r':
        return RED;
      case 'g':
        return GREEN;
      case 'b':
        return BLUE;
      case 'y':
        return YELLOW;
      default:
        throw new IllegalArgumentException("Invalid trophy color character: " + c);
    }
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

  // Returns the character representation for this ground type
  char toChar();
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

  public char toChar() {
    return '_';
  }
}

// Represents a colored target
class Target implements IGroundType {
  GameColor color;

  Target(GameColor color) {
    this.color = color;
  }

  public WorldImage render() {
    return new OverlayImage(new CircleImage(15, OutlineMode.OUTLINE, color.toAWTColor()),
        new CircleImage(10, OutlineMode.SOLID, color.toAWTColor()));
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

  public char toChar() {
    return this.color.toTargetChar();
  }
}

// Represents a hole in the ground
class Hole implements IGroundType {
  public WorldImage render() {
    return new FromFileImage("src/hole.png");
    // Alternative if image not available:
    // return new OverlayImage(
    // new CircleImage(15, OutlineMode.SOLID, Color.BLACK),
    // new CircleImage(20, OutlineMode.OUTLINE, new Color(0, 200, 0))
    // );
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

  public char toChar() {
    return 'H';
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

  // Returns the character representation for this content
  char toChar();
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

  public char toChar() {
    return ' ';
  }
}

// Represents a wall
class Wall implements ICellContent {
  public WorldImage render() {
    return new FromFileImage("src/wall.png");
    // Alternative if image not available:
    // return new RectangleImage(40, 40, OutlineMode.SOLID, new Color(165, 42, 42));
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

  public char toChar() {
    return 'W';
  }
}

// Represents a crate
class Crate implements ICellContent {
  public WorldImage render() {
    return new FromFileImage("src/crate.png");
    // Alternative if image not available:
    // return new RectangleImage(30, 30, OutlineMode.SOLID, new Color(139, 69, 19));
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

  public char toChar() {
    return 'B';
  }
}

// Represents a trophy
class Trophy implements ICellContent {
  GameColor color;

  Trophy(GameColor color) {
    this.color = color;
  }

  public WorldImage render() {
    return new FromFileImage("src/" + this.color.toString().toLowerCase() + "_trophy.png");
    // Alternative if image not available:
    // return new OverlayImage(
    // new TextImage("🏆", 20, this.color.toAWTColor()),
    // new RectangleImage(30, 30, OutlineMode.SOLID, Color.WHITE)
    // );
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

  public char toChar() {
    return this.color.toTrophyChar();
  }
}

// Represents the player
class Player implements ICellContent {
  Direction facing;

  Player(Direction facing) {
    this.facing = facing;
  }

  public WorldImage render() {
    return new FromFileImage("src/player.png");
    // Alternative if image not available:
    // return new TextImage("🧍", 30, Color.BLACK);
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

  public char toChar() {
    return this.facing.toChar();
  }

  // Returns a new player facing the given direction
  public Player withDirection(Direction dir) {
    return new Player(dir);
  }
}

//…[imports and enums unchanged]…

//Represents a cell in the game
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
    return this.content instanceof EmptyContent;
  }

  // Returns true if this cell has a movable content
  boolean hasMovableContent() {
    return this.content.isMovable() && !this.hasTrophyOnMatchingTarget();
  }

  // Returns true if this cell contains the player
  boolean hasPlayer() {
    return this.content.isPlayer();
  }

  // Returns true if this cell contains a trophy
  boolean hasTrophy() {
    return this.content.isTrophy();
  }

  // Returns true if this cell has a matching trophy on target
  boolean hasTrophyOnMatchingTarget() {
    return this.ground.isTarget() && this.content.isTrophy()
        && this.ground.getColor() == this.content.getColor();
  }

  // Returns true if this cell is a hole
  boolean isHole() {
    return this.ground.isHole();
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

  // Constructs a level from ground and content strings
  SokobanLevel(String groundStr, String contentStr) {
    String[] groundRows = groundStr.split("\n");
    String[] contentRows = contentStr.split("\n");

    if (groundRows.length != contentRows.length) {
      throw new IllegalArgumentException("Ground and content must have same number of rows");
    }

    this.board = new ArrayList<>();
    this.playerPos = null;
    this.gameOver = false;
    this.playerFell = false;

    for (int y = 0; y < groundRows.length; y++) {
      String groundRow = groundRows[y];
      String contentRow = contentRows[y];

      if (groundRow.length() != contentRow.length()) {
        throw new IllegalArgumentException("Row " + y + " has mismatched lengths");
      }

      ArrayList<Cell> row = new ArrayList<>();

      for (int x = 0; x < groundRow.length(); x++) {
        char groundChar = groundRow.charAt(x);
        char contentChar = contentRow.charAt(x);

        IGroundType ground = charToGround(groundChar);
        ICellContent content = charToContent(contentChar);

        row.add(new Cell(ground, content));

        if (content.isPlayer()) {
          if (this.playerPos != null) {
            throw new IllegalArgumentException("Multiple players in level");
          }
          this.playerPos = new Posn(x, y);
        }
      }

      this.board.add(row);
    }

    if (this.playerPos == null) {
      throw new IllegalArgumentException("No player in level");
    }
  }

  // Converts a character to a ground type
  private IGroundType charToGround(char c) {
    switch (c) {
      case '_':
        return new EmptyGround();
      case 'H':
        return new Hole();
      case 'R':
        return new Target(GameColor.RED);
      case 'G':
        return new Target(GameColor.GREEN);
      case 'B':
        return new Target(GameColor.BLUE);
      case 'Y':
        return new Target(GameColor.YELLOW);
      default:
        throw new IllegalArgumentException("Invalid ground character: " + c);
    }
  }

  // Converts a character to a cell content
  private ICellContent charToContent(char c) {
    switch (c) {
      case ' ':
        return new EmptyContent();
      case '_':
        return new EmptyContent(); // Handle underscore as empty
      case 'W':
        return new Wall();
      case 'B':
        return new Crate();
      case 'r':
        return new Trophy(GameColor.RED);
      case 'g':
        return new Trophy(GameColor.GREEN);
      case 'b':
        return new Trophy(GameColor.BLUE);
      case 'y':
        return new Trophy(GameColor.YELLOW);
      case '^':
        return new Player(Direction.UP);
      case 'v':
        return new Player(Direction.DOWN);
      case '<':
        return new Player(Direction.LEFT);
      case '>':
        return new Player(Direction.RIGHT);
      default:
        throw new IllegalArgumentException("Invalid content character: " + c);
    }
  }

  // Gets the cell at the given position, or null if out of bounds
  public Cell getCell(Posn pos) {
    if (pos.y < 0 || pos.y >= this.board.size() || pos.x < 0
        || pos.x >= this.board.get(pos.y).size()) {
      return null;
    }
    return this.board.get(pos.y).get(pos.x);
  }

  // Sets the cell at the given position, returns true if successful
  private boolean setCell(Posn pos, Cell cell) {
    if (pos.y < 0 || pos.y >= this.board.size() || pos.x < 0
        || pos.x >= this.board.get(pos.y).size()) {
      return false;
    }
    this.board.get(pos.y).set(pos.x, cell);
    return true;
  }

  // Attempts to move the player in the given direction
  boolean movePlayer(Direction dir) {
    if (this.gameOver) {
      return false;
    }

    // Update player direction even if they can't move
    Cell playerCell = this.getCell(this.playerPos);
    if (playerCell != null && playerCell.content instanceof Player) {
      Player player = (Player) playerCell.content;
      this.setCell(this.playerPos, playerCell.withContent(player.withDirection(dir)));
    }

    // Calculate target positions
    Posn offset = dir.getOffset();
    Posn newPos = new Posn(this.playerPos.x + offset.x, this.playerPos.y + offset.y);
    Posn pushPos = new Posn(newPos.x + offset.x, newPos.y + offset.y);

    // Get cells at target positions
    Cell targetCell = this.getCell(newPos);
    Cell pushTargetCell = this.getCell(pushPos);

    // Check if movement is valid
    if (targetCell == null) {
      return false; // Out of bounds
    }

    if (targetCell.hasTrophyOnMatchingTarget()) {
      return this.movePlayerOntoMatchingTrophy(newPos);
    }

    if (targetCell.isEmpty()) {
      // Move to empty cell
      return this.movePlayerTo(newPos, targetCell);
    }
    else if (targetCell.hasMovableContent() && !targetCell.hasPlayer()) {
      // Try to push something
      if (pushTargetCell == null || !pushTargetCell.isEmpty()) {
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
  }

  // Helper: move player onto a cell with a matching trophy (without displacing it)
  private boolean movePlayerOntoMatchingTrophy(Posn newPos) {
    Cell playerCell = this.getCell(this.playerPos);
    if (playerCell == null || !playerCell.hasPlayer()) {
      return false;
    }
    // Remove the player from the old cell.
    this.setCell(this.playerPos, playerCell.withContent(new EmptyContent()));
    // Update player position.
    this.playerPos = newPos;
    this.checkWin();
    return true;
  }

  // Moves the player to the given position (and updates playerPos)
  private boolean movePlayerTo(Posn newPos, Cell targetCell) {
    Cell playerCell = this.getCell(this.playerPos);
    if (playerCell == null || !playerCell.hasPlayer()) {
      return false;
    }

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
  }

  // Checks if the level is won (all targets have matching trophies)
  private void checkWin() {
    for (ArrayList<Cell> row : this.board) {
      for (Cell cell : row) {
        if (cell.ground.isTarget()
            && (!cell.content.isTrophy() || cell.ground.getColor() != cell.content.getColor())) {
          return; // Found an unfilled target
        }
      }
    }
    this.gameOver = true;
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

  SokobanWorld(SokobanLevel level, int width, int height) {
    this.level = level;
    this.width = width;
    this.height = height;
  }

  @Override
  public WorldScene makeScene() {
    WorldScene scene = this.level.render(this.width, this.height);
    if (this.level.isGameOver()) {
      String message = this.level.isWon() ? "You Win!" : "Game Over!";
      scene.placeImageXY(new TextImage(message, 30, Color.BLACK), this.width / 2, this.height / 2);
    }
    return scene;
  }

  @Override
  public void onKeyEvent(String key) {
    Direction dir = null;
    switch (key) {
      case "up":
        dir = Direction.UP;
        break;
      case "down":
        dir = Direction.DOWN;
        break;
      case "left":
        dir = Direction.LEFT;
        break;
      case "right":
        dir = Direction.RIGHT;
        break;
      default:
        break;
    }
    if (dir != null) {
      this.level.movePlayer(dir);
    }
  }

}

// Main class for running Sokoban
class Sokoban {
  static String introLevelGround = "________\n" + "_R______\n" + "________\n" + "_B____G_\n"
      + "________\n" + "___Y____\n" + "________";

  static String introLevelContents = "__WWW___\n" + "__W>WW__\n" + "WWW__WWW\n" + "W_bg__rW\n"
      + "WWyWWWWW\n" + "_WW_W___\n" + "__WWW___";

  static String holeLevelGround = "________\n" + "________\n" + "__H_____\n" + "_RHHR___\n"
      + "________\n" + "________\n" + "________";

  static String holeLevelContents = "WWWWWWWW\n" + "W______W\n" + "W_>____W\n" + "W_r____W\n"
      + "W______W\n" + "WWWWWWWW\n" + "________";

  public static boolean runIntroLevel(Tester t) {
    SokobanLevel level = new SokobanLevel(introLevelGround, introLevelContents);
    SokobanWorld world = new SokobanWorld(level, 400, 400);
    world.bigBang(400, 400, 0.1);
    return true;
  }

  public static boolean runHoleLevel(Tester t) {
    SokobanLevel level = new SokobanLevel(holeLevelGround, holeLevelContents);
    SokobanWorld world = new SokobanWorld(level, 400, 400);
    world.bigBang(400, 400, 0.1);
    return true;
  }
}
