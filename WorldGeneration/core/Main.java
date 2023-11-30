package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static long seed;
    private static World board;
    private static boolean isGameRunning = false;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        mainMenu();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char nextKey = StdDraw.nextKeyTyped();
                if (nextKey == 'n' || nextKey == 'N') {
                    loadWorld();
                    board = new World(seed, WIDTH, HEIGHT);
                    isGameRunning = true;
                    break;
                } else if (nextKey == 'l' || nextKey == 'L') {
                    resetBoardFromLoad();
                    isGameRunning = true;
                    break;
                } else if (nextKey == ':') {
                    previouschar = ':';
                } else if (previouschar == ':') {
                    if (nextKey == 'q') {
                        try (FileWriter writer = new FileWriter("LastGame.txt")) {
                            writer.write("");
                            System.exit(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    previouschar = 'a';
                }
            }
        }

        while (isGameRunning) {
            updateBoard();
            board.renderTime();
            board.renderHUD();
            StdDraw.show();
            ter.drawTiles(board.returnBoard());
        }
    }

    private static void mainMenu() {
        String gameName = "CS61B: The Game";
        int fontSize = 60;
        Font font = new Font("Arial",  Font.BOLD, fontSize);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(25, 30, gameName);

        String menuOption1 = "New Game (N)";
        String menuOption2 = "Load Game (L)";
        String menuOption3 = "Quit (:Q)";
        fontSize = 25;
        Font options = new Font("Arial",  Font.BOLD, fontSize);
        StdDraw.setFont(options);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(25, 20, menuOption1);
        StdDraw.text(25, 15, menuOption2);
        StdDraw.text(25, 10, menuOption3);

        StdDraw.show();
    }

    private static void loadWorld() {
        StdDraw.clear(Color.BLACK);

        String seedget = "Enter Seed: ";
        String start = "Press S to Start";

        int fontSize = 25;
        Font font = new Font("Arial",  Font.BOLD, fontSize);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);

        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.text(25, 30, seedget);
            StdDraw.text(25, 25, start);

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'S' || key == 's') {
                    seed = Long.parseLong(seedget.replaceAll("\\D", ""));
                    break;
                } else {
                    seedget += key;
                }
            }

            StdDraw.show();
            StdDraw.pause(20);
        }

        StdDraw.show();
    }

    private static char previouschar = 'a';
    private static void updateBoard() {
        if (StdDraw.hasNextKeyTyped()) {
            char nextKey = StdDraw.nextKeyTyped();

            if (nextKey == 'n') {
                board.newWorld();
            } else if (nextKey == 'l') {
                board.openLoad();
            } else if (nextKey == 'a') {
                board.move("A");
            } else if (nextKey == 's') {
                board.move("S");
            } else if (nextKey == 'd') {
                board.move("D");
            } else if (nextKey == 'w') {
                board.move("W");
            } else if (nextKey == ':') {
                previouschar = ':';
            } else if (previouschar == ':') {
                if (nextKey == 'q') {
                    board.move(":Q");
                }
                previouschar = 'a';
            }
        }
    }

    private static void resetBoardFromLoad() {
        In fileread = new In("LastGame.txt");
        String getSaved = "";
        while (fileread.hasNextLine()) {
            getSaved = fileread.readLine();
        }

        seed = Long.parseLong(getSaved.replaceAll("[^0-9]", ""));
        board = new World(seed, WIDTH, HEIGHT);

        String savedInput = getSaved.replaceAll("\\d", "");
        // savedInput = savedInput.replaceAll("N", "");
        savedInput = savedInput.substring(2);
        savedInput = savedInput.replaceAll(":Q", "");
        board.move(savedInput);
    }
}
