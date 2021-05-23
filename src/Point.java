/**
 * An interface to represent a single point in a geometric space.
 */
public interface Point {

    /**
     * @return the coordinates of this point as an array of doubles, specifying its location in the geometric space.
     */
    double[] coordinates();

    default double distance() {
        double sum = 0;
        for (int i = 0; i < coordinates().length; i++) {
            sum += coordinates()[i];
        }
        return Math.sqrt(sum);
    }

    default int compareTo(Point o) {
        return (int) (distance() - o.distance());
    }
}