import java.util.*;

public class Quadrilateral implements TwoDShape, Positionable {

    List<TwoDPoint> vertices;

    public Quadrilateral(List<TwoDPoint> vertices) {
        try {
            if (isMember(vertices))
                this.vertices = vertices;
            else
                throw new IllegalArgumentException("Invalid inputs for a quadrilateral.");
        }
        catch(IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sets the position of this quadrilateral according to the first four elements in the specified list of points. The
     * quadrilateral is formed on the basis of these four points taken in a clockwise manner on the two-dimensional
     * x-y plane. If the input list has more than four elements, the subsequent elements are ignored.
     *
     * @param points the specified list of points.
     */
    @Override
    public void setPosition(List<? extends Point> points) {
        double[] centroid = new double[2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                centroid[i] += points.get(j).coordinates()[i];
            }
            centroid[i] = centroid[i] / 4;
        }

        double[] angles = new double[4];
        HashMap<Double, TwoDPoint> reference = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            double x = points.get(i).coordinates()[0];
            double y = points.get(i).coordinates()[1];
            angles[i] = Math.atan2(x - centroid[0], y - centroid[1]);
            reference.put(angles[i], (TwoDPoint) points.get(i));
        }

        Arrays.sort(angles);
        //Collections.reverse(Arrays.asList(angles));

        List<TwoDPoint> temp = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            temp.add(reference.get(angles[i]));
        }
        vertices = temp;
    }

    /**
     * Retrieve the position of an object as a list of points. The points are be retrieved and added to the returned
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
        for (int i = 1; i < 4; i++) {
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

        for (int i = 0; i < 4; i++) {
            clockwise.add(vertices.get((minyIndex + i) % 4));
        }
        vertices = temp;
        return clockwise;
    }

    /**
     * @return the number of sides of this quadrilateral, which is always set to four
     */
    @Override
    public int numSides() {
        return 4;
    }

    /**
     * Checks whether or not a list of vertices forms a valid quadrilateral. The <i>trivial</i> quadrilateral, where all
     * four corner vertices are the same point, is considered to be an invalid quadrilateral.
     *
     * @param vertices the list of vertices to check against, where each vertex is a <code>Point</code> type.
     * @return <code>true</code> if <code>vertices</code> is a valid collection of points for a quadrilateral, and
     * <code>false</code> otherwise. For example, if three of the four vertices are in a straight line is invalid.
     */
    @Override
    public boolean isMember(List<? extends Point> vertices) {
        List<TwoDPoint> temp = this.vertices;
        setPosition(vertices); //sets all points in quadrilateral clockwise

        //triangle of points 1 2 3
        List<TwoDPoint> points0 = this.vertices.subList(0,3);
        Triangle triangle0 = new Triangle(points0);

        //triangle of points 1 2 4
        List<TwoDPoint> points1 = new ArrayList<>();
        points1.add(this.vertices.get(0));
        points1.add(this.vertices.get(1));
        points1.add(this.vertices.get(3));
        Triangle triangle1 = new Triangle(points1);

        //triangle of points 2 3 4
        List<TwoDPoint> points2= this.vertices.subList(1,4);
        Triangle triangle2 = new Triangle(points2);

        //triangle of points 1 3 4
        List<TwoDPoint> points3 = new ArrayList<>();
        points3.add(this.vertices.get(0));
        points3.add(this.vertices.get(2));
        points3.add(this.vertices.get(3));
        Triangle triangle3 = new Triangle(points3);

        this.vertices = temp;

        return triangle0.isMember(points0) && triangle1.isMember(points1) && triangle2.isMember(points2) && triangle3.isMember(points3);
    }

    /**
     * This method snaps each vertex of this quadrilateral to its nearest integer-valued x-y coordinate. For example, if
     * a corner is at (0.8, -0.1), it will be snapped to (1,0). The resultant quadrilateral will thus have all four
     * vertices in positions with integer x and y values. If the snapping procedure described above results in this
     * quadrilateral becoming invalid (e.g., all four corners collapse to a single point), then it is left unchanged.
     * Snapping is an in-place procedure, and the current instance is modified.
     */
    public void snap() {
        List<TwoDPoint> v = new ArrayList<>();
        double[] xcoords = new double[4];
        double[] ycoords = new double[4];
        for (int i = 0; i < 4; i++) {
            xcoords[i] = vertices.get(i).coordinates()[0];
            ycoords[i] = vertices.get(i).coordinates()[1];
        }

        for (int i = 0; i < 4; i++) {
            xcoords[i] = Math.round(xcoords[i]);
            ycoords[i] = Math.round(ycoords[i]);
            TwoDPoint rounded = new TwoDPoint(xcoords[i], ycoords[i]);
            v.add(rounded);
        }
        vertices = v;
    }

    /**
     * @return the area of this quadrilateral
     */
    public double area() {
        List<TwoDPoint> temp = this.vertices;
        setPosition(vertices);

        List<TwoDPoint> points0 = this.vertices.subList(0,3);
        Triangle triangle0 = new Triangle(points0);

        List<TwoDPoint> points1 = new ArrayList<>();
        points1.add(this.vertices.get(0));
        points1.add(this.vertices.get(2));
        points1.add(this.vertices.get(3));
        Triangle triangle1 = new Triangle(points1);

        this.vertices = temp;
        return triangle0.area() + triangle1.area();
    }

    /**
     * @return the perimeter (i.e., the total length of the boundary) of this quadrilateral
     */
    public double perimeter() {
        List<TwoDPoint> temp = this.vertices;
        setPosition(vertices);
        double perimeter = 0;
        for (int i = 0; i < 4; i++) {
            if (i == 3)
                perimeter += Math.hypot(vertices.get(i).coordinates()[0] - vertices.get(0).coordinates()[0], vertices.get(i).coordinates()[1] - vertices.get(0).coordinates()[1]);
            else
                perimeter += Math.hypot(vertices.get(i).coordinates()[0] - vertices.get(i + 1).coordinates()[0], vertices.get(i).coordinates()[1] - vertices.get(i + 1).coordinates()[1]);
        }
        this.vertices = temp;
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

        double x4 = vertices.get(3).coordinates()[0];
        double y4 = vertices.get(3).coordinates()[1];

        vertices = temp;

        return "Quadrilateral[(" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + "), (" + x3 + ", " + y3 + "), (" + x4 + ", " + y4 + ")]";
    }
}
