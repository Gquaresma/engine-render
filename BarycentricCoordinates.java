public class BarycentricCoordinates {
  public double b1, b2, b3;

  public BarycentricCoordinates(double b1, double b2, double b3) {
      this.b1 = b1;
      this.b2 = b2;
      this.b3 = b3;
  }

  public static BarycentricCoordinates calculateBarycentricCoordinates(Vertex v1, Vertex v2, Vertex v3, int x, int y, double triangleArea) {
    double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
    double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
    double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
  
    return new BarycentricCoordinates(b1, b2, b3);
  }
}

