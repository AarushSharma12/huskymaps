package seamfinding;

import seamfinding.energy.EnergyFunction;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Dynamic programming implementation of the {@link SeamFinder} interface.
 *
 * @see SeamFinder
 */
public class DynamicProgrammingSeamFinder implements SeamFinder {

    @Override
    public List<Integer> findHorizontal(Picture picture, EnergyFunction f) {
        int width = picture.width();
        int height = picture.height();

        // Initialize a 2D array to store the accumulated energy costs
        double[][] dp = new double[width][height];

        // Fill out the leftmost column with the energy values
        for (int y = 0; y < height; y++) {
            dp[0][y] = f.apply(picture, 0, y);
        }

        // Fill out the rest of the dp table
        for (int x = 1; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double minPrevEnergy = dp[x - 1][y];  // Left-middle neighbor

                // Check left-up neighbor if within bounds
                if (y > 0) {
                    minPrevEnergy = Math.min(minPrevEnergy, dp[x - 1][y - 1]);
                }

                // Check left-down neighbor if within bounds
                if (y < height - 1) {
                    minPrevEnergy = Math.min(minPrevEnergy, dp[x - 1][y + 1]);
                }

                // Update dp with current pixel's energy plus minimum previous energy
                dp[x][y] = f.apply(picture, x, y) + minPrevEnergy;
            }
        }

        // Find the minimum energy value in the rightmost column
        int minIndex = 0;
        double minEnergy = dp[width - 1][0];
        for (int y = 1; y < height; y++) {
            if (dp[width - 1][y] < minEnergy) {
                minEnergy = dp[width - 1][y];
                minIndex = y;
            }
        }

        // Trace back to find the seam
        List<Integer> seam = new ArrayList<>();
        seam.add(minIndex);
        for (int x = width - 1; x > 0; x--) {
            int prevY = seam.get(seam.size() - 1);
            int bestY = prevY;

            // Consider left-up neighbor
            if (prevY > 0 && dp[x - 1][prevY - 1] < dp[x - 1][bestY]) {
                bestY = prevY - 1;
            }

            // Consider left-down neighbor
            if (prevY < height - 1 && dp[x - 1][prevY + 1] < dp[x - 1][bestY]) {
                bestY = prevY + 1;
            }

            seam.add(bestY);
        }

        // Reverse the seam to get the coordinates from left to right
        Collections.reverse(seam);
        return seam;
    }
}

