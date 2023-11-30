package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

public class World {

    // build your own world!
    private int gameWidth;
    private int gameHeight;
    private Random random;

    private ArrayList<ArrayList<Integer>> tracker;

    // Contains the tiles for the board
    private TETile[][] board;
    private int numRooms;
    private long seed;

    private int[] myAvatar;

    private String datasave;

    public World(long seed, int width, int height) {
        this.seed = seed;
        random = new Random(seed);

        board = new TETile[width][height];
        gameWidth = width;
        gameHeight = height;
        myAvatar = new int[2];
        myEncounter = new int[2];
        encounterCoord = new ArrayList<>();
        dupeCoord = new ArrayList<>();
        encounterHappening = false;
        finishedEncounter = false;

        datasave = "N" + seed;
        newWorld();
    }

    public void initializeboard() {
        tracker = new ArrayList<>();
        for (int x = 0; x < gameWidth; x++) {
            for (int y = 0; y < gameHeight; y++) {
                board[x][y] = Tileset.NOTHING;
            }
        }
    }

    public TETile[][] returnBoard() {
        return board;
    }

    public void createBoard() {
        for (int i = 0; i < numRooms; i++) {
            int randomizerX = random.nextInt(gameWidth);
            int randomizerY = random.nextInt(gameHeight);
            int randomizerW = random.nextInt(4, gameWidth / 4);
            int randomizerH = random.nextInt(4, gameHeight / 4);

            int cutoff = 0;
            while (!isValidRoom(randomizerX, randomizerY, randomizerW, randomizerH)) {
                if (cutoff < gameHeight) {
                    randomizerX = random.nextInt(gameWidth);
                    randomizerY = random.nextInt(gameHeight);
                    cutoff += 1;
                } else {
                    numRooms = i;
                    return;
                }
            }

            ArrayList<Integer> row = new ArrayList<>();
            row.add(randomizerX);
            row.add(randomizerY);
            row.add(randomizerW);
            row.add(randomizerH);
            tracker.add(row);

            createRoom(randomizerX, randomizerY, randomizerW, randomizerH);
        }
    }

