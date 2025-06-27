/*
 * Karis Jones
 * MoveAction
 * This class determines an action specifically for movement.
 * Move actions extend Action and can have a dedicated direction and speed.
 */

public class MoveAction extends Action {

    private double speed;
    private MoveType moveType;
    private Vector2 targetPosition;
    private Vector2 currentPosition;
    private Enemy enemy;

    public MoveAction(double speed, MoveType moveType, int weight) {
        this.speed = speed;
        this.moveType = moveType;
        this.setWeight(weight);
    }

    @Override
    public boolean checkCondition() {
        Vector2 distance = new Vector2(targetPosition.getX() - currentPosition.getX(), targetPosition.getY() - currentPosition.getY());
        switch (moveType) {
            case TOWARDS: if (distance.magnitude() < 1) return false;
            case AWAY: if (distance.magnitude() > 750) return false;
            case STRAFE_LEFT: if (distance.magnitude() > 600) return false;
            case STRAFE_RIGHT: if (distance.magnitude() > 600) return false;
            default: return true;
        }
    }

    public void setTarget(Vector2 input) {
        targetPosition = input;
    }

    public void setSelf(Enemy e) {
        enemy = e;
        currentPosition = enemy.getPosition();
    }

    @Override
    public void doAction() {
    };


    public Vector2 move(Vector2 currentPosition) {
        switch (moveType) {
            case TOWARDS -> {
                Vector2 velocity = new Vector2(targetPosition.getX() - currentPosition.getX(), targetPosition.getY() - currentPosition.getY());
                velocity.normalize(speed);
                return velocity;
            }
            case AWAY -> {
                Vector2 velocity = new Vector2(targetPosition.getX() - currentPosition.getX(), targetPosition.getY() - currentPosition.getY());
                velocity.normalize(-speed);
                return velocity;
            }
            case STRAFE_LEFT -> {
                Vector2 velocity = new Vector2(targetPosition.getY() - currentPosition.getY(), -(targetPosition.getX() - currentPosition.getX()));
                velocity.normalize(speed);
                return velocity;
            }
            case STRAFE_RIGHT -> {
                Vector2 velocity = new Vector2(-(targetPosition.getY() - currentPosition.getY()), (targetPosition.getX() - currentPosition.getX()));
                velocity.normalize(speed);
                return velocity;
            }
            case STOP -> {
                return new Vector2(0, 0);
            }
        }
        return new Vector2(0, 0);
    };
}
