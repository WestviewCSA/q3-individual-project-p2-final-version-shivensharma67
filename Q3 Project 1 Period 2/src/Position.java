public class Position {
    public int row;
    public int col;
    public Position parent;

    public Position(int row, int col, Position parent) {
        this.row = row;
        this.col = col;
        this.parent = parent;
    }
}