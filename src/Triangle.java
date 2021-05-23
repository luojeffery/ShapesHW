import java.util.*;

public class Triangle implements TwoDShape, Positionable {

    List<TwoDPoint> vertices;

    public Triangle(List<TwoDPoint> vertices) {
        try {
            if (isMember(vertices))
                this.vertices = vertices;
            else
                throw new IllegalArgumentException("Invalid inputs for a triangle.");
        }
        catch(IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sets the position of this triangle according to the first three elements in the specified list of points. The
     * triangle is formed on the basis of these three points taken in a clockwise manner on the two-dimensional
     * x-y plane. If the input list has more than three elements, the subsequent elements are ignored.
     *
     * @param points the specified list of points.
     */
    @Override
    public void setPosition(List<? extends Point> points) {
        try {
            if (!isMember(vertices))
                throw new IllegalArgumentException("Invalid inputs for a triangle.");

            //finds the coordinates of the centroid of the triangle
            double[] center = new double[2];
            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < 3; i++) {
                    center[j] += points.get(i).coordinates()[j];
                }
                center[j] = center[j] / 3;
            }

            //finds the polar angle of the centroid to the first three points
            //then creates a hashmap to associate each angle to each TwoDPoint object
            double[] angles = new double[3];
            HashMap<Double, TwoDPoint> reference = new HashMap<>();
            for (int i = 0; i < 3; i++) {
                double x = points.get(i).coordinates()[0];
                double y = points.get(i).coordinates()[1];
                angles[i] = Math.atan2(x - center[0], y - center[1]);
                reference.put(angles[i], (TwoDPoint) points.get(i));
            }
            //the angles are sorted from greatest to least
            //each angle is then used to get corresponding triangle points, added to a temp var
            // in clockwise order, and then vertices is reassigned to temp.
            Arrays.sort(angles);
            //Collections.reverse(Arrays.asList(angles));

            List<TwoDPoint> temp = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                temp.add(reference.get(angles[i]));
            }
            vertices = temp;
        }
        catch(IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Retrieve the position of an object as a list of points. The points are to be retrieved and added to the returned
     * list in a clockwise manner on the two-dimensional x-y plane, starting with the point with the least x-value. If
     * two points have the same least x-value, then the clockwise direction starts with the point with the lower y-value.
     *
     * @return the retrieved list of points.
     */
    @Override
    public List<? extends Point> getPosition() {
        List<TwoDPoint> temp = vertices;
        //this contains the indices of the vertices with the same minimum x value
        ArrayList<Integer> minXi = new ArrayList<>();

        //the 0th x is taken out so that in finding the min x values, there will be no error
        double minx = vertices.get(0).coordinates()[0];
        minXi.add(0);

        //find the minimum x values
        for (int i = 1; i < 3; i++) {
            double x = vertices.get(i).coordinates()[0];
            if (x < minx) {
                minx = x;
                //because a new minimum was found, clear minXi to store the index of a new minimum x
                minXi.clear();
                minXi.add(i);
            }
            else if (x == minx)
                minXi.add(i);
        }

        //the only values within minXi should be equal, because there can only be one minimum value and its duplicates.
        double miny = vertices.get(minXi.get(0)).coordinates()[1];
        int minyIndex = minXi.get(0);

        //find the minimum y value
        for (int i = 1; i < minXi.size(); i++) {
            double y = vertices.get(i).coordinates()[1];
            if (y < miny) {
                miny = y;
                minyIndex = i;
            }
        }

        setPosition(vertices);
        List<TwoDPoint> clockwise = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            clockwise.add(vertices.get((minyIndex + i) % 3));
        }
        vertices = temp;
        return clockwise;
    }

    /**
     * @return the number of sides of this triangle, which is always set to three
     */
    @Override
    public int numSides() {
        return 3;
    }

