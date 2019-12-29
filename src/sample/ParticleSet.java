package sample;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class ParticleSet {
    static final int PARTICLE_COUNT = 300;
    static final float SIGMA = 0.3f;
    private static final Random RANDOM = new Random();
    boolean hasCalculatedWeigths  = false;
    ObstacleMap board;
    ArrayList<Particle> particles = new ArrayList<>(PARTICLE_COUNT);
    Point2D estimate;
    public ParticleSet(ObstacleMap board) {
        this.board = board;
    }

    void initializeParticles() {
        particles.clear();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float weight = 1.0f / PARTICLE_COUNT;
            particles.add(new Particle(RANDOM.nextFloat() * board.width, RANDOM.nextFloat() * board.height, RANDOM.nextInt(AbsoluteDirection.values().length), weight, board));
        }
estimate=calcAverageParticle();
    }
void calculateParticleWeights(float measurement){
    for (Particle p : particles) {
        p.updateWeigt(measurement);
       // System.out.println(p.x+ " y:"+p.y  +" dir:"+ p.direction +" measure: "+measurement+" weigth: "+ p.weight);
    }

hasCalculatedWeigths=true;
}
    void resampleParticles() {

        ArrayList<Particle> newParticles = new ArrayList<>(PARTICLE_COUNT);

        //resample
        float sumOfWeigths = getSumOfWeigths();
        System.out.println("\nsumOfWeights was " + sumOfWeigths);

        float rangeOfSingleParticle = sumOfWeigths / PARTICLE_COUNT;
        float upperBound = 0;
        float curCounter = 0;
        for (Particle p : particles) {
            upperBound += p.weight;
            int binCount = 0;
            while (curCounter < upperBound) {
                curCounter += rangeOfSingleParticle;
                newParticles.add(new Particle(p.x, p.y, p.direction.ordinal(), 1.0f / PARTICLE_COUNT, board));
                binCount++;
            }
            System.out.print(binCount + " ");
        }
        System.out.println(", new Particles count: " + newParticles.size());
        particles = newParticles;
        disperse(SIGMA);
    hasCalculatedWeigths = false;
        estimate = calcAverageParticle();

    }

    void disperse(float sigma) {
        for (Particle p : particles) {
            p.disperse(sigma);
        }

    }

    void moveParticlesAccordingModel() {
        for (Particle p : particles) {
            p.moveAccordingModel();
        }
        estimate = calcAverageParticle();

    }

    void turnRightParticles() {
        for (Particle p : particles) {
            p.turnRight();
        }
    }
    void turnLeftParticles() {
        for (Particle p : particles) {
            p.turnLeft();
        }
    }
    float getSumOfWeigths() {
        float sum = 0;
        for (Particle p : particles) {
            sum += p.weight;
        }
        return sum;
    }

    Point2D calcAverageParticle(){
        float x = 0;
        float y = 0;
        for (Particle p : particles) {
           x+=p.x;
           y+=p.y;
        }
        if(!particles.isEmpty()){
        x=x/particles.size();
        y=y/particles.size();
    }
    return new Point2D(x,y);
    }

    void draw() {
        for (Particle p : particles) {
            p.draw();
        }
    GraphicsContext g =board.canvas.getGraphicsContext2D();
      if(estimate!=null) {
          g.setFill(Color.RED);
          g.fillOval(estimate.getX() * MapCell.size + ObstacleMap.cellsOffset, estimate.getY() * MapCell.size + ObstacleMap.cellsOffset, 9, 9);
      }
    }
}
