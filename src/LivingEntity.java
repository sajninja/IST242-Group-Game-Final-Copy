/*
 * Karis Jones
 * LivingEntity
 * This class creates a living game object.
 * Living entities, like players and enemies, have health that can be modified.
 */

public abstract class LivingEntity extends GameObject {
    private int health;

    public int getHealth() {
        return health;
    }

    public void setHealth(int input) {
        health = input;
    }

    public void hit(Projectile proj) {
        changeHealth(-proj.damage);
    }

    public void changeHealth(int input) {
        health += input;
    }
}
