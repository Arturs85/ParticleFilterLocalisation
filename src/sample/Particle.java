package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.*;
import java.util.Random;

public class Particle {
    float x;
    float y;
    AbsoluteDirection direction;
    float weight;
    float oldWeight = 0;
    static Random random = new Random();
    ObstacleMap board;

    public Particle(float x, float y, int direction, float weight, ObstacleMap board) {
        this.x = x;
        this.y = y;
        this.weight = weight;
        this.direction = AbsoluteDirection.values()[direction];
        this.board = board;
    }

    float predictMeasurement() {
        int dist = MonteCarloAgent.maeasureDistToObstacles(MonteCarloAgent.BEAM_LENGTH, board, (int) x, (int) y, direction);// change to float
        return dist;
    }

    float calcDifference(float predictedMeasurement, float actualMeasurement) {
        return Math.abs(actualMeasurement - predictedMeasurement);
    }

    void disperse(float sigma) {
        double dx = sigma * (random.nextGaussian());
        x += dx;
        double dy = sigma * (random.nextGaussian());
        y += dy;
        // System.out.println("dx: "+dx+" dy: "+dy);
    }

    void updateWeigt(float actualMeasurement) {

        float diff = calcDifference(predictMeasurement(), actualMeasurement);
        System.out.println("dif: " +diff);
        if (diff > 0.5) weight = 0;
        else
            weight = MonteCarloAgent.BEAM_LENGTH - diff;

        if (weight < 0) weight = 0;
        oldWeight = weight;
    }

    void moveAccordingModel() {
        MonteCarloAgent.moveAccordingModel(this);
        clipCordinates();
    }

    void turnRight() {
        direction = MonteCarloAgent.turnRight(direction);
    }

    void turnLeft() {
        direction = MonteCarloAgent.turnLeft(direction);
    }

    void clipCordinates() {
        if (x > board.width) x = board.width;
        if (x < 0) x = 0;
        if (y > board.height) y = board.height;
        if (y < 0) y = 0;

    }

    void draw() {
// 🤖
        int lengthOfDirIndicator = 10;
        int r = 3;
        GraphicsContext g = board.canvas.getGraphicsContext2D();
        g.setStroke(Color.GREEN);
        g.setLineWidth(1);
        g.strokeOval(x * MapCell.size + ObstacleMap.cellsOffset - r, y * MapCell.size + ObstacleMap.cellsOffset - r, 2 * r, 2 * r);
        float dirIndicatorCoordX = x * MapCell.size + ObstacleMap.cellsOffset;
        float dirIndicatorCoordY = y * MapCell.size + ObstacleMap.cellsOffset;

        switch (direction) {

            case NORTH:
                dirIndicatorCoordY -= lengthOfDirIndicator;
                break;
            case SOUTH:
                dirIndicatorCoordY += lengthOfDirIndicator;
                break;
            case EAST:
                dirIndicatorCoordX += lengthOfDirIndicator;
                break;
            case WEST:
                dirIndicatorCoordX -= lengthOfDirIndicator;
                break;

        }

        g.strokeLine(x * MapCell.size + ObstacleMap.cellsOffset, y * MapCell.size + ObstacleMap.cellsOffset, dirIndicatorCoordX, dirIndicatorCoordY);
        //  g.strokeText(""+oldWeight,x*MapCell.size+ObstacleMap.cellsOffset,y*MapCell.size+ObstacleMap.cellsOffset);
    }
}
