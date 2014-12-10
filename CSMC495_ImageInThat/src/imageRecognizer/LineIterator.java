package imageRecognizer;

import java.util.List;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

/**
 * Bresenham's algorithm to find all pixels on a Line2D.
 * @author nes
 * from: https://snipt.net/nikoschwarz/iterate-all-points-on-a-line-using-bresenhams-algorithm/
 */

public class LineIterator implements Iterator<Point2D> {
	final static double DEFAULT_PRECISION = 1.0;
	final Line2D line;
	final double precision;
	
	final double sx, sy;
	final double dx, dy;
	
	double x,y,error;
	
	public LineIterator(Line2D line, double precision) {
		this.line = line;
		this.precision = precision;

		sx = line.getX1() < line.getX2() ? precision : -1 * precision;
		sy = line.getY1() < line.getY2() ? precision : -1 * precision;
			 
		dx =  Math.abs(line.getX2() - line.getX1());
		dy = Math.abs(line.getY2() - line.getY1());
		
		error = dx - dy;
		
		y = line.getY1();
		x = line.getX1();
	}
	
	public LineIterator(Line2D line) {
		this(line, DEFAULT_PRECISION);
	}
	
	@Override
	public boolean hasNext() {
	    return Math.abs( x - line.getX2()) > 0.9 || ( Math.abs(y - line.getY2()) > 0.9);
	}

	@Override
	public Point2D next() {
		Point2D ret = new Point2D.Double(x, y);
		
		double e2 = 2*error;
		if (e2 > -dy) {
			error -= dy;
			x += sx;
		}
		if (e2 < dx) {
			error += dx;
			y += sy;
		}
		
		return ret;
	}

	@Override
	public void remove() {
		throw new AssertionError();
	}
}

//class IteratorTest {
//    @Test
//    public void testIterator() {
//		List<Point2D> ary = new ArrayList<Point2D>();
//		Line2D line = new Line2D.Double(0,0,8,4);
//		Point2D current;
//		for(Iterator<Point2D> iter = new LineIterator(line); iter.hasNext();) {
//			current =iter.next();
//			ary.add(current);
//		}
//		assertThat(ary.toString(), 
//				is("[Point2D.Double[0.0, 0.0], Point2D.Double[1.0, 0.0], " +
//						"Point2D.Double[2.0, 1.0], Point2D.Double[3.0, 1.0], " +
//						"Point2D.Double[4.0, 2.0], Point2D.Double[5.0, 2.0], " +
//						"Point2D.Double[6.0, 3.0], Point2D.Double[7.0, 3.0]]"));
//	}
//	
//	@Test
//	public void testIterator2() {
//		List<Point2D> ary = new ArrayList<Point2D>();
//		Line2D line = new Line2D.Double(new Point2D.Double(4, 4), new Point2D.Double(10, 0));
//		Point2D current;
//		for(Iterator<Point2D> iter = new LineIterator(line); iter.hasNext();) {
//			current =iter.next();
//			ary.add(current);
//		}
//		assertThat(ary.toString(), 
//				is("[Point2D.Double[4.0, 4.0], Point2D.Double[5.0, 3.0], Point2D.Double[6.0, 3.0], " +
//						"Point2D.Double[7.0, 2.0], Point2D.Double[8.0, 1.0], Point2D.Double[9.0, 1.0]]"));
//	}
//	
//	@Test
//	public void testIterator3() {
//		List<Point2D> ary = new ArrayList<Point2D>();
//		Line2D line = new Line2D.Double(new Point2D.Double(0, 0),
//				new Point2D.Double(100, 0));
//		Point2D current;
//		for (Iterator<Point2D> iter = new LineIterator(line); iter.hasNext();) {
//			current = iter.next();
//			ary.add(current);
//		}
//		assertThat(ary.size(), is(100));
//	}
//}