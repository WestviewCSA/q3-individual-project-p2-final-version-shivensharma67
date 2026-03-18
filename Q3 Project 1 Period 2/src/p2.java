import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class p2{

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalCommandLineInputsException("Not enough arguments.");
        }

        boolean useStack = false;
        boolean useQueue = false;
        boolean useOpt = false;
        boolean useTime = false;
        boolean inCoord = false;
        boolean outCoord = false;

        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--Stack")) useStack = true;
            else if (args[i].equals("--Queue")) useQueue = true;
            else if (args[i].equals("--Opt")) useOpt = true;
            else if (args[i].equals("--Time")) useTime = true;
            else if (args[i].equals("--Incoordinate")) inCoord = true;
            else if (args[i].equals("--Outcoordinate")) outCoord = true;
            else if (args[i].equals("--Help")) {
                System.out.println("This program solves a maze.");
                System.out.println("--Stack   : solve using a stack");
                System.out.println("--Queue   : solve using a queue");
                System.out.println("--Opt     : solve using shortest path");
                System.out.println("--Time    : print runtime");
                System.out.println("--Incoordinate  : input is coordinate format");
                System.out.println("--Outcoordinate : output is coordinate format");
                System.exit(0);
            }
        }

        int count = 0;
        if (useStack) count++;
        if (useQueue) count++;
        if (useOpt) count++;

        if (count != 1) {
            throw new IllegalCommandLineInputsException("Must use exactly one of --Stack, --Queue, or --Opt.");
        }

        String filename = args[args.length - 1];
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
            System.exit(-1);
            return;
        }

        if (!sc.hasNextInt()) {
            sc.close();
            throw new IncorrectMapFormatException("File must start with three positive integers.");
        }
        int rows = sc.nextInt();

        if (!sc.hasNextInt()) {
            sc.close();
            throw new IncorrectMapFormatException("File must start with three positive integers.");
        }
        int cols = sc.nextInt();

        if (!sc.hasNextInt()) {
            sc.close();
            throw new IncorrectMapFormatException("File must start with three positive integers.");
        }
        int numMazes = sc.nextInt();

        if (rows <= 0 || cols <= 0 || numMazes <= 0) {
            sc.close();
            throw new IncorrectMapFormatException("Rows, cols, and numMazes must all be positive.");
        }

        Maze[] mazes;
        if (inCoord) {
            mazes = readCoordMap(sc, rows, cols, numMazes);
        } else {
            mazes = readTextMap(sc, rows, cols, numMazes);
        }

        sc.close();

        System.out.println("Mazes loaded successfully: " + mazes.length);
        System.out.println("Start: (" + mazes[0].startRow + ", " + mazes[0].startCol + ")");
        if (mazes[mazes.length - 1].endRow != -1)
            System.out.println("End: (" + mazes[mazes.length-1].endRow + ", " + mazes[mazes.length-1].endCol + ")");
    }

    public static Maze[] readTextMap(Scanner sc, int rows, int cols, int numMazes) throws Exception {
        Maze[] mazes = new Maze[numMazes];
        for (int m = 0; m < numMazes; m++) {
            mazes[m] = new Maze(rows, cols);
        }

        for (int m = 0; m < numMazes; m++) {
            for (int r = 0; r < rows; r++) {
                if (!sc.hasNext()) {
                    throw new IncompleteMapException("Not enough rows in maze " + m);
                }
                String line = sc.next();
                if (line.length() < cols) {
                    throw new IncompleteMapException("Row " + r + " in maze " + m + " is too short.");
                }
                for (int c = 0; c < cols; c++) {
                    char ch = line.charAt(c);
                    if (ch != '.' && ch != '@' && ch != 'W' && ch != '$' && ch != '|') {
                        throw new IllegalMapCharacterException("Illegal character '" + ch + "' at row " + r + " col " + c);
                    }
                    mazes[m].setCell(ch, r, c);
                }
            }
        }

        return mazes;
    }

    public static Maze[] readCoordMap(Scanner sc, int rows, int cols, int numMazes) throws Exception {
        Maze[] mazes = new Maze[numMazes];
        for (int m = 0; m < numMazes; m++) {
            mazes[m] = new Maze(rows, cols);
        }

        while (sc.hasNext()) {
            String token = sc.next();
            if (token.length() != 1) {
                throw new IllegalMapCharacterException("Illegal character token: " + token);
            }
            char ch = token.charAt(0);
            if (ch != '.' && ch != '@' && ch != 'W' && ch != '$' && ch != '|') {
                throw new IllegalMapCharacterException("Illegal character: " + ch);
            }

            if (!sc.hasNextInt()) {
                throw new IncorrectMapFormatException("Expected row integer after character.");
            }
            int r = sc.nextInt();

            if (!sc.hasNextInt()) {
                throw new IncorrectMapFormatException("Expected col integer after row.");
            }
            int c = sc.nextInt();

            if (!sc.hasNextInt()) {
                throw new IncorrectMapFormatException("Expected maze level integer after col.");
            }
            int level = sc.nextInt();

            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                throw new IncorrectMapFormatException("Coordinate (" + r + "," + c + ") is out of bounds.");
            }
            if (level < 0 || level >= numMazes) {
                throw new IncorrectMapFormatException("Maze level " + level + " is out of bounds.");
            }

            mazes[level].setCell(ch, r, c);
        }

        return mazes;
    }
}