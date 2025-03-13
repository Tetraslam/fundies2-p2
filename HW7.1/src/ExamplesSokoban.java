import tester.Tester;
import javalib.worldimages.*;
import java.util.ArrayList;

class ExamplesSokoban {
  // Factory for creating directions consistently
  DirectionFactory dirFactory = new DirectionFactory();
  // Factory for creating game colors consistently
  GameColorFactory colorFactory = new GameColorFactory();
  // Utils for parsing characters
  Utils utils = new Utils();

  // Example game elements
  EmptyGround emptyGround = new EmptyGround();
  Target redTarget = new Target(this.colorFactory.getRed());
  Target greenTarget = new Target(this.colorFactory.getGreen());
  Hole hole = new Hole();

  EmptyContent emptyContent = new EmptyContent();
  Wall wall = new Wall();
  Crate crate = new Crate();
  Trophy redTrophy = new Trophy(this.colorFactory.getRed());
  Player playerRight = new Player(this.dirFactory.getRight());

  // Example cells
  Cell emptyCell = new Cell(this.emptyGround, this.emptyContent);
  Cell wallCell = new Cell(this.emptyGround, this.wall);
  Cell crateCell = new Cell(this.emptyGround, this.crate);
  Cell trophyCell = new Cell(this.emptyGround, this.redTrophy);
  Cell playerCell = new Cell(this.emptyGround, this.playerRight);
  Cell holeCell = new Cell(this.hole, this.emptyContent);
  Cell trophyOnTargetCell = new Cell(this.redTarget, this.redTrophy);

  // Test small level strings
  String smallLevelGround = "___\n" + "_R_\n" + "___";
  String smallLevelContents = "W_W\n" + "Wr>\n" + "WWW";

  // Test level with holes
  String holeLevelGround = "___\n" + "_H_\n" + "___";
  String holeLevelContents = "WWW\n" + "W>W\n" + "WWW";

  // Test board construction
  void testBoardConstruction(Tester t) {
    SokobanLevel level = new SokobanLevel(this.smallLevelGround, this.smallLevelContents);
    t.checkExpect(level.board.size(), 3);
    t.checkExpect(level.board.get(0).size(), 3);
    t.checkExpect(level.playerPos, new Posn(2, 1));
    t.checkExpect(level.getCell(new Posn(0, 0)).content.isWallContent(), true);
    t.checkExpect(level.getCell(new Posn(1, 1)).ground.isTargetGround(), true);
    t.checkExpect(level.getCell(new Posn(1, 1)).content.isTrophyContent(), true);
  }

  // Test custom split method
  void testCustomSplit(Tester t) {
    ArrayList<String> result = this.utils.customSplit("hello\nworld\ntest", '\n');
    t.checkExpect(result.size(), 3);
    t.checkExpect(result.get(0), "hello");
    t.checkExpect(result.get(1), "world");
    t.checkExpect(result.get(2), "test");
    
    // Test with empty string
    ArrayList<String> emptyResult = this.utils.customSplit("", '\n');
    t.checkExpect(emptyResult.size(), 1);
    t.checkExpect(emptyResult.get(0), "");
    
    // Test with single delimiter
    ArrayList<String> singleDelimiter = this.utils.customSplit("\n", '\n');
    t.checkExpect(singleDelimiter.size(), 2);
    t.checkExpect(singleDelimiter.get(0), "");
    t.checkExpect(singleDelimiter.get(1), "");
    
    // Test with no delimiter
    ArrayList<String> noDelimiter = this.utils.customSplit("hello", '\n');
    t.checkExpect(noDelimiter.size(), 1);
    t.checkExpect(noDelimiter.get(0), "hello");
  }

  // Test basic player movement
  void testPlayerMovement(Tester t) {
    SokobanLevel level = new SokobanLevel(this.smallLevelGround, this.smallLevelContents);
    t.checkExpect(level.movePlayer(this.dirFactory.getDown()), false);
    t.checkExpect(level.playerPos, new Posn(2, 1));
    t.checkExpect(level.movePlayer(this.dirFactory.getRight()), false);
    t.checkExpect(level.playerPos, new Posn(2, 1));
    t.checkExpect(level.movePlayer(this.dirFactory.getLeft()), true);
    t.checkExpect(level.playerPos, new Posn(1, 1));
  }

  // Test pushing mechanics
  void testPushing(Tester t) {
    String pushLevelGround = "____\n" + "____\n" + "____";
    String pushLevelContents = "W__W\n" + "W>r_\n" + "WWWW";
    SokobanLevel level = new SokobanLevel(pushLevelGround, pushLevelContents);

    // Push the trophy right: expected to succeed,
    // with the trophy moving into the adjacent cell and the player moving into its
    // old cell.
    t.checkExpect(level.movePlayer(this.dirFactory.getRight()), true);
    t.checkExpect(level.playerPos, new Posn(2, 1));

    // Check the trophy moved (now at the push target cell)
    Cell pushCell = level.getCell(new Posn(3, 1));
    t.checkExpect(pushCell.content.isTrophyContent(), true);

    // Try to push the trophy into a wall (should fail)
    t.checkExpect(level.movePlayer(this.dirFactory.getRight()), false);
    t.checkExpect(level.playerPos, new Posn(2, 1));
  }

