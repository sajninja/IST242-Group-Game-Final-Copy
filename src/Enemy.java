/*
 * Karis Jones and Patrick Jin
 * Enemy
 * This class creates an enemy object.
 * Enemies can have a variety of types, and its type determines its parameters like health, size, color, projectiles, and movement and attack AI.
 */

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Enemy extends LivingEntity {

    private int[] actionTimeRange;
    private int actionTimer;
    private ActionTimer moveTimer;
    private ActionTimer shootTimer;
    private Player player;
    private ArrayList<MoveAction> moveActions;
    private ArrayList<ShootAction> shootActions;
    private EnemyType type;

    public Enemy(EnemyType type, Vector2 position, Player player) {
        createFromType(type);
        this.type = type;
        setPosition(position);
        setVelocity(new Vector2(0, 0));
        this.player = player;
    }

    // Patrick Jin
    public Image getSprite() {
        // return Game.imageMap.get("monsterSprite");
       switch (type) {
           case BASIC:
               return Game.imageMap.get("basic");
           case STRONG:
               return Game.imageMap.get("strong");
           case RANGED:
               return Game.imageMap.get("ranged");
           case TANK:
               return Game.imageMap.get("tank");
           default:
               return null;
       }
    }

    public ArrayList<Projectile> shootProjectile() {
        ArrayList<Projectile> shots = new ArrayList<>();
        Projectile p = new Projectile(5, Color.RED, false, 6, 10);
        p.setPosition(this.getPosition());
        p.setSize(10);
        p.setVelocity(Vector2.AtoB(player.getTruePosition(), this.getPosition()));
        p.getVelocity().normalize(6);  // adjust speed as needed

        shots.add(p);
        return shots;
    }

    void createFromType(EnemyType type) {
        switch (type) {
            case BASIC -> createBasic(this);
            case STRONG -> createStrong(this);
            case TANK -> createTank(this);       // new
            case RANGED -> createRanged(this);   // new
        }
    }

    void createBasic(Enemy enemy) {
        enemy.moveTimer = new ActionTimer(new int[]{Game.random.nextInt(20) + 40, Game.random.nextInt(20) + 70});
        enemy.shootTimer = new ActionTimer(new int[]{Game.random.nextInt(10) + 30, Game.random.nextInt(10) + 45});
        enemy.setHealth(50);
        enemy.setSize(30);
        this.shootActions = new ArrayList<>();
        this.shootActions.add(new ShootAction(new Projectile(5, new Color(139, 197, 24), false, 7, 6), ShootType.SINGLE, 1));
        this.moveActions = new ArrayList<>();
        this.moveActions.add(new MoveAction(0, MoveType.STOP, 1));
        this.moveActions.add(new MoveAction(3, MoveType.TOWARDS, 1));
        this.moveActions.add(new MoveAction(3, MoveType.AWAY, 1));
        this.moveActions.add(new MoveAction(3, MoveType.STRAFE_LEFT, 1));
        this.moveActions.add(new MoveAction(3, MoveType.STRAFE_RIGHT, 1));
    }

    void createStrong(Enemy enemy) {
        enemy.moveTimer = new ActionTimer(new int[]{Game.random.nextInt(20) + 60, Game.random.nextInt(20) + 80});
        enemy.shootTimer = new ActionTimer(new int[]{Game.random.nextInt(10) + 30, Game.random.nextInt(20) + 50});
        enemy.setHealth(75);
        enemy.setSize(40);
        this.shootActions = new ArrayList<>();
        this.shootActions.add(new ShootAction(new Projectile(20, new Color(50, 100, 0), false, 3, 30), ShootType.SINGLE, 1));
        this.shootActions.add(new ShootAction(new Projectile(5, new Color(50, 100, 0), false, 5, 10), ShootType.RING, 1));
        this.moveActions = new ArrayList<>();
        this.moveActions.add(new MoveAction(0, MoveType.STOP, 3));
        this.moveActions.add(new MoveAction(2, MoveType.TOWARDS, 1));
        this.moveActions.add(new MoveAction(2, MoveType.AWAY, 1));
        this.moveActions.add(new MoveAction(2, MoveType.STRAFE_LEFT, 1));
        this.moveActions.add(new MoveAction(2, MoveType.STRAFE_RIGHT, 1));
    }

    // Patrick Jin
    void createTank(Enemy enemy) {
        enemy.moveTimer = new ActionTimer(new int[]{Game.random.nextInt(20) + 40, Game.random.nextInt(20) + 70});
        enemy.shootTimer = new ActionTimer(new int[]{Game.random.nextInt(10) + 10, Game.random.nextInt(10) + 25});
        enemy.setHealth(200);
        enemy.setSize(60);
        this.shootActions = new ArrayList<>();
        this.shootActions.add(new ShootAction(new Projectile(10, Color.GRAY, false, 5, 8), ShootType.SINGLE, 2));
        this.shootActions.add(new ShootAction(new Projectile(20, Color.GRAY, false, 3, 12), ShootType.SINGLE, 1));
        this.shootActions.add(new ShootAction(new Projectile(20, Color.GRAY, false, 3, 12), ShootType.RING, 1));
        this.shootActions.add(new ShootAction(new Projectile(5, Color.GRAY, false, 10, 6), ShootType.RING, 1));
        this.shootActions.add(new ShootAction(new Projectile(5, Color.GRAY, false, 10, 6), ShootType.SINGLE, 1));
        this.moveActions = new ArrayList<>();
        this.moveActions.add(new MoveAction(0, MoveType.STOP, 5));
        this.moveActions.add(new MoveAction(1, MoveType.TOWARDS, 1));
        this.moveActions.add(new MoveAction(1, MoveType.AWAY, 1));
        this.moveActions.add(new MoveAction(2, MoveType.STRAFE_LEFT, 1));
        this.moveActions.add(new MoveAction(2, MoveType.STRAFE_RIGHT, 1));
    }

    // Patrick Jin
    void createRanged(Enemy enemy) {
        enemy.moveTimer = new ActionTimer(new int[]{Game.random.nextInt(20) + 10, Game.random.nextInt(20) + 80});
        enemy.shootTimer = new ActionTimer(new int[]{Game.random.nextInt(5) + 10, Game.random.nextInt(5) + 16});
        enemy.setHealth(70);
        enemy.setSize(28);
        this.shootActions = new ArrayList<>();
        this.shootActions.add(new ShootAction(new Projectile(7, Color.MAGENTA, false, 8, 6), ShootType.SINGLE, 5));
        this.shootActions.add(new ShootAction(new Projectile(8, Color.MAGENTA, false, 8, 8), ShootType.SINGLE, 4));
        this.moveActions = new ArrayList<>();
        this.moveActions.add(new MoveAction(0, MoveType.STOP, 1));
        this.moveActions.add(new MoveAction(4, MoveType.TOWARDS, 2));
        this.moveActions.add(new MoveAction(6, MoveType.AWAY, 2));
        this.moveActions.add(new MoveAction(5, MoveType.STRAFE_LEFT, 2));
        this.moveActions.add(new MoveAction(5, MoveType.STRAFE_RIGHT, 2));

    }

    Player player() {
        return player;
    }

    public ActionTimer getMoveTimer() {
        return moveTimer;
    }

    public ActionTimer getShootTimer() {
        return shootTimer;
    }

    public ArrayList<ShootAction> getShootActions() {
        return shootActions;
    }

    public ArrayList<MoveAction> getMoveActions() {
        return moveActions;
    }


    public void act() {
    }

    public EnemyType getType() {
        return this.type;
//    public ArrayList<Projectile> shootProjectile() {
//        return new ArrayList<>();
//    }

//    Enemies that skirmish around the player
//    Enemies that stay still
//    Enemies that move away from the player if near and mvoe towards the player if far
//    Small enemies with low health that move towards allies and shoot healing bullets
    }
}
