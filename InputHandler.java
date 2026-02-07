import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class InputHandler implements MouseListener {

    private final List<Constraint> constraints; // a list of all the constraints or threads between particles
    private final double clickTolerance; // a threshold distance within which a mouse click is considered “close enough”
                                         // to a constraint to tear it

    public InputHandler(List<Constraint> constraints, double clickTolerance) { // initializes the handler with a
                                                                               // reference to all constraints and a
                                                                               // tolerance setting.

        this.constraints = constraints;
        this.clickTolerance = clickTolerance;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // gets the mouse click position
        double mouseX = e.getX();
        double mouseY = e.getY();

        Constraint nearest = findNearestConstraint(mouseX, mouseY); // finds the nearest constraint to the mouse click
                                                                    // (within tolerance)
        if (nearest != null) {
            nearest.deactivate();
        }
    }

    private Constraint findNearestConstraint(double mx, double my) {
        Constraint nearestConstraint = null;
        double minDist = clickTolerance;

        for (Constraint c : constraints) {
            double dist = pointToSegmentDistance(mx, my,
                    c.p1.position.x, c.p1.position.y,
                    c.p2.position.x, c.p2.position.y);
            if (dist < minDist) {
                minDist = dist;
                nearestConstraint = c;
            }
        }
        return nearestConstraint;
    }

    private double pointToSegmentDistance(double px, double py, // distance from a point to a line segment
            float x1, float y1, float x2, float y2) { // coordinates of the point (the mouse click)

        // AB: Vector from point A to B (segment vector)
        float ABx = x2 - x1;
        float ABy = y2 - y1;

        // AP: Vector from A to the point P (mouse click)
        double APx = px - x1;
        double APy = py - y1;

        double AB_AB = ABx * ABx + ABy * ABy; // AB . AB
        double AB_AP = ABx * APx + ABy * APy; // AB . AP

        double t = AB_AP / AB_AB; // t is where the projection of the point lies
        if (t < 0) { // means the mouse click is to the left and it is nearest to p1
            return Math.hypot(px - x1, py - y1);
        } else if (t > 1) { // means the mouse click is to the left and it is nearest to p2
            return Math.hypot(px - x2, py - y2);
        } else {
            double projX = x1 + t * ABx;
            double projY = y1 + t * ABy;
            return Math.hypot(px - projX, py - projY);
        }
    }

    // Unused mouse events
    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}