import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class ClothSimulation extends JPanel {

    static final float GRAVITY = 9.8f;
    static final float TIME_STEP = 0.1f;

    static final int ROW = 15;
    static final int COL = 15;
    static final float REST_DISTANCE = 30.0f;
    static final float CLICK_TOLERANCE = 5.0f;

    private final ArrayList<Particle> particles = new ArrayList<>();
    private final ArrayList<Constraint> constraints = new ArrayList<>();
    private final InputHandler inputHandler;

    public ClothSimulation() {
        setPreferredSize(new Dimension(1080, 640));
        setBackground(Color.BLACK);

        // Create particles
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                float x = col * REST_DISTANCE;
                float y = row * REST_DISTANCE;
                boolean pinned = (row == 0);
                particles.add(new Particle(x, y, pinned));
            }
        }

        // Connect constraints
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                int index = row * COL + col;
                if (col < COL - 1)
                    constraints.add(new Constraint(particles.get(index), particles.get(index + 1)));
                if (row < ROW - 1)
                    constraints.add(new Constraint(particles.get(index), particles.get(index + COL)));
            }
        }

        inputHandler = new InputHandler(constraints, CLICK_TOLERANCE);
        addMouseListener(inputHandler);

        // Center the cloth initially and on resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerCloth();
            }
        });

        // Timer for animation
        Timer timer = new Timer(16, e -> {
            updateSimulation();
            repaint();
        });
        timer.start();
    }

    private void centerCloth() {
        float totalWidth = (COL - 1) * REST_DISTANCE;
        float totalHeight = (ROW - 1) * REST_DISTANCE;

        float startX = (getWidth() - totalWidth) / 2f;
        float startY = (getHeight() - totalHeight) / 2f;

        int i = 0;
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                Particle p = particles.get(i++);
                p.position.x = startX + col * REST_DISTANCE;
                p.position.y = startY + row * REST_DISTANCE;
                p.previousPosition.setLocation(p.position); // Prevent snapping
            }
        }
    }

    private void updateSimulation() {
        for (Particle p : particles) {
            p.applyForce(new java.awt.geom.Point2D.Float(0, GRAVITY));
            p.update(TIME_STEP);
            p.constrainToBounds(getWidth(), getHeight());
        }

        for (int i = 0; i < 5; i++) {
            for (Constraint c : constraints) {
                c.satisfy();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);

        for (Constraint c : constraints) {
            if (!c.active) continue;
            g2.drawLine((int) c.p1.position.x, (int) c.p1.position.y,
                        (int) c.p2.position.x, (int) c.p2.position.y);
        }

        for (Particle p : particles) {
            g2.fillOval((int) (p.position.x - 1), (int) (p.position.y - 1), 2, 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            JLabel label = new JLabel("Cloth(Thread) Simulator");
            label.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setPreferredSize(new Dimension(1080, 30));

            JFrame frame = new JFrame("Cloth Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            ClothSimulation simulation = new ClothSimulation();

            frame.add(label, BorderLayout.NORTH);
            frame.add(simulation, BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}