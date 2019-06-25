package minesweeper;

import java.io.IOException;
import java.util.Random;

public class Minesweeper {
    static public void main(String [] args) throws IOException {
        System.out.println("ArgNum(" + args.length + ")");
        for (String arg: args) {
            System.out.println(arg);
        }
        int width = Integer.parseUnsignedInt(args[0]);
        int height = Integer.parseUnsignedInt(args[1]);
        
        System.out.print("How many bombs: ");
        int numBombs = readIntFromStdIn();

        State state = new State(width, height, numBombs);
        state.printBoard(false);

        // User should type non-int to exit
        while (true) {
            System.out.print("Reveal X-coord: ");
            int xReveal = readIntFromStdIn();

            System.out.print("Reveal Y-coord: ");
            int yReveal = readIntFromStdIn();

            boolean exploded = state.revealSquare(xReveal, yReveal);
            if (exploded) {
                System.out.println("============== YOU LOSE ===============");
                state.printBoard(true);
                break;
            }
            if (state.checkWin()) {
                System.out.println("!!!!!!!!!!!!!! YOU WIN !!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!! YOU WIN !!!!!!!!!!!!!!!!");
                state.printBoard(true);
                System.out.println("!!!!!!!!!!!!!! YOU WIN !!!!!!!!!!!!!!!!");
                System.out.println("!!!!!!!!!!!!!! YOU WIN !!!!!!!!!!!!!!!!");
                break;
            }
            state.printBoard(false);
        }
    }

    static int readIntFromStdIn() throws IOException {
        StringBuilder input = new StringBuilder();
        int ch;
        while ((ch = System.in.read ()) != '\n') {
            if (Character.isWhitespace(ch)) {
                // Windows newline is \r\n
                continue;
            }
            input.append((char)ch);
        }
        return Integer.parseUnsignedInt(input.toString());
    }

    static class State {
        // Bombs are -2.
        // Unrevealed squares are -1
        // int >= 0 is visible.

        int [][] board;
        int numBombs;
        State(int width, int height, int numBombs) {
            this.board = new int[height][width];
            this.numBombs = numBombs;
            int numSquares = height * width;
            Random random = new Random();

            for (int i = 0; i < height; ++i) {
                for (int j = 0; j<width; ++j) {
                    int randNum = random.nextInt(numSquares);
                    if (randNum < numBombs) {  // place bomb
                        board[i][j] = -2;
                        --numBombs;
                    } else {
                        board[i][j] = -1;
                    }
                    --numSquares;
                }
            }
        }
        
        /**
         * return true if mine revealed.
         */
        public boolean revealSquare(int x, int y) {
            if (x < 0 || x >= board[0].length || y < 0 || y >= board.length) {
                // Outside range, ignore
                return false;
            }
            if (board[y][x] >= 0) { // already revealed
                return false;
            }
            if (board[y][x] == -2) { // KABOOM
                return true;
            }
            int numBombs =
                    checkSquare(x-1, y-1) +
                    checkSquare(x, y-1) +
                    checkSquare(x+1, y-1) +
                    checkSquare(x-1, y) +
                    checkSquare(x+1, y) +
                    checkSquare(x-1, y+1) +
                    checkSquare(x, y+1) +
                    checkSquare(x+1, y+1);
            board[y][x] = numBombs;

            // Bonus reveal if 0.
            if (numBombs == 0) {
                revealSquare(x-1, y-1);
                revealSquare(x, y-1);
                revealSquare(x+1, y-1);
                revealSquare(x-1, y);
                revealSquare(x+1, y);
                revealSquare(x-1, y+1);
                revealSquare(x, y+1);
                revealSquare(x+1, y+1);
            }
            return false;
        }
        
        int checkSquare(int x, int y) {
            if (x < 0 || x >= board[0].length || y < 0 || y >= board.length) {
                // Outside range, ignore
                return 0;
            }
            if (board[y][x] == -2) {
                return 1;
            } else {
                return 0;
            }
        }
        
        /**
         * @return true if the board is a winning game state.  (no -1);
         */
        public boolean checkWin() {
            for (int j = 0; j < board[0].length; ++j) {
                for (int i = 0; i < board.length; ++i) {
                    if(board[i][j] == -1) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        public void printBoard(boolean revealBombs) {
            System.out.println();
            
            // Print top header
            System.out.print("    ");
            for (int j = 0; j < board[0].length; ++j) {
                System.out.print(Integer.toString(j % 10) + " ");
            }
            System.out.println();
            for (int j = 0; j < board[0].length*2+4; ++j) {
                System.out.print("=");
            }
            System.out.println();
            
            // Print board line by line
            for (int i = 0; i < board.length; ++i) {
                System.out.print(" " + Integer.toString(i%10) + " |");
                for (int j = 0; j < board[0].length; ++j) {
                    int value = board[i][j];
                    if (value < 0) {
                        if (revealBombs && value == -2) {
                            System.out.print("* ");
                        } else {
                            System.out.print(". ");
                        }
                    } else {
                        System.out.print(value + " ");
                    }
                }
                System.out.println();
            }
        }
    }
}
