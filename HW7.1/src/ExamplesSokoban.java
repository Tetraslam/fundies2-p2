import tester.Tester;
import javalib.worldimages.*;

class ExamplesSokoban {
  // Example game elements
  EmptyGround emptyGround = new EmptyGround();
  Target redTarget = new Target(GameColor.RED);
  Target greenTarget = new Target(GameColor.GREEN);
  Hole hole = new Hole();

  EmptyContent emptyContent = new EmptyContent();
  Wall wall = new Wall();
  Crate crate = new Crate();
  Trophy redTrophy = new Trophy(GameColor.RED);
  Player playerRight = new Player(Direction.RIGHT);

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
    SokobanLevel level = new SokobanLevel(smallLevelGround, smallLevelContents);
    t.checkExpect(level.board.size(), 3);
    t.checkExpect(level.board.get(0).size(), 3);
    t.checkExpect(level.playerPos, new Posn(2, 1));
    t.checkExpect(level.getCell(new Posn(0, 0)).content instanceof Wall, true);
    t.checkExpect(level.getCell(new Posn(1, 1)).ground instanceof Target, true);
    t.checkExpect(level.getCell(new Posn(1, 1)).content instanceof Trophy, true);
  }

  // Test basic player movement
  void testPlayerMovement(Tester t) {
    SokobanLevel level = new SokobanLevel(smallLevelGround, smallLevelContents);
    t.checkExpect(level.movePlayer(Direction.DOWN), false);
    t.checkExpect(level.playerPos, new Posn(2, 1));
    t.checkExpect(level.movePlayer(Direction.RIGHT), false);
    t.checkExpect(level.playerPos, new Posn(2, 1));
    t.checkExpect(level.movePlayer(Direction.LEFT), true);
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
    t.checkExpect(level.movePlayer(Direction.RIGHT), true);
    t.checkExpect(level.playerPos, new Posn(2, 1));

    // Check the trophy moved (now at the push target cell)
    Cell pushCell = level.getCell(new Posn(3, 1));
    t.checkExpect(pushCell.content instanceof Trophy, true);

    // Try to push the trophy into a wall (should fail)
    t.checkExpect(level.movePlayer(Direction.RIGHT), false);
    t.checkExpect(level.playerPos, new Posn(2, 1));
  }

  // Test hole interactions
  void testHoleInteractions(Tester t) {
    String holePushLevelGround = "____\n" + "_H__\n" + "____";
    String holePushLevelContents = "____\n" + ">r__\n" + "____";
    SokobanLevel level = new SokobanLevel(holePushLevelGround, holePushLevelContents);
    t.checkExpect(level.movePlayer(Direction.RIGHT), true);
    Cell holeCell = level.getCell(new Posn(1, 1));
    t.checkExpect(holeCell.ground instanceof EmptyGround, true);
    t.checkExpect(holeCell.content instanceof EmptyContent, true);

    String playerHoleLevelGround = "____\n" + "_H__\n" + "____";
    String playerHoleLevelContents = "____\n" + ">___\n" + "____";
    SokobanLevel playerHoleLevel = new SokobanLevel(playerHoleLevelGround, playerHoleLevelContents);
    t.checkExpect(playerHoleLevel.movePlayer(Direction.RIGHT), true);
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
    t.checkExpect(level.movePlayer(Direction.RIGHT), true);
    t.checkExpect(level.isWon(), true);
    t.checkExpect(level.isGameOver(), true);
  }

  // Run the game
  boolean testGame(Tester t) {
    return Sokoban.runIntroLevel(t);
  }

  // Run the hole level
  boolean testHoleLevel(Tester t) {
    return Sokoban.runHoleLevel(t);
  }
}
