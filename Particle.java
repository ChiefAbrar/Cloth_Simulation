import java.awt.geom.Point2D;

public class Particle {
    public Point2D.Float position;
    public Point2D.Float previousPosition;
    public Point2D.Float acceleration;
    public boolean isPinned;

    public Particle(float x, float y, boolean pinned) {
        position = new Point2D.Float(x, y);
        previousPosition = new Point2D.Float(x, y);
        acceleration = new Point2D.Float(0, 0);
        isPinned = pinned;
    }

    public void applyForce(Point2D.Float force) {
        if (!isPinned) {
            acceleration.x += force.x;
            acceleration.y += force.y;
        }
    }

    public void update(float timeStep) {
        if (!isPinned) {
            float vx = position.x - previousPosition.x;
            float vy = position.y - previousPosition.y;

            previousPosition.x = position.x;
            previousPosition.y = position.y;

            position.x += vx + acceleration.x * timeStep * timeStep;
            position.y += vy + acceleration.y * timeStep * timeStep;

            acceleration.x = 0;
            acceleration.y = 0;
        }
    }

    public void constrainToBounds(float width, float height) {
        if (position.x < 0)
            position.x = 0;
        if (position.x > width)
            position.x = width;
        if (position.y < 0)
            position.y = 0;
        if (position.y > height)
            position.y = height;
    }
}