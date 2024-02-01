import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    Container pane = frame.getContentPane();
    pane.setLayout(new BorderLayout());

    JSlider xSlider = new JSlider(0, 360, 180);
    pane.add(xSlider, BorderLayout.SOUTH);

    JSlider ySlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
    pane.add(ySlider, BorderLayout.EAST);

    JPanel renderPanel = new JPanel() {
      public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        List<Triangle> tris = new ArrayList<>();
        tris.add(new Triangle(new Vertex(100, 100, 100),
            new Vertex(-100, -100, 100),
            new Vertex(-100, 100, -100),
            Color.WHITE));
        tris.add(new Triangle(new Vertex(100, 100, 100),
            new Vertex(-100, -100, 100),
            new Vertex(100, -100, -100),
            Color.RED));
        tris.add(new Triangle(new Vertex(-100, 100, -100),
            new Vertex(100, -100, -100),
            new Vertex(100, 100, 100),
            Color.GREEN));
        tris.add(new Triangle(new Vertex(-100, 100, -100),
            new Vertex(100, -100, -100),
            new Vertex(-100, -100, 100),
            Color.BLUE));

        for (int i = 0; i < 4; i++) {
          tris = Triangle.inflate(tris);
        }

        // rotação dos eixos
        double heading = Math.toRadians(xSlider.getValue());
        Matrix headingTransform = new Matrix(new double[] {
            Math.cos(heading), 0, -Math.sin(heading),
            0, 1, 0,
            Math.sin(heading), 0, Math.cos(heading)
        });
        double pitch = Math.toRadians(ySlider.getValue());
        Matrix pitchTransform = new Matrix(new double[] {
            1, 0, 0,
            0, Math.cos(pitch), Math.sin(pitch),
            0, -Math.sin(pitch), Math.cos(pitch)
        });
        Matrix transform = headingTransform.multiply(pitchTransform);

        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        double[] zBuffer = new double[img.getWidth() * img.getHeight()];

        for (int q = 0; q < zBuffer.length; q++) {
          zBuffer[q] = Double.NEGATIVE_INFINITY;
        }

        for (Triangle t : tris) {
          Vertex v1 = transform.transform(t.v1);
          Vertex v2 = transform.transform(t.v2);
          Vertex v3 = transform.transform(t.v3);

          // Translação dos eixos
          v1.x += getWidth() / 2;
          v1.y += getHeight() / 2;
          v2.x += getWidth() / 2;
          v2.y += getHeight() / 2;
          v3.x += getWidth() / 2;
          v3.y += getHeight() / 2;

          Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
          Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
          Vertex norm = new Vertex(ab.y * ac.z - ab.z * ac.y, ab.z * ac.x - ab.x * ac.z, ab.x * ac.y - ab.y * ac.x);

          double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
          norm.x /= normalLength;
          norm.y /= normalLength;
          norm.z /= normalLength;

          double angleCos = Math.abs(norm.z);

          // Limites do triângulo
          BoundingBox bbox = BoundingBox.calculateBoundingBox(img, v1, v2, v3);

          double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

          for (int y = bbox.minY; y <= bbox.maxY; y++) {
            for (int x = bbox.minX; x <= bbox.maxX; x++) {
              BarycentricCoordinates barycentricCoordinates = BarycentricCoordinates.calculateBarycentricCoordinates(v1,
                  v2, v3, x, y, triangleArea);

              if (barycentricCoordinates.b1 >= 0 && barycentricCoordinates.b1 <= 1 && barycentricCoordinates.b2 >= 0
                  && barycentricCoordinates.b2 <= 1 && barycentricCoordinates.b3 >= 0
                  && barycentricCoordinates.b3 <= 1) {
                double depth = barycentricCoordinates.b1 * v1.z + barycentricCoordinates.b2 * v2.z
                    + barycentricCoordinates.b3 * v3.z;
                int zIndex = y * img.getWidth() + x;
                if (zBuffer[zIndex] < depth) {
                  img.setRGB(x, y, Triangle.getShade(t.color, angleCos).getRGB());
                  zBuffer[zIndex] = depth;
                }
              }
            }
          }

        }

        g2.drawImage(img, 0, 0, null);
      }
    };
    pane.add(renderPanel, BorderLayout.CENTER);

    xSlider.addChangeListener(e -> renderPanel.repaint());
    ySlider.addChangeListener(e -> renderPanel.repaint());

    frame.setSize(700, 500);
    frame.setVisible(true);

  }
}