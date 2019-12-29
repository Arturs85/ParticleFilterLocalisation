package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ObstacleMap {
    MapCell selectedCell;
    ArrayList<MapCell> cells;
    int width;
    int height;
    int maxSize = 100;

    static int cellsOffset = 10;
    int startPositionCount = 0;
    int terminalCount = 0;
    Canvas canvas;

    ObstacleMap(int width, int height, Canvas canvas) {
        this.width = width;
        this.height = height;
        this.canvas = canvas;
        cells = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int cellNr = i + j * width + 1;// numbering starts with 1
                cells.add(new MapCell(i, j, this));

                //   System.out.println("cell " + cellNr + " created");
            }
        }
        //cells.get(0).setAsTerminal(true);
    }

    public void setWidth(int width) {
        if(width<maxSize && width>1)
        this.width = width;
            }

    public void setHeight(int height) {
        if(height<maxSize && height>1)
            this.height = height;
    }

    void createMapCells(){
        cells = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int cellNr = i + j * width + 1;// numbering starts with 1
                cells.add(new MapCell(i, j, this));

                //   System.out.println("cell " + cellNr + " created");
            }
        }
selectedCell =null;
    }

    void draw() {
        draw(canvas);
    }

    void draw(Canvas canvas) {
        //background
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);  // fill with white background
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // draw cells
        for (MapCell c : cells) {
            c.draw(canvas);
        }
        if (selectedCell != null)
            selectedCell.draw(canvas);
    }

    void unselectMapCells() {
        for (MapCell c : cells) {
            c.isSelected = false;
        }
        selectedCell = null;
    }

    void selectCell(double x, double y) {
        unselectMapCells();

        int xx = (int) x;
        int yy = (int) y;
        int xpos = (xx / MapCell.size) * MapCell.size + cellsOffset;
        int ypos = (yy / MapCell.size) * MapCell.size + cellsOffset;
        //  System.out.println("xpos : " + xpos + " ypos : " + ypos);
        for (MapCell c : cells) {
            if (c.position.x == xpos && c.position.y == ypos) {
                selectedCell = c;
                c.isSelected = true;
                System.out.println("x : " + c.x + " y : " + c.y);

                break;
            }
        }

    }


    void getAdjecentCells(MapCell c, MapCell[] adjecentCells) {
        adjecentCells[1] = getCell(c.x, c.y - 1);//up
        adjecentCells[2] = getCell(c.x + 1, c.y);//right
        adjecentCells[3] = getCell(c.x, c.y + 1);//down
        adjecentCells[0] = getCell(c.x - 1, c.y);//left

    }

    MapCell getCell(int nr) {
        for (MapCell c : cells) {
            if (nr == c.cellNr)
                return c;
        }
        return null;
    }

    MapCell getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return null;

        for (MapCell c : cells) {
            if (x + y * width + 1 == c.cellNr)
                return c;
        }

        return null;
    }

    MapCell getStartPosition() {
        for (MapCell c : cells) {
            if (c.isStartPosition)
                return c;
        }
        return null;
    }

    void clearStartPosition() {
        MapCell st = getStartPosition();
        if (st != null)
            st.isStartPosition = false;
    }

}
