/*
 * Karis Jones
 * Vector2
 * This class creates an object that stores two doubles.
 * 2D vectors can be used to represent position, velocity, and size. Also includes a few vector operation methods
 */

public class Vector2 {
    private double x;
    private double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(double input) {
        this.x = input;
        this.y = input;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void normalize() {
        double magnitude = magnitude();
        System.out.println("magnitude is " + magnitude);
        System.out.println(y + " " + y / magnitude);
        x /= magnitude;
        y /= magnitude;
    }

    public void scale(double scale) {
        x *= scale;
        y *= scale;
    }

    public Vector2 scaled(double scale) {
        x *= scale;
        y *= scale;
        return new Vector2(x, y);
    }

    public void normalize(double scalar) {
        double magnitude = magnitude();
        x /= magnitude;
        y /= magnitude;
        x *= scalar;
        y *= scalar;
    }

    public static Vector2 AtoB(Vector2 A, Vector2 B) {
        return new Vector2(A.getX() - B.getX(), A.getY() - B.getY());
    }

    public static Vector2 add(Vector2 A, Vector2 B) {
        return new Vector2(A.getX() + B.getX(), A.getY() + B.getY());
    }

    public static Vector2 subtract(Vector2 A, Vector2 B) {
        return new Vector2(A.getX() - B.getX(), A.getY() - B.getY());
    }
}
