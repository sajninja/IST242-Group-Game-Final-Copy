/*
 * Karis Jones
 * Player
 * This class creates the player.
 * The player has an acceleration that helps them move around the game world more naturally. They have a "true position" to make the scrolling camera work with world positions.
 */

public class Player extends LivingEntity {
    private final double ACCELERATION = 2;
    public Player(Vector2 position) {
        this.setPosition(position);
    }

    public double getAcceleration() {
        return ACCELERATION;
    }

    public Vector2 getTruePosition() {
        return new Vector2(getPosition().getX() + (double) Game.WIDTH / 2, getPosition().getY() + (double) Game.HEIGHT / 2);
    }
}