    private boolean isValidRoom(int startX, int startY, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((startX + x) >= gameWidth || (startY + y) >= gameHeight) {
                    return false; //check no room tiles go oob
                }
                if (board[startX + x][startY + y] != Tileset.NOTHING) {
                    return false; //check every tile is unoccupied
                }
            }
        }
        return true;
    }

    // random number of rooms and hallways

    // room (of random width and height)
    private void createRoom(int startx, int starty, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                board[startx + x][starty + y] = Tileset.FLOOR;
            }
        }
    }


    /*
    * Keep track of where you would place tiles, then place after rooms-> hallways,
    * Helper to make room, then store x, y coordinate to connect hallways
    * Keep track of rooms being connected
    * Remove invalid objects
     */

    private void connectBoard() {
        for (int i = 0; i < numRooms - 1; i++) {
            int room1x = tracker.get(i).get(0);
            int room1y = tracker.get(i).get(1);
            int start1x = room1x + 1;
            int start1y = room1y + 1;

            int room2x = tracker.get(i + 1).get(0);
            int room2y = tracker.get(i + 1).get(1);
            int start2x = room2x + 1;
            int start2y = room2y + 1;

            createHall(start1x, start1y, start2x, start2y);
        }
    }

    // hallway (width = 1; random length)
    private void createHall(int startx, int starty, int endx, int endy) {
        makeHorizHallway(startx, endx, starty);
        makeVerticalHallway(starty, endy, endx);
    }

    // if ((s + x) >= BOARDWIDTH || (startcol + x) >= BOARDHEIGHT) {
    private void makeHorizHallway(int startx, int endx, int y) {
        for (int hallHeight = 0; hallHeight < 3; hallHeight++) {
            if (startx <= endx) {
                for (int x = startx; x <= endx; x++) {
                    board[x][y + hallHeight] = Tileset.FLOOR;
                }
            }
            if (startx > endx) {
                for (int x = endx; x <= startx; x++) {
                    board[x][y + hallHeight] = Tileset.FLOOR;
                }
            }
        }
    }
    private void makeVerticalHallway(int starty, int endy, int x) {
        for (int hallLength = 0; hallLength < 3; hallLength++) {
            if (starty <= endy) {
                for (int y = starty; y <= endy + 2; y++) {
                    board[x + hallLength][y] = Tileset.FLOOR;
                }
            }
            if (starty > endy) {
                for (int y = endy; y <= starty + 2; y++) {
                    board[x + hallLength][y] = Tileset.FLOOR;
                }
            }
        }
    }

    // walls and floors are visually distinct
    private void makeWall() {
        // turn any floor tile next to a NOTHING tile into a wall tile
        for (int x = 0; x < gameWidth; x++) {
            for (int y = 0; y < gameHeight; y++) {
                if (board[x][y] == Tileset.FLOOR) {
                    if (x == 0 || y == 0 || x == (gameWidth - 1) || y == (gameHeight - 1)) {
                        board[x][y] = Tileset.WALL;
                    } else {
                        if (board[x + 1][y] == Tileset.NOTHING
                                || board[x - 1][y] == Tileset.NOTHING
                                || board[x][y + 1] == Tileset.NOTHING
                                || board[x][y - 1] == Tileset.NOTHING) {
                            board[x][y] = Tileset.WALL;
                        }
                        if (board[x + 1][y] == Tileset.WALL
                                && board[x - 1][y] == Tileset.WALL
                                && board[x][y + 1] == Tileset.WALL
                                && board[x][y - 1] == Tileset.WALL) {
                            board[x][y] = Tileset.NOTHING;
                        }
                    }
                }
            }
        }


    }


    /*
    private boolean nextToNothing(int x, int y) {
        int width = board.length;
        int height = board[0].length;

        // Check if x is within bounds
        if (x > 0 && x < width - 1) {
            if (board[x + 1][y] == Tileset.FLOOR || board[x - 1][y] == Tileset.FLOOR) {
                return true;
            }
        }

        // Check if y is within bounds
        if (y > 0 && y < height - 1) {
            if (board[x][y + 1] == Tileset.FLOOR || board[x][y - 1] == Tileset.FLOOR) {
                return true;
            }
        }

        return false;
    }

    // all rooms should be reachable
    private static boolean roomsHaveHalls() {
        int count = 0;
        for (int i = 0; i < 5; i ++) {
            count++;
        }
        if (count == 4) {
            return true;
        }
        return false;
    }
    */

    // Avatar Stuff
    public void createAvatar() {
        int randomx = random.nextInt(gameWidth);
        int randomy = random.nextInt(gameHeight);
        while (board[randomx][randomy] != Tileset.FLOOR) {
            randomx = random.nextInt(gameWidth);
            randomy = random.nextInt(gameHeight);
        }

        board[randomx][randomy] = Tileset.AVATAR;
        myAvatar[0] = randomx;
        myAvatar[1] = randomy;
    }

    public int getAvatarY() {
        return myAvatar[1];
    }

    public int getAvatarX() {
        return myAvatar[0];
    }

    public void move(String input) {
        char previouschar = 'a';
        for (char direction : input.toCharArray()) {
            if (direction == 'W' || direction == 'w') {
                moveUp();
            } else if (direction == 'A' || direction == 'a') {
                moveLeft();
            } else if (direction == 'S' || direction == 's') {
                moveDown();
            } else if (direction == 'D' || direction == 'd') {
                moveRight();
            } else if (direction == 'Q' || direction == 'q') {
                if (previouschar == ':') {
                    saveToFile(datasave, "LastGame.txt");
                }
            } else if (direction == 'L' || direction == 'l') {
                openLoad();
            } else if (direction == 'N' || direction == 'n') {
                newWorld();
            }
            previouschar = direction;

            if (encounterHappening == true) {
                encounterinteraction();
            } else {
                runin();
            }
        }
    }

    public void moveUp() {
        datasave += 'W';
        if (myAvatar[1] < board[0].length - 1 && board[myAvatar[0]][myAvatar[1] + 1] != Tileset.WALL) {
            board[myAvatar[0]][myAvatar[1]] = Tileset.FLOOR;
            myAvatar[1]++;
            board[myAvatar[0]][myAvatar[1]] = Tileset.AVATAR;
        }
    }

    public void moveDown() {
        datasave += 'S';
        if (myAvatar[1] > 0 && board[myAvatar[0]][myAvatar[1] - 1] != Tileset.WALL) {
            board[myAvatar[0]][myAvatar[1]] = Tileset.FLOOR;
            myAvatar[1]--;
            board[myAvatar[0]][myAvatar[1]] = Tileset.AVATAR;
        }
    }

    public void moveLeft() {
        datasave += 'A';
        if (myAvatar[0] > 0 && board[myAvatar[0] - 1][myAvatar[1]] != Tileset.WALL) {
            board[myAvatar[0]][myAvatar[1]] = Tileset.FLOOR;
            myAvatar[0]--;
            board[myAvatar[0]][myAvatar[1]] = Tileset.AVATAR;
        }
    }

    public void moveRight() {
        datasave += 'D';
        if (myAvatar[0] < board.length - 1 && board[myAvatar[0] + 1][myAvatar[1]] != Tileset.WALL) {
            board[myAvatar[0]][myAvatar[1]] = Tileset.FLOOR;
            myAvatar[0]++;
            board[myAvatar[0]][myAvatar[1]] = Tileset.AVATAR;
        }
    }


    // Main Menu
    /*
        * (N) New game
        * Similar to your autograderBuddy, this will allow you to type in a seed and opens up a new world
        * (L) Load game
        * Loads the last game played, in its exact state (if you add more elements, those must also be the same)
        * (Q) Quit game
        * Immediately quits + and saves

     */

    public void newWorld() {
        numRooms = random.nextInt(5, gameHeight / 4);
        initializeboard();
        createBoard();
        connectBoard();
        makeWall();

        createAvatar();
        createEncounter();
        datasave += "N";
    }
    public void saveToFile(String data, String filePath) {
        data = data.replaceAll(":Q", "");
        data += ":Q";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openLoad() {
        In fileread = new In("LastGame.txt");
        String input = "";
        while (fileread.hasNextLine()) {
            input = fileread.readLine();
        }
        datasave = input;
        input = input.substring(1);
        input = input.replaceAll("L", "");
        input = input.replaceAll(":Q", "");

        this.seed =  Long.parseLong(input.replaceAll("[^0-9]", ""));
        random = new Random(seed);
        numRooms = random.nextInt(5, gameHeight / 4);
        initializeboard();
        createBoard();
        connectBoard();
        makeWall();

        createAvatar();
        createEncounter();
        String thisInput = input.replaceAll("\\d", "");
        thisInput = thisInput.substring(1);
        move(thisInput);
    }

    private void returnToWorld() {
        random = new Random(seed);
        numRooms = random.nextInt(5, gameHeight / 4);
        initializeboard();
        createBoard();
        connectBoard();
        makeWall();

        createAvatar();
    }

    // HUD Stuff
    private final int xcoordHUD = 45;
    private final int ycoordHUD = 48;
    public void renderTime() {
        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        int fontSize = 16;
        Font font = new Font("Arial",  Font.BOLD, fontSize);
        StdDraw.setFont();
        StdDraw.setPenColor(Color.white);
        StdDraw.text(xcoordHUD - 1, ycoordHUD, formattedDateTime);
        StdDraw.enableDoubleBuffering();
    }

    // include Text that describes the tile currently under the mouse pointer
    // This should not be flickering, if it flickers you won’t be able to receive credit.
    public void renderHUD() {
        String hudText = "Tile: ";
        if (board[(int) StdDraw.mouseX()][(int) StdDraw.mouseY()] == Tileset.WALL) {
            hudText += "Wall";
        } else if (board[(int) StdDraw.mouseX()][(int) StdDraw.mouseY()] == Tileset.FLOOR) {
            hudText += "Floor";
        } else if (board[(int) StdDraw.mouseX()][(int) StdDraw.mouseY()] == Tileset.AVATAR) {
            hudText += "You";
        } else if (board[(int) StdDraw.mouseX()][(int) StdDraw.mouseY()] == Tileset.NOTHING) {
            hudText += "Empty Space";
        } else if (board[(int) StdDraw.mouseX()][(int) StdDraw.mouseY()] == Tileset.FLOWER) {
            hudText += "Encounter!";
        }

        hudText += " (" + (int) StdDraw.mouseX() + ", " + (int) StdDraw.mouseY() + ")";

        StdDraw.setFont();
        StdDraw.setPenColor(Color.white);
        StdDraw.text(7, ycoordHUD, hudText);
        StdDraw.enableDoubleBuffering();
    }

    // Ambition 3C
    // Create a system so that the tile renderer only displays tiles on the screen that are within the line of sight
    // of the avatar. The line of sight must be able to be toggled on and off with a keypress


    private int numEncounter = 8;
    private int[] myEncounter;
    private int encounterXMin = 15;
    private int encounterXMax = 35;
    private int encounterYMin = 0;
    private int encounterYMax = 15;
    private ArrayList<int[]> encounterCoord;
    private ArrayList<int[]> dupeCoord;
    private boolean encounterHappening;
    private long startTime;
    private long elapsedTime;
    private boolean finishedEncounter;

    private void runin() {
        if (myAvatar[0] == myEncounter[0] && myAvatar[1] == myEncounter[1] && finishedEncounter == false) {
            encounters();
            instructionDisplay(1);

            encounterHappening = true;
            finishedEncounter = true;
            startTime = System.currentTimeMillis();
        }
    }
    private void encounters() {
        initializeboard();
        for (int x = encounterXMin; x < encounterXMax; x++) {
            for (int y = encounterYMin; y < encounterYMax; y++) {
                board[x][y] = Tileset.FLOOR;
            }
        }
        makeWall();

        myAvatar[0] = 25;
        myAvatar[1] = 5;
        board[25][5] = Tileset.AVATAR;

        for (int i = 0; i < numEncounter; i++) {
            int randomizerX = random.nextInt(encounterXMin + 1, encounterXMax - 1);
            int randomizerY = random.nextInt(encounterYMin + 1, encounterYMax - 1);
            board[randomizerX][randomizerY] = Tileset.FLOWER;
            int[] newECoord = new int [2];
            newECoord[0] = randomizerX;
            newECoord[1] = randomizerY;
            encounterCoord.add(newECoord);
            dupeCoord.add(newECoord);
        }
    }

    private void encounterinteraction() {
        elapsedTime = System.currentTimeMillis() - startTime;
        for (int[] tile : encounterCoord) {
            if (Arrays.equals(myAvatar, tile)) {
                numEncounter--;
                encounterCoord.remove(tile);
                break;
            }
        }

        if (elapsedTime > 10000) {
            numEncounter = 8;
            encounterHappening = true;
            instructionDisplay(3);
            board[myAvatar[0]][myAvatar[1]] = Tileset.FLOOR;
            myAvatar[0] = 25;
            myAvatar[1] = 5;
            board[25][5] = Tileset.AVATAR;
            recreateEncounter();
        } else if (numEncounter == 0) {
            numEncounter = 8;
            encounterHappening = false;
            encounterCoord = new ArrayList<>();
            instructionDisplay(2);
            returnToWorld();
        }
    }

    private void createEncounter() {
        int randomx = random.nextInt(encounterXMin + 1, encounterXMax);
        int randomy = random.nextInt(encounterYMin + 1, encounterYMax);
        while (board[randomx][randomy] != Tileset.FLOOR
                && board[randomx][randomy] != Tileset.FLOWER
                && board[randomx][randomy] != Tileset.AVATAR) {
            randomx = random.nextInt(encounterXMin + 1, encounterXMax);
            randomy = random.nextInt(encounterYMin + 1, encounterYMax);
        }

        board[randomx][randomy] = Tileset.FLOWER;
        myEncounter[0] = randomx;
        myEncounter[1] = randomy;
    }

    private void recreateEncounter() {
        for (int[] tile : dupeCoord) {
            board[tile[0]][tile[1]] = Tileset.FLOWER;
            if (!encounterCoord.contains(tile)) {
                encounterCoord.add(tile);
            }
        }
        startTime = System.currentTimeMillis();
    }

    private void instructionDisplay(int type) {
        String instruction = "";
        if (type == 1) {
            instruction = "You have 10 seconds to Collect all FLOWERS";
        } else if (type == 2) {
            instruction = "Good Job!";
        } else if (type == 3) {
            instruction = "Nice Try... Try Again!";
        }

        StdDraw.setPenColor(Color.PINK);
        int fontSize = 30;
        Font font = new Font("Arial", Font.PLAIN, fontSize);
        StdDraw.setFont(font);
        StdDraw.text(encounterXMin + 10, ycoordHUD, instruction);
        StdDraw.show();
        StdDraw.pause(2000);
    }
}

