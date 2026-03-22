import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;

public class p1 {

    boolean useStack = false;
    boolean useQueue = false;
    boolean useOpt = false;
    boolean useTime = false;
    boolean inCoord = false;
    boolean outCoord = false;
    Maze[] mazes;

    public p1() {}

    public static void main(String[] args) {
        p1 p = new p1();
        p.run(args);
    }

    public void run(String[] args) {
        try {
            if (args.length < 2) {
                throw new IllegalCommandLineInputsException("Not enough arguments.");
            }

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

            if (inCoord) {
                mazes = readCoordMap(sc, rows, cols, numMazes);
            } else {
                mazes = readTextMap(sc, rows, cols, numMazes);
            }

            sc.close();

            double startTime = System.nanoTime();

            boolean solved = false;
            if (useQueue) {
                solved = solveAllMazes(false);
            } else if (useStack) {
                solved = solveAllMazes(true);
            } else if (useOpt) {
                solved = solveAllMazes(false);
            }

            double endTime = System.nanoTime();

            if (!solved) {
                System.out.println("The Wolverine Store is closed.");
            }

            if (useTime) {
                double totalTime = (endTime - startTime) / 1000000000.0;
                System.out.println("Total Runtime: " + totalTime + " seconds");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean solveAllMazes(boolean useStack) {
        for (int m = 0; m < mazes.length; m++) {
            Maze maze = mazes[m];
            int targetRow, targetCol;
            boolean hasEnd = maze.endRow != -1;

            if (hasEnd) {
                targetRow = maze.endRow;
                targetCol = maze.endCol;
            } else if (maze.walkRow != -1) {
                targetRow = maze.walkRow;
                targetCol = maze.walkCol;
            } else {
                return false;
            }

            Position result;
            if (useStack) {
                result = dfs(maze, maze.startRow, maze.startCol, targetRow, targetCol);
            } else {
                result = bfs(maze, maze.startRow, maze.startCol, targetRow, targetCol);
            }

            if (result == null) return false;

            markPath(maze, result);
            printSolution(maze, m, result);

            if (hasEnd) {
                return true;
            } else {
                if (m + 1 < mazes.length) {
                    if (mazes[m + 1].startRow == -1) {
                        mazes[m + 1].startRow = maze.walkRow;
                        mazes[m + 1].startCol = maze.walkCol;
                    }
                }
            }
        }
        return false;
    }

    public Position bfs(Maze maze, int startRow, int startCol, int targetRow, int targetCol) {
        boolean[][] visited = new boolean[maze.rows][maze.cols];
        Queue<Position> queue = new LinkedList<Position>();

        Position start = new Position(startRow, startCol, null);
        queue.add(start);
        visited[startRow][startCol] = true;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, 1, -1};

        while (!queue.isEmpty()) {
            Position curr = queue.remove();

            for (int d = 0; d < 4; d++) {
                int nr = curr.row + dr[d];
                int nc = curr.col + dc[d];

                if (nr < 0 || nr >= maze.rows || nc < 0 || nc >= maze.cols) continue;
                if (visited[nr][nc]) continue;
                if (maze.grid[nr][nc] == '@') continue;

                Position next = new Position(nr, nc, curr);
                visited[nr][nc] = true;

                if (nr == targetRow && nc == targetCol) {
                    return next;
                }

                queue.add(next);
            }
        }

        return null;
    }

    public Position dfs(Maze maze, int startRow, int startCol, int targetRow, int targetCol) {
        boolean[][] visited = new boolean[maze.rows][maze.cols];
        Stack<Position> stack = new Stack<Position>();

        Position start = new Position(startRow, startCol, null);
        stack.push(start);
        visited[startRow][startCol] = true;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, 1, -1};

        while (!stack.isEmpty()) {
            Position curr = stack.pop();

            if (curr.row == targetRow && curr.col == targetCol) {
                return curr;
            }

            for (int d = 0; d < 4; d++) {
                int nr = curr.row + dr[d];
                int nc = curr.col + dc[d];

                if (nr < 0 || nr >= maze.rows || nc < 0 || nc >= maze.cols) continue;
                if (visited[nr][nc]) continue;
                if (maze.grid[nr][nc] == '@') continue;

                visited[nr][nc] = true;
                stack.push(new Position(nr, nc, curr));
            }
        }

        return null;
    }

    public void markPath(Maze maze, Position end) {
        Position curr = end;
        while (curr != null) {
            if (maze.grid[curr.row][curr.col] != 'W' && maze.grid[curr.row][curr.col] != '$' && maze.grid[curr.row][curr.col] != '|') {
                maze.grid[curr.row][curr.col] = '+';
            }
            curr = curr.parent;
        }
    }

    public void printSolution(Maze maze, int level, Position end) {
        if (outCoord) {
            Stack<Position> pathOrder = new Stack<Position>();
            Position curr = end;
            while (curr != null) {
                if (maze.grid[curr.row][curr.col] == '+') {
                    pathOrder.push(curr);
                }
                curr = curr.parent;
            }
            while (!pathOrder.isEmpty()) {
                Position p = pathOrder.pop();
                System.out.println("+ " + p.row + " " + p.col + " " + level);
            }
        } else {
            for (int r = 0; r < maze.rows; r++) {
                for (int c = 0; c < maze.cols; c++) {
                    System.out.print(maze.grid[r][c]);
                }
                System.out.println();
            }
        }
    }

    public Maze[] readTextMap(Scanner sc, int rows, int cols, int numMazes) throws Exception {
        Maze[] mz = new Maze[numMazes];
        for (int m = 0; m < numMazes; m++) {
            mz[m] = new Maze(rows, cols);
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
                    mz[m].setCell(ch, r, c);
                }
            }
        }

        return mz;
    }

    public Maze[] readCoordMap(Scanner sc, int rows, int cols, int numMazes) throws Exception {
        Maze[] mz = new Maze[numMazes];
        for (int m = 0; m < numMazes; m++) {
            mz[m] = new Maze(rows, cols);
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

            mz[level].setCell(ch, r, c);
        }

        return mz;
    }
}