  // Test hole interactions
  void testHoleInteractions(Tester t) {
    String holePushLevelGround = "____\n" + "_H__\n" + "____";
    String holePushLevelContents = "____\n" + ">r__\n" + "____";
    SokobanLevel level = new SokobanLevel(holePushLevelGround, holePushLevelContents);
    t.checkExpect(level.movePlayer(this.dirFactory.getRight()), true);
    Cell holeCell = level.getCell(new Posn(1, 1));
    t.checkExpect(holeCell.ground.isEmptyGround(), true);
    t.checkExpect(holeCell.content.isEmptyContent(), true);

    String playerHoleLevelGround = "____\n" + "_H__\n" + "____";
    String playerHoleLevelContents = "____\n" + ">___\n" + "____";
    SokobanLevel playerHoleLevel = new SokobanLevel(playerHoleLevelGround, playerHoleLevelContents);
    t.checkExpect(playerHoleLevel.movePlayer(this.dirFactory.getRight()), true);
    t.checkExpect(playerHoleLevel.playerFell(), true);
    t.checkExpect(playerHoleLevel.isGameOver(), true);
    t.checkExpect(playerHoleLevel.isWon(), false);
  }

  // Test win condition
  void testWinCondition(Tester t) {
    String almostWonLevelGround = "___\n" + "_R_\n" + "___";
    String almostWonLevelContents = "___\n" + ">r_\n" + "___";
    SokobanLevel level = new SokobanLevel(almostWonLevelGround, almostWonLevelContents);
    t.checkExpect(level.isWon(), false);
    t.checkExpect(level.movePlayer(this.dirFactory.getRight()), true);
    t.checkExpect(level.isWon(), true);
    t.checkExpect(level.isGameOver(), true);
  }

  // Test out of bounds check
  void testIsOutOfBounds(Tester t) {
    SokobanLevel level = new SokobanLevel(this.smallLevelGround, this.smallLevelContents);
    t.checkExpect(level.isOutOfBounds(new Posn(-1, 0)), true);
    t.checkExpect(level.isOutOfBounds(new Posn(0, -1)), true);
    t.checkExpect(level.isOutOfBounds(new Posn(3, 0)), true);
    t.checkExpect(level.isOutOfBounds(new Posn(0, 3)), true);
    t.checkExpect(level.isOutOfBounds(new Posn(1, 1)), false);
  }

  // Test direction functionality
  void testDirection(Tester t) {
    Direction up = this.dirFactory.getUp();
    Direction down = this.dirFactory.getDown();
    Direction left = this.dirFactory.getLeft();
    Direction right = this.dirFactory.getRight();
    
    t.checkExpect(up.getOffset(), new Posn(0, -1));
    t.checkExpect(down.getOffset(), new Posn(0, 1));
    t.checkExpect(left.getOffset(), new Posn(-1, 0));
    t.checkExpect(right.getOffset(), new Posn(1, 0));
    
    // Test sameName method
    t.checkExpect(up.sameName(new Direction("UP")), true);
    t.checkExpect(up.sameName(down), false);
    
    // Test equals method
    t.checkExpect(up.equals(new Direction("UP")), true);
    t.checkExpect(up.equals(down), false);
    t.checkExpect(up.equals("UP"), false);
  }
  
  // Test game color functionality
  void testGameColor(Tester t) {
    GameColor red = this.colorFactory.getRed();
    GameColor green = this.colorFactory.getGreen();
    GameColor blue = this.colorFactory.getBlue();
    GameColor yellow = this.colorFactory.getYellow();
    
    t.checkExpect(red.toTargetChar(), 'R');
    t.checkExpect(green.toTrophyChar(), 'g');
    t.checkExpect(blue.toString(), "blue");
    t.checkExpect(yellow.toString(), "yellow");
    
    // Test sameName method
    t.checkExpect(red.sameName(new GameColor("RED")), true);
    t.checkExpect(red.sameName(blue), false);
    
    // Test equals method
    t.checkExpect(red.equals(new GameColor("RED")), true);
    t.checkExpect(red.equals(green), false);
    t.checkExpect(red.equals("RED"), false);
  }
  
