public class Constraint {
    public Particle p1;
    public Particle p2;
    public double initialLength;
    public boolean active;

    public Constraint(Particle p1, Particle p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.initialLength = p1.position.distance(p2.position);
        this.active = true;
    }

    public void satisfy() {
        if (!active) // if true the constraint will try to maintain distance, otherwise it is torn
            return;

        double dx = p2.position.x - p1.position.x;
        double dy = p2.position.y - p1.position.y;
        double currentLength = Math.sqrt(dx * dx + dy * dy);
        if (currentLength == 0)
            return;

        double difference = (currentLength - initialLength) / currentLength; // what difference it does to an unit, it calculates
        double correctionX = dx * 0.5 * difference;
        double correctionY = dy * 0.5 * difference;

        if (!p1.isPinned) {
            p1.position.x += correctionX;
            p1.position.y += correctionY;
        }
        if (!p2.isPinned) {
            p2.position.x -= correctionX;
            p2.position.y -= correctionY;
        }
    }

    public void deactivate() {
        active = false;
    }
}