    /**
     * Checks whether or not a list of vertices forms a valid triangle. The <i>trivial</i> triangle, where all three
     * corner vertices are the same point, is considered to be an invalid triangle.
     *
     * @param vertices the list of vertices to check against, where each vertex is a <code>Point</code> type.
     * @return <code>true</code> if <code>vertices</code> is a valid collection of points for a triangle, and
     * <code>false</code> otherwise. For example, three vertices are in a straight line is invalid.
     */
    @Override
    public boolean isMember(List<? extends Point> vertices) {
        double[] xcoords = new double[3];
        for (int i = 0; i < 3; i++) {
            xcoords[i] = vertices.get(i).coordinates()[0];
        }
        //short circuiting, since all the xs are the same it's an invalid triangle. There is no need to check for all ys being the same.
        if (xcoords[0] == xcoords[1] && xcoords[1] == xcoords[2])
            return false;
        else {
            return area(vertices) != 0;
        }
    }

    /**
     * This method snaps each vertex of this triangle to its nearest integer-valued x-y coordinate. For example, if
     * a corner is at (0.8, -0.1), it will be snapped to (1,0). The resultant triangle will thus have all three
     * vertices in positions with integer x and y values. If the snapping procedure described above results in this
     * triangle becoming invalid (e.g., all corners collapse to a single point), then it is left unchanged. Snapping is
     * an in-place procedure, and the current instance is modified.
     */
    public void snap() {
        List<TwoDPoint> v = new ArrayList<>();
        double[] xcoords = new double[3];
        double[] ycoords = new double[3];
        for (int i = 0; i < 3; i++) {
            xcoords[i] = vertices.get(i).coordinates()[0];
            ycoords[i] = vertices.get(i).coordinates()[1];
        }

        for (int i = 0; i < 3; i++) {
            xcoords[i] = Math.round(xcoords[i]);
            ycoords[i] = Math.round(ycoords[i]);
            TwoDPoint rounded = new TwoDPoint(xcoords[i], ycoords[i]);
            v.add(rounded);
        }
        vertices = v;
    }

    /**
     * @return the area of this triangle
     */
    public double area() {
        ArrayList<Double> sides = new ArrayList<>();
        double s = perimeter() / 2;
        for (int i = 0; i < 2; i++) {
            for (int j = i + 1; j < 3; j++) {
                sides.add(Math.hypot(vertices.get(i).coordinates()[0] - vertices.get(j).coordinates()[0],
                        vertices.get(i).coordinates()[1] - vertices.get(j).coordinates()[1]));
            }
        }
        return Math.sqrt(s * (s - sides.get(0))* (s - sides.get(1))* (s - sides.get(2)));
    }

    public double area(List<? extends Point> vertices) {
        ArrayList<Double> sides = new ArrayList<>();
        double s = perimeter(vertices) / 2;
        for (int i = 0; i < 2; i++) {
            for (int j = i + 1; j < 3; j++) {
                sides.add(Math.hypot(vertices.get(i).coordinates()[0] - vertices.get(j).coordinates()[0],
                        vertices.get(i).coordinates()[1] - vertices.get(j).coordinates()[1]));
            }
        }
        return Math.sqrt(s * (s - sides.get(0))* (s - sides.get(1))* (s - sides.get(2)));
    }

    /**
     * @return the perimeter (i.e., the total length of the boundary) of this triangle
     */
    public double perimeter() {
        double perimeter = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = i + 1; j < 3; j++) {
                perimeter += Math.hypot(vertices.get(i).coordinates()[0] - vertices.get(j).coordinates()[0],
                        vertices.get(i).coordinates()[1] - vertices.get(j).coordinates()[1]);
            }
        }
        return perimeter;
    }

    public double perimeter(List<? extends Point> vertices) {
        double perimeter = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = i + 1; j < 3; j++) {
                perimeter += Math.hypot(vertices.get(i).coordinates()[0] - vertices.get(j).coordinates()[0],
                        vertices.get(i).coordinates()[1] - vertices.get(j).coordinates()[1]);
            }
        }
        return perimeter;
    }

    public String toString() {
        List<TwoDPoint> temp = vertices;
        setPosition(vertices);
        double x1 = vertices.get(0).coordinates()[0];
        double y1 = vertices.get(0).coordinates()[1];

        double x2 = vertices.get(1).coordinates()[0];
        double y2 = vertices.get(1).coordinates()[1];

        double x3 = vertices.get(2).coordinates()[0];
        double y3 = vertices.get(2).coordinates()[1];

        vertices = temp;

        return "Triangle[(" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + "), (" + x3 + ", " + y3 + ")]";
    }
}
