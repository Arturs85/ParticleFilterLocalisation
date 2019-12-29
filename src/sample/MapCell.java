package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.*;

public class MapCell {
    static int size = 50;
    static int scale = 1;
    static double MAX_ERR_PERCENT = 1;
    static double MAX_ERR_PERCENT_FIRST_STEP = 230;

    double reward = -0.04;
    double terminalValue = 1;
    boolean isObstacle = false;
    boolean isTerminal = false;
    boolean isStartPosition = false;
    boolean isSelected = false;
    AbsoluteDirection policyDirection = AbsoluteDirection.NORTH;
    double curUtilityValue = 0;
    double nextUtilityValue = 0;

    int x;//numbering
    int y;
    Point position;//drawing
    int cellNr;
    ObstacleMap board;
    String availableDirectionsString;

    MapCell(int x, int y, ObstacleMap board) {
        this.board = board;
        this.x = x;//redundant - also present in board
        this.y = y;
        this.cellNr = x + y * board.width + 1;
        position = new Point(x * MapCell.size + ObstacleMap.cellsOffset, y * MapCell.size + ObstacleMap.cellsOffset);
    }

    void setAsTerminal(boolean isTerminal) {
        this.isTerminal = isTerminal;
        if (isTerminal)
            curUtilityValue = terminalValue;
        else curUtilityValue = 0;
    }

    //use only after next u value calculation
    //returns true if values match
    boolean updateValue() {
        boolean ret;

        if (Math.abs(curUtilityValue - nextUtilityValue) < Math.abs(curUtilityValue) * MAX_ERR_PERCENT / 100)
            ret = true;
        else {
            ret = false;
            // System.out.println(" no match cell: x: "+x+" y: "+y);
        }
        curUtilityValue = nextUtilityValue;
        return ret;

    }

    boolean updateValueFirstStep() {
        boolean ret;

        if (Math.abs(curUtilityValue - nextUtilityValue) < Math.abs(curUtilityValue) * MAX_ERR_PERCENT_FIRST_STEP / 100)
            ret = true;
        else {
            ret = false;
            // System.out.println(" no match cell: x: "+x+" y: "+y);
        }
        curUtilityValue = nextUtilityValue;
        return ret;

    }

    String composeAvailableDirectionsString() {
        String res = "";
        MapCell[] adj = new MapCell[4];
        board.getAdjecentCells(this, adj);
        if (adj[3] != null && !adj[3].isObstacle)
            res += "◀";
        if (adj[0] != null && !adj[0].isObstacle)
            res += "▲";
        if (adj[2] != null && !adj[2].isObstacle)
            res += "▼";
        if (adj[1] != null && !adj[1].isObstacle)
            res += "▶";

        return res;
    }

    String getPolicyDirectionString() {
        String res = "Policy ";
        int dir = policyDirection.ordinal();
        if (dir == 3)
            res += "◀";
        if (dir == 0)
            res += "▲";
        if (dir == 2)
            res += "▼";
        if (dir == 1)
            res += "▶";

        return res;
    }

    double calculateUtility(AbsoluteDirection absoluteDirection) {
        MapCell[] adjCells = new MapCell[4];
        board.getAdjecentCells(this, adjCells);
        double[] probabilities = MotionModel.getDirectionProb(absoluteDirection);
        double uValue = 0;
        for (int i = 0; i < adjCells.length; i++) {
            if (adjCells[i] != null && !adjCells[i].isObstacle)
                uValue += adjCells[i].curUtilityValue * probabilities[i];
            else
                uValue += curUtilityValue * probabilities[i];// if adj cell is null, then it means that agent bumps back in current position
        }
        return uValue + reward;
    }

    void calculateUtility() {
        nextUtilityValue = calculateUtility(policyDirection);

    }

    //do not use return value
    boolean updatePolicy() {
        double maxUtilityVal = -Double.MAX_VALUE;
        //if(x==19&&y==0)
        //  System.out.println("minVal: "+maxUtilityVal);
        for (int i = 0; i < AbsoluteDirection.values().length; i++) {
            double proposedValue = calculateUtility(AbsoluteDirection.values()[i]);
            // if(x==19&&y==0) {
            //     System.out.println("Proposed value at cell " + x + ", " + y + " is" + proposedValue);

            // }
            if (proposedValue > maxUtilityVal) {
                policyDirection = AbsoluteDirection.values()[i];
                nextUtilityValue = proposedValue; // update curValues to nextValues at the end of iteration
                maxUtilityVal = proposedValue;
            }
        }

        return true;
    }

    void draw(Canvas canvas) {
        GraphicsContext g = canvas.getGraphicsContext2D();

//draw border;
        if (!isSelected)
            g.setStroke(Color.DARKGRAY);
        else
            g.setStroke(Color.RED);

        g.setLineWidth(2);
        g.strokeRect(scale * position.x, scale * position.y, scale * size, scale * size);

        g.setStroke(Color.DARKGRAY);
        g.setFill(Color.DARKGRAY);

        //avail dir
        g.setLineWidth(1);

//        if (!isObstacle && !isTerminal) {
//            //g.strokeText(composeAvailableDirectionsString(), position.x + 5, position.y + 20);
//            g.strokeText(getPolicyDirectionString(), position.x + 5, position.y + 20);
//            String uVal = String.format("%.4f", curUtilityValue);
//            g.strokeText("U: " + uVal, position.x + 5, position.y + 35);
//
//        }

        //obstacle

        if (isObstacle) {
            g.fillRect(position.x, position.y, size, size);
        }
        if (isStartPosition) {
            g.strokeText("S", position.x + 5, position.y + 40);
        }
        if (isTerminal) {
            g.strokeText("T " + terminalValue, position.x + 25, position.y + 40);
        }


    }


}
