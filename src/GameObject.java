/*
 * Karis Jones
 * GameObject
 * This class creates an game object.
 * Game objects have a position in the world, position on the screen, and a velocity.
 */

public abstract class GameObject {
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 size;

    public Vector2 getPosition() {return position;}
    public Vector2 getVelocity() {return velocity;}
    public Vector2 getSize() {return size;}

//    public Vector2 getCenter() {
//        return new Vector2(position.getX() +);
//    }

    public void setPosition(Vector2 input) {
        position = input;
    }

    public void setSize(Vector2 input) {
        size = input;
    }

    public void setSize(int input) {
        size = new Vector2(input);
    }

    public void setVelocity(Vector2 input) {
        velocity = input;
    }
}
