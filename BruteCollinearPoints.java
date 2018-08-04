import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

/**
 * Class to examine 4 points at a time and to check whether they all lie on the same line segment, 
 * returning all such line segments
 */
public class BruteCollinearPoints {
    /** The line segments found as a list of points */
    private final ArrayList<Point> segmentsList;
    
    /**
     * Constructor finds all collinear line segments containing 4 points
     * @param myPoints the 4 points
     */
    public BruteCollinearPoints(Point[] points) {
        if (points == null || points.length == 0) {
            throw new IllegalArgumentException("No points were found");
        }
        segmentsList = new ArrayList<>();
        ArrayList<Point> tmpPoints = new ArrayList<Point>();
        for (Point p : points) {
            if (p == null) {
                throw new IllegalArgumentException("Null pointer was found");              
            }
            if (tmpPoints.contains(p)) {
                throw new IllegalArgumentException("Two equal points were found");
            }
            tmpPoints.add(p);
        }
        // sort first the points in ascending order
        Point[] myPoints =  points.clone();
        Arrays.sort(myPoints);
        // build line segments while checking not null /not repeated element
        for (int i = 0; i < myPoints.length; i++) {
            // repeated elements have consecutive positions in the ordered array
            if (i > 0) {
                if (myPoints[i].equals(myPoints[i-1])) {
                    throw new IllegalArgumentException("Two equal points were found");
                }
            }
            for (int j = i + 1; j < myPoints.length - 2; j++) {
                for (int k = j + 1; k < myPoints.length; k++) {
                    // three different elements
                    if (areCollinear(myPoints[i], myPoints[j], myPoints[k])) {
                        // traverse to find the fourth one
                        for (int m = k + 1; m < myPoints.length; m++) {
                            if (myPoints[m] == null) {
                                throw new IllegalArgumentException("A null point was found");
                            }
                            if (areCollinear(myPoints[j], myPoints[k], myPoints[m])) {
                                // 4 collinear points found
                                segmentsList.add(myPoints[i]);
                                segmentsList.add(myPoints[j]);
                                segmentsList.add(myPoints[k]);
                                segmentsList.add(myPoints[m]);
                            }
                        } // end for m
                    }
                } // end for k
            } // end for j
        } // end for i
    }
    
    /** 
     * Method to check if three points are collinear
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return true if they are collinear
     */
    private boolean areCollinear(final Point p1, final Point p2, final Point p3) {
        double slope1 = p1.slopeTo(p2);
        double slope2 = p2.slopeTo(p3);
        // edge cases
        if (slope1 == Double.POSITIVE_INFINITY) {
            return slope2 == Double.POSITIVE_INFINITY;
        }
        if (slope1 == Double.NEGATIVE_INFINITY || slope2 == Double.NEGATIVE_INFINITY) {
            throw new IllegalArgumentException("Equal points were found");
        }
        return (Math.abs(slope1 - slope2) < 0.000001);
    }
    
    /**
     * Method to count the number of segments found
     * @return the number of line segments
     */
    public int numberOfSegments() {
        return segmentsList.size()/4;
    }
    
    /**
     * The line segments that were found
     * @return an array with the segments
     */
    public LineSegment[] segments() {
        LineSegment[] copy = new LineSegment[segmentsList.size() / 4];
        int index = 0;
        for (int i = 0; i < segmentsList.size() - 3; i = i + 4) {
            copy[index++] = new LineSegment(segmentsList.get(i), segmentsList.get(i + 3));
        }
        return copy;
    }
    
    /**
     * Unit tests the BruteCollinearPoints data type.
     */
    public static void main(String[] args) {
     // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
