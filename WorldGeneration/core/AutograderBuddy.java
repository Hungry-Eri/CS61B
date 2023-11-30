package core;

//import tileengine.TERenderer;
import edu.princeton.cs.algs4.In;
import tileengine.TETile;
import tileengine.Tileset;

public class AutograderBuddy {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        input = input.substring(1);
        String seedInput = input.replaceAll("[^0-9]", "");
        long seed = 0;
        if (seedInput.isEmpty()) {
            In fileread = new In("LastGame.txt");
            String getSaved = "";
            while (fileread.hasNextLine()) {
                getSaved = fileread.readLine();
            }

            seed = Long.parseLong(getSaved.replaceAll("[^0-9]", ""));
            World board = new World(seed, WIDTH, HEIGHT);

            String savedInput = getSaved.replaceAll("\\d", "");
            savedInput = savedInput.replaceAll("N", "");
            savedInput = savedInput.replaceAll(":Q", "");
            board.move(savedInput);

            String thisInput = input.replaceAll("\\d", "");
            board.move(thisInput);

            return board.returnBoard();
        }

        seed = Long.parseLong(seedInput);
        World board = new World(seed, WIDTH, HEIGHT);

        String thisInput = input.replaceAll("\\d", "");
        board.move(thisInput);

        return board.returnBoard();
    }

    /*
    // found on Stack Overflow
    static long stringToSeed(String s) {
        if (s == null) {
            return 0;
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L * hash + c;
        }
        return hash;
    }
    */

    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
