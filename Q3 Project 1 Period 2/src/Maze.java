public class Maze {
    public char[][] grid;
    public int rows;
    public int cols;
    public int startRow, startCol;
    public int endRow, endCol;
    public int walkRow, walkCol;

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        this.walkRow = -1;
        this.walkCol = -1;
        this.endRow = -1;
        this.endCol = -1;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = '.';
    }

    public void setCell(char ch, int r, int c) throws IllegalMapCharacterException {
        if (ch != '.' && ch != '@' && ch != 'W' && ch != '$' && ch != '|' && ch != '+')
            throw new IllegalMapCharacterException("Illegal character: " + ch);

        grid[r][c] = ch;

        if (ch == 'W') {
            startRow = r;
            startCol = c;
        } else if (ch == '$') {
            endRow = r;
            endCol = c;
        } else if (ch == '|') {
            walkRow = r;
            walkCol = c;
        }
    }
}