  // Test cell functionality
  void testCell(Tester t) {
    Cell emptyCell = new Cell(this.emptyGround, this.emptyContent);
    Cell playerCell = new Cell(this.emptyGround, this.playerRight);
    Cell trophyCell = new Cell(this.emptyGround, this.redTrophy);
    Cell trophyOnTarget = new Cell(this.redTarget, this.redTrophy);
    
    t.checkExpect(emptyCell.isEmpty(), true);
    t.checkExpect(playerCell.isEmpty(), false);
    t.checkExpect(playerCell.hasPlayer(), true);
    t.checkExpect(trophyCell.hasTrophy(), true);
    t.checkExpect(trophyOnTarget.hasTrophyOnMatchingTarget(), true);
    
    // Test with methods
    Cell newContent = emptyCell.withContent(this.wall);
    t.checkExpect(newContent.content, this.wall);
    t.checkExpect(newContent.ground, this.emptyGround);
    
    Cell newGround = playerCell.withGround(this.hole);
    t.checkExpect(newGround.content, this.playerRight);
    t.checkExpect(newGround.ground, this.hole);
  }
  
  // Test utility methods
  void testUtils(Tester t) {
    t.checkExpect(this.utils.charToGround('_').isEmptyGround(), true);
    t.checkExpect(this.utils.charToGround('H').isHoleGround(), true);
    t.checkExpect(this.utils.charToGround('R').isTargetGround(), true);
    
    t.checkExpect(this.utils.charToContent(' ').isEmptyContent(), true);
    t.checkExpect(this.utils.charToContent('W').isWallContent(), true);
    t.checkExpect(this.utils.charToContent('r').isTrophyContent(), true);
    t.checkExpect(this.utils.charToContent('>').isPlayerContent(), true);
  }
  
  // Test player on hole at construction
  void testPlayerOnHoleAtConstruction(Tester t) {
    String levelGround = "___\n" + "_H_\n" + "___";
    String levelContents = "___\n" + "_>_\n" + "___";
    SokobanLevel level = new SokobanLevel(levelGround, levelContents);
    
    // Player should have disappeared into the hole
    t.checkExpect(level.playerFell, true);
    t.checkExpect(level.gameOver, true);
    t.checkExpect(level.playerPos, null);
    
    Cell holeCell = level.getCell(new Posn(1, 1));
    t.checkExpect(holeCell.ground.isHoleGround(), true);
    t.checkExpect(holeCell.content.isEmptyContent(), true);
  }
  
  // Test object on hole at construction
  void testObjectOnHoleAtConstruction(Tester t) {
    String levelGround = "___\n" + "_H_\n" + "___";
    String levelContents = "___\n" + "_r_\n" + "_>_"; // Added player at (1,2)
    SokobanLevel level = new SokobanLevel(levelGround, levelContents);
    
    // Trophy should have disappeared into the hole
    Cell holeCell = level.getCell(new Posn(1, 1));
    t.checkExpect(holeCell.ground.isHoleGround(), true);
    t.checkExpect(holeCell.content.isEmptyContent(), true);
    
    // Try with a crate
    String levelGround2 = "___\n" + "_H_\n" + "___";
    String levelContents2 = ">__\n" + "_B_\n" + "___"; // Added player at (0,0)
    SokobanLevel level2 = new SokobanLevel(levelGround2, levelContents2);
    
    Cell holeCell2 = level2.getCell(new Posn(1, 1));
    t.checkExpect(holeCell2.ground.isHoleGround(), true);
    t.checkExpect(holeCell2.content.isEmptyContent(), true);
  }
  
  // Test trophies on targets are movable (just checking the property)
  void testTrophyOnTargetIsMovable(Tester t) {
    // Create a level with a trophy on target
    String levelGround = "____\n" + "_R__\n" + "____\n" + "____";
    String levelContents = "____\n" + "_r__\n" + "_>__\n" + "____";
    SokobanLevel level = new SokobanLevel(levelGround, levelContents);
    
    // First verify the trophy is on its matching target
    Cell trophyCell = level.getCell(new Posn(1, 1));
    t.checkExpect(trophyCell.hasTrophyOnMatchingTarget(), true);
    
    // Verify the trophy's isMovable property returns true
    t.checkExpect(trophyCell.content.isMovable(), true);
  }
  
  // Additional test to verify trophy on target movability
  void testCanPushTrophyOnTarget(Tester t) {
    // Create a cell with a trophy on a target
    Cell cell = new Cell(this.redTarget, this.redTrophy);
    
    // This is the key test - verify the cell reports the trophy as movable
    t.checkExpect(cell.content.isMovable(), true);
    
    // And that it's properly identified as a trophy on matching target
    t.checkExpect(cell.hasTrophyOnMatchingTarget(), true);
  }

  // Run the intro level
  boolean testIntroLevel(Tester t) {
    Sokoban sokoban = new Sokoban();
    return sokoban.runIntroLevel(t);
  }

  // Run the hole level
  boolean testHoleLevel(Tester t) {
    Sokoban sokoban = new Sokoban();
    return sokoban.runHoleLevel(t);
  }
}