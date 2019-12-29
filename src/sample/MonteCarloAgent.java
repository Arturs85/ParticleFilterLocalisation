package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MonteCarloAgent {

    ObstacleMap board;
    MapCell currentLocation;
    int moveCount = 0;
    double collectedReward;
    AbsoluteDirection agentActualDirection = AbsoluteDirection.NORTH;
    static int BEAM_LENGTH = 4;
   static String[] dirSymbol=new String[]{"<","^",">","v"};
static AbsoluteDirection[] ADvalues=AbsoluteDirection.values();
   int lastMeasurement=0;
    MonteCarloAgent(ObstacleMap board) {
        this.board = board;

    }

    void move(AbsoluteDirection intendedDirection) {
        MapCell[] adjCells = new MapCell[4];
        board.getAdjecentCells(currentLocation, adjCells);
        int resultingDirection = intendedDirection.ordinal() + MotionModel.getRelativeDirection().ordinal() - 1;
        if (resultingDirection < 0) resultingDirection += 4;
        if (resultingDirection > 3) resultingDirection -= 4;
        MapCell destinationCell = adjCells[resultingDirection];
        if (destinationCell != null && !destinationCell.isObstacle) {
            currentLocation = destinationCell; // move to destination cell
        } else {
            //do nothing, agent stays in current cell

        }
        collectedReward += currentLocation.reward;
        if (currentLocation.isTerminal) {
            collectedReward += currentLocation.terminalValue;
        }
        moveCount++;

       measureDistance();
    }
    static void moveAccordingModel(Particle p){// for particles
        int resultingDirection = p.direction.ordinal() + MotionModel.getRelativeDirection().ordinal() - 1;
        if (resultingDirection < 0) resultingDirection += 4;
        if (resultingDirection > 3) resultingDirection -= 4;

        switch (ADvalues[resultingDirection]) {
            case NORTH:
                p.y -= 1;
                break;
            case SOUTH:
                p.y += 1;
                break;
            case EAST:
                p.x += 1;
                break;
            case WEST:
                p.x -= 1;
                break;

        }
       }
    static void moveForward(Particle p){//for particles
        switch (p.direction) {
            case NORTH:
                p.y -= 1;
                break;
            case SOUTH:
                p.y += 1;
                break;
            case EAST:
                p.x += 1;
                break;
            case WEST:
                p.x -= 1;
                break;

        }

    }

    void  turnRight(){
       int dir = agentActualDirection.ordinal()+1;
    if(dir>3)dir -=4;
    agentActualDirection=AbsoluteDirection.values()[dir];
        System.out.println("Dir: "+agentActualDirection.name());
measureDistance();
    }
   static AbsoluteDirection  turnRight(AbsoluteDirection aDir){
        int dir = aDir.ordinal() + 1;
        if(dir>3)dir -=4;
        return AbsoluteDirection.values()[dir];
    }
    static AbsoluteDirection  turnLeft(AbsoluteDirection aDir){
        int dir = aDir.ordinal() - 1;
        if(dir<0)dir +=4;
        return AbsoluteDirection.values()[dir];
    }

    void  turnLeft(){
        int dir = agentActualDirection.ordinal()-1;
        if(dir<0)dir +=4;
        agentActualDirection=AbsoluteDirection.values()[dir];
        System.out.println("Dir: "+agentActualDirection.name());
measureDistance();
    }
    void moveAccordingToPolicy() {
        if (currentLocation != null)
            move(currentLocation.policyDirection);
        else {
            System.out.println("Agent: current location not set, returning");
        }
    }

    boolean moveToStart() {
        MapCell start = board.getStartPosition();
        if (start != null) {
            currentLocation = start;
            return true;
        }
        return false;
    }
    void measureDistance(){//for agent
        lastMeasurement = maeasureDistToObstacles(BEAM_LENGTH,board,currentLocation.x,currentLocation.y,agentActualDirection);
        System.out.println("dist: " + lastMeasurement);

    }

    static int maeasureDistToObstacles(int beamLength,ObstacleMap board, int xCur,int yCur,AbsoluteDirection agentActualDirection) {
        int dist = Integer.MAX_VALUE;// means no obstacle dtected in sight
        for (int i = 1; i < beamLength; i++) {//forward direction
            MapCell c = getMapCellAtDistance(board,xCur,yCur,agentActualDirection, i, RelativeDirection.FORWARD);
            if (c == null || c.isObstacle) {
                dist = i;
                break;
            }
        }
        return dist;
    }

   static MapCell getMapCellAtDistance(ObstacleMap board,int xCur,int yCur,AbsoluteDirection agentActualDirection, int distance, RelativeDirection sensorDirection) {//for dist measurements
        AbsoluteDirection sensorAbsDirection = getDirection(sensorDirection, agentActualDirection);

            switch (sensorAbsDirection) {
                case NORTH:
                    yCur -= distance;
                    break;
                case SOUTH:
                    yCur += distance;
                    break;
                case EAST:
                    xCur += distance;
                    break;
                case WEST:
                    xCur -= distance;
                    break;

            }
        return board.getCell(xCur, yCur);
    }

   static AbsoluteDirection getDirection(RelativeDirection sensorDirection, AbsoluteDirection robotDirection) {
        int resultingDirection = robotDirection.ordinal() + sensorDirection.ordinal() - 1;
        if (resultingDirection < 0) resultingDirection += 4;
        if (resultingDirection > 3) resultingDirection -= 4;
        return AbsoluteDirection.values()[resultingDirection];
    }

    void draw() {
// 🤖

        GraphicsContext g = board.canvas.getGraphicsContext2D();

        g.setLineWidth(1);
        g.setFont(Font.font(20));
        g.setStroke(Color.BLACK);
        if (currentLocation != null) {
            g.strokeText(dirSymbol[agentActualDirection.ordinal()]+" \uD83E\uDD16", currentLocation.position.x + 5, currentLocation.position.y + 35);
        }
        g.setFont(Font.getDefault());
    }

}

