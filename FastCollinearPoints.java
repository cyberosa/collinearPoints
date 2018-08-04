import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

/**
 * Class to find all line segments containing 4 or more points
 * @author Castila
 *
 */
public class FastCollinearPoints {
    /** The start-end point positions of the segments */
    private final ArrayList<Integer> startEnd;
    
    /** List with all the involved points in the segments, in order of appearence */
    private final ArrayList<Point> pointsList;
    
    /**
     * Constructor
     * @param myPoints
     */
    public FastCollinearPoints(Point[] points) {
        if (points == null || points.length == 0) {
            throw new IllegalArgumentException("No points were found");
        }
        pointsList = new ArrayList<>();
        startEnd = new ArrayList<>();
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
            Point p0 = myPoints[i];
            // enough points left in the array to find a 4 or more length collinear segment?
            if ((myPoints.length - i) < 4) {
                break;
            }
            Arrays.sort(myPoints, i+1, myPoints.length, p0.slopeOrder());
            int j = i+1;
            while (j < myPoints.length - 2) {
                Point p1 = myPoints[j];
                Point p2 = myPoints[j+1];
                Point p3 = myPoints[j+2];
                if (areCollinear(p0, p1, p2) && areCollinear(p1, p2, p3)) { // segment with the minimum length found
                    ArrayList<Point> tmpList = new ArrayList<>();
                    tmpList.add(p0);
                    tmpList.add(p1);
                    tmpList.add(p2);
                    tmpList.add(p3);
                    // check if longer collinear segment is found 
                    int k = j + 3;
                    boolean collinear = true;
                    while (k < myPoints.length && collinear) {
                        Point p4 = myPoints[k];
                        collinear = areCollinear(p2, p3, p4);
                        if (collinear) {
                            tmpList.add(p4);
                            p2 = p3;
                            p3 = p4;
                        }
                        k++;
                    }
                    checkSegmentAndInsert(tmpList);
                    j = j + 3;
                } else {
                    j = j + 1;
                }
            } // end for int j
            Arrays.sort(myPoints); // restore the original order
        }
    }
    
    /**
     * Method to check if the segment is a child of another existing segment
     * @param list the list of points to check
     */
    private void checkSegmentAndInsert(ArrayList<Point> list) {
        boolean addSegment = false;
        boolean found = false;
        for (int i = 0; i < (startEnd.size() - 1); i = i + 2) {
            int begin = startEnd.get(i);
            int end = startEnd.get(i+1);
            List<Point> storedSegment = pointsList.subList(begin, end+1);
            found = false;
            if (storedSegment.size() >= list.size()) {
                 found = storedSegment.containsAll(list);
                 if (found) { // the new list is shorter
                     break;
                 }
            } else { // the new list is longer
                 found = list.containsAll(storedSegment);
                 if (found) { // delete the stored segment since it is shorter
                     deleteSegment(begin, end, i);
                     addSegment = true;
                 }
            }
        }
        
        if (!found || addSegment) {
            startEnd.add(pointsList.size());
            pointsList.addAll(list);
            startEnd.add(pointsList.size() - 1); // the last element on the list
        }
    }
    
    /**
     * Method to delete a redundant segment that was added before
     */
    private void deleteSegment(int start, int end, int startEndPosition) {
        // delete the points in descending order
        for (int i = end; i >= start; i--) {
            pointsList.remove(i);
        }
        // remove the reference indexes
        startEnd.remove(startEndPosition+1);
        startEnd.remove(startEndPosition);
    }
    
    /**
     * Method to count the number of segments found
     * @return the number of line segments
     */
    public int numberOfSegments() {
        return segments().length;
    }
    
    /**
     * The line segments that were found
     * @return an array with the segments
     */
    public LineSegment[] segments() {
        LineSegment[] copy = new LineSegment[startEnd.size()/2];
        int index = 0;
        for (int i = 0; i < startEnd.size() - 1; i = i + 2) {
            copy[index++] = new LineSegment(pointsList.get(startEnd.get(i)), pointsList.get(startEnd.get(i+1)));
        }
        return copy;
    }
    
    /** 
     * Method to check if three points are collinear
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return true if they are collinear
     */
    private boolean areCollinear(Point p1, Point p2, Point p3) {
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
