package sample;

import java.util.Arrays;
import java.util.Random;

public class MotionModel {
    private static final int[] directionProbability = new int[]{10, 80, 10}; //left ,forward, right
    private static final Random RANDOM = new Random();
    private static int sum = Arrays.stream(directionProbability).sum();

    public static RelativeDirection getRelativeDirection() {
        int uniformRandValue = RANDOM.nextInt(sum);

        // System.out.println("rndVal= "+uniformRandValue);
        if (uniformRandValue <= directionProbability[0])
            return RelativeDirection.LEFT;
        else if (uniformRandValue > directionProbability[0] && uniformRandValue <= directionProbability[0] + directionProbability[1])
            return RelativeDirection.FORWARD;
//  default else if (uniformRandValue>taskDistribution[1])
        return RelativeDirection.RIGHT;

    }

    public static double[] getDirectionProb(AbsoluteDirection intendedDir) {
        double[] res = new double[4];
        res[intendedDir.ordinal()] = directionProbability[RelativeDirection.FORWARD.ordinal()] / (double) sum;

        int right = intendedDir.ordinal() + 1;
        if (right > 3) right -= 4;
        if (right < 0) right += 4;
        int left = intendedDir.ordinal() - 1;
        if (left > 3) left -= 4;
        if (left < 0) left += 4;

        res[right] = directionProbability[RelativeDirection.RIGHT.ordinal()] / (double) sum;
        res[left] = directionProbability[RelativeDirection.LEFT.ordinal()] / (double) sum;
        return res;
    }

}
