import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

class Vertex {
  double x, y, z;

  Vertex(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}

class Triangle {
  Vertex v1, v2, v3;
  Color color;

  Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
    this.v1 = v1;
    this.v2 = v2;
    this.v3 = v3;
    this.color = color;
  }

  public static Color getShade(Color color, double shade) {
    double redLinear = Math.pow(color.getRed(), 2.4) * shade;
    double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
    double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

    int red = (int) Math.pow(redLinear, 1 / 2.4);
    int green = (int) Math.pow(greenLinear, 1 / 2.4);
    int blue = (int) Math.pow(blueLinear, 1 / 2.4);

    return new Color(red, green, blue);
  }

  public static List<Triangle> inflate(List<Triangle> tris) {
    List<Triangle> result = new ArrayList<>();

    for (Triangle t : tris) {
      Vertex m1 = new Vertex((t.v1.x + t.v2.x) / 2, (t.v1.y + t.v2.y) / 2, (t.v1.z + t.v2.z) / 2);
      Vertex m2 = new Vertex((t.v2.x + t.v3.x) / 2, (t.v2.y + t.v3.y) / 2, (t.v2.z + t.v3.z) / 2);
      Vertex m3 = new Vertex((t.v1.x + t.v3.x) / 2, (t.v1.y + t.v3.y) / 2, (t.v1.z + t.v3.z) / 2);
      result.add(new Triangle(t.v1, m1, m3, t.color));
      result.add(new Triangle(t.v2, m1, m2, t.color));
      result.add(new Triangle(t.v3, m2, m3, t.color));
      result.add(new Triangle(m1, m2, m3, t.color));
    }
    for (Triangle t : result) {
      for (Vertex v : new Vertex[] { t.v1, t.v2, t.v3 }) {
        double l = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z) / Math.sqrt(30000);
        v.x /= l;
        v.y /= l;
        v.z /= l;
      }
    }
    return result;
  }

}