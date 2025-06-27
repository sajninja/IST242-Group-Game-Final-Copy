import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game extends JFrame implements KeyListener {
    //Patrick Jin
    private void playSound(String filename) {
        try {
            File soundFile = new File("assets/" + filename);
            javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(soundFile);
            javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Utility
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 750;
    private final JPanel gamePanel;
    public static final Random random = new Random();

    //    Media
    public static Map<String, Image> imageMap = new HashMap<>();
    String[] images = new String[] {
            "wizardSprite", "strongSprite", "tankSprite", "rangedSprite", "basicSprite", "monsterSprite"
    };

    private Image tileBackground;
    private final int tileSize = 256;
    private Image skyLayer, fogLayer, rockLayer, lavaLayer;

    private void drawParallaxLayer(Graphics g, Image layer, double parallax, int screenWidth, int screenHeight) {
        if (layer == null) return;

        int imgWidth = (int) (layer.getWidth(null) * 0.9);
        int imgHeight = (int) (layer.getHeight(null) * 0.9);

        int xOffset = (int)(-player.getPosition().getX() * parallax) % imgWidth;
        int yOffset = (int)(-player.getPosition().getY() * parallax) % imgHeight;

        // Ensure offset is positive for wrapping
        if (xOffset > 0) xOffset -= imgWidth;
        if (yOffset > 0) yOffset -= imgHeight;

        for (int x = xOffset; x < screenWidth; x += imgWidth) {
            for (int y = yOffset; y < screenHeight; y += imgHeight) {
                g.drawImage(layer, x, y, null);
            }
        }
    }

    //    Keys
    private Map<Integer, Boolean> keyMap = new HashMap<>();

    //    Objects
    public ArrayList<GameObject> nonPlayerObjects;
    public ArrayList<Projectile> projectiles;
    public static Player player;
    private int currentLevel = 1;
    private boolean gameOver = false;


    private final int projectileCooldown = 10; // ticks between shots
    private boolean mousePressed = false;


    public Point crosshair = new Point();

    //    This value needs to be even or it won't look right
    private static final int strokeLength = 8;

    private int playerShootCooldown;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Game().setVisible(true);
            }
        });
    }

    public Game() {
        setTitle("Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        try {
            tileBackground = ImageIO.read(new File("assets/background.png")); // name it accordingly
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

//        Initialize lists
        nonPlayerObjects = new ArrayList<>();
        projectiles = new ArrayList<>();

//        Create objects
        player = new Player(new Vector2(0, 0));
        player.setVelocity(new Vector2(0, 0));
        player.setSize(30);
        player.setHealth(100);
        playerShootCooldown = 0;

        spawnEnemiesForLevel(currentLevel);

//        Initialize key map
        for (int i = 0; i < 255; i++) {
            keyMap.put(i, false);
        }

        try {
            for (String s : images) {
                File file = new File("assets/" + s + ".png");
                // Patrick Jin - null checks
                if (!file.exists()) {
                    System.out.println("missing file: " + file.getAbsolutePath());
                } else {
                    imageMap.put(s, ImageIO.read(file));
                    System.out.println("loaded: " + file.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1); // stop game if image loading fails
        }

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });

        gamePanel.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent me) {
                crosshair.x = me.getX();
                crosshair.y = me.getY();
                gamePanel.repaint();
            }
        });

        //                Add a game win/lose condition here?
        Timer timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                Add a game win/lose condition here?
                update();
                gamePanel.repaint();
            }
        });
        timer.start();

        add(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);
    }
    private void spawnEnemiesForLevel(int level) {
        nonPlayerObjects.clear(); // remove previous enemies

        int numEnemies = 3 + level / 3;
        for (int i = 0; i < numEnemies; i++) {
            int type = random.nextInt(4);
            int x = random.nextInt(800) - 400;
            int y = random.nextInt(800) - 400;
            switch (type) {
                case 0: nonPlayerObjects.add(new Enemy(EnemyType.BASIC, new Vector2(x, y), player));
                    break;
                case 1: nonPlayerObjects.add(new Enemy(EnemyType.BASIC, new Vector2(x, y), player));
                    break;
                case 2: nonPlayerObjects.add(new Enemy(EnemyType.STRONG, new Vector2(x, y), player));
                    break;
                case 3: nonPlayerObjects.add(new Enemy(EnemyType.RANGED, new Vector2(x, y), player));
                    break;
            }
        }

        int x = random.nextInt(800) - 400;
        int y = random.nextInt(800) - 400;

        if (level % 5 == 0) {
                int bosses = level / 5;
                for (int j = 0; j < bosses; j++) {
            nonPlayerObjects.add(new Enemy(EnemyType.TANK, new Vector2(x, y), player));
                }
        }

//        switch (level) {
//            case 1:
//                nonPlayerObjects.add(new Enemy(EnemyType.BASIC, new Vector2(200, 200), player));
//                nonPlayerObjects.add(new Enemy(EnemyType.BASIC, new Vector2(400, 200), player));
//                break;
//            case 2:
//                nonPlayerObjects.add(new Enemy(EnemyType.BASIC, new Vector2(200, 200), player));
//                nonPlayerObjects.add(new Enemy(EnemyType.STRONG, new Vector2(400, 200), player));
//                break;
//            case 3:
//                nonPlayerObjects.add(new Enemy(EnemyType.RANGED, new Vector2(150, 200), player));
//                nonPlayerObjects.add(new Enemy(EnemyType.BASIC, new Vector2(350, 200), player));
//                nonPlayerObjects.add(new Enemy(EnemyType.STRONG, new Vector2(550, 200), player));
//                break;
//            case 4:
//                nonPlayerObjects.add(new Enemy(EnemyType.TANK, new Vector2(300, 250), player));
//                nonPlayerObjects.add(new Enemy(EnemyType.RANGED, new Vector2(500, 200), player));
//                break;
//            default:
//                for (int i = 0; i < level; i++) {
//                    EnemyType type = EnemyType.values()[random.nextInt(EnemyType.values().length)];
//                    nonPlayerObjects.add(new Enemy(type, new Vector2(100 + i * 100, 150), player));
//                }
//                break;
//        }
    }

    void draw(Graphics g) {
        // FIRST: draw background
        if (tileBackground != null) {
            int bgWidth = tileBackground.getWidth(null);
            int bgHeight = tileBackground.getHeight(null);

            int xOffset = (int) (-player.getPosition().getX() % bgWidth);
            int yOffset = (int) (-player.getPosition().getY() % bgHeight);

            for (int x = -bgWidth; x < WIDTH + bgWidth; x += bgWidth) {
                for (int y = -bgHeight; y < HEIGHT + bgHeight; y += bgHeight) {
                    g.drawImage(tileBackground, x + xOffset, y + yOffset, null);
                }
            }
        }
        // --- Draw background layers in parallax order ---
        drawParallaxLayer(g, skyLayer, 0.25, WIDTH, HEIGHT);   // Farthest, moves slowest
        drawParallaxLayer(g, fogLayer, 0.4, WIDTH, HEIGHT);    // Light fog drift
        drawParallaxLayer(g, rockLayer, 0.75, WIDTH, HEIGHT);  // Base ground layer
        drawParallaxLayer(g, lavaLayer, 1.0, WIDTH, HEIGHT);   // Closest, moves fastest


        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER!", WIDTH / 2 - 150, HEIGHT / 2);
            return;
        }

        g.setColor(darkened(Color.green));
        g.fillRect(WIDTH / 2 - player.getHealth() / 2 - strokeLength / 2, HEIGHT / 2 - (int) player.getSize().getY() - strokeLength / 2 - 5, player.getHealth() + strokeLength, 8 + strokeLength);
        g.setColor(Color.green);
        g.fillRect(WIDTH / 2 - player.getHealth() / 2, HEIGHT / 2 - (int) player.getSize().getY() - 5, player.getHealth(), 8);
        g.setColor(new Color(16, 72, 137));
        Image playerImg = imageMap.get("wizardSprite");
        if (playerImg != null) {
            // Patrick Jin - draw shadow
            g.setColor(new Color(0, 0, 0, 100)); // translucent black
            g.fillOval(
                    WIDTH / 2 - (int) player.getSize().getX() / 2,
                    HEIGHT / 2 - (int) player.getSize().getY() / 2 + 25, // shifted downward
                    (int) player.getSize().getX(),
                    (int) player.getSize().getY() / 3
            );
            g.drawImage(playerImg,
                    WIDTH / 2 - (int) player.getSize().getX() / 2,
                    HEIGHT / 2 - (int) player.getSize().getY() / 2,
                    (int) player.getSize().getX(),
                    (int) player.getSize().getY(),
                    null);
        } else {
            // fallback: original oval if image is missing
            g.setColor(new Color(16, 72, 137));
            g.fillOval(
                    WIDTH / 2 - (int) player.getSize().getX() / 2,
                    HEIGHT / 2 - (int) player.getSize().getY() / 2,
                    (int) player.getSize().getX(),
                    (int) player.getSize().getY()
            );
        }
//        g.fillOval((int) (player.getTruePosition().getX()), (int) (player.getTruePosition().getY()), 20, 20);
        for (GameObject o : nonPlayerObjects) {
            if (o instanceof Enemy) {
                Enemy enemy = (Enemy) o;
                Image img = enemy.getSprite();
                int x = considerPlayerX(enemy.getPosition()) - (int) enemy.getSize().getX() / 2;
                int y = considerPlayerY(enemy.getPosition()) - (int) enemy.getSize().getY() / 2;
                int w = (int) enemy.getSize().getX();
                int h = (int) enemy.getSize().getY();

                // Patrick Jin - draw shadow
                g.setColor(new Color(0, 0, 0, 100)); // translucent black
                g.fillOval(x, y + 10, w, h / 3);

                if (img != null) {
                    g.drawImage(img, x, y, w, h, null);
                } else {
                    g.setColor(darkened(new Color(176, 13, 51)));
                    g.fillOval(x - strokeLength / 2, y - strokeLength / 2, w + strokeLength, h + strokeLength);
                    g.setColor(new Color(176, 13, 51));
                    g.fillOval(x, y, w, h);
                }

                // health bar
                int health = ((Enemy) o).getHealth();
                int healthBarX = x + w / 2 - health / 2;
                int healthBarY = y - 10;
                g.setColor(darkened(Color.red));
                g.fillRect(healthBarX - strokeLength / 2, healthBarY - strokeLength / 2, health + strokeLength, 8 + strokeLength);
                g.setColor(Color.red);
                g.fillRect(healthBarX, healthBarY, health, 8);
            }
        }

        boolean allEnemiesDead = true;
        for (GameObject o : nonPlayerObjects) {
            if (o instanceof Enemy) {
                allEnemiesDead = false;
                break;
            }
        }
        if (allEnemiesDead) {
            currentLevel++;
//            if (currentLevel > 4) { // you currently have 4 levels
//                gameOver = true;
//            } else {
                spawnEnemiesForLevel(currentLevel);
//            }
        }

        for (Projectile p : projectiles) {
            g.setColor(darkened(p.getColor()));
            g.fillOval(considerPlayerX(p.getPosition()) - (int) p.getSize().getX() / 2 - strokeLength / 2, considerPlayerY(p.getPosition()) - (int) p.getSize().getY() / 2 - strokeLength / 2, (int) p.getSize().getX() + strokeLength, (int) p.getSize().getY() + strokeLength);
            g.setColor(p.getColor());
            g.fillOval(considerPlayerX(p.getPosition()) - (int) p.getSize().getX() / 2, considerPlayerY(p.getPosition()) - (int) p.getSize().getY() / 2, (int) p.getSize().getX(), (int) p.getSize().getY());
//            g.fillOval((int) (p.getPosition().getX() - player.getPosition().getX()), (int) (p.getPosition().getY() - player.getPosition().getY()), 7, 7);
        }

        g.setColor(new Color(16, 72, 137));
        if (crosshair != null) {
//            Clamp the crosshair's position near the player? Somehow?
//            Vector2 crosshairPos = new Vector2(crosshair.x, crosshair.y);
//            Vector2 distance = Vector2.AtoB(crosshairPos, player.getPosition());
//            if (distance.magnitude() > 100) {
//                crosshairPos.sc
//            }
//            g.fillRect(crosshair.x - 10, crosshair.y - 1, 20, 2);
//            g.fillRect(crosshair.x - 1, crosshair.y - 10, 2, 20);
        }
    }

    //    A neat pair of methods that handle some vector operations for drawing
    int considerPlayerX(Vector2 input) {
        return (int) (input.getX() - player.getPosition().getX());
    }

    int considerPlayerY(Vector2 input) {
        return (int) (input.getY() - player.getPosition().getY());
    }

    void update() {
        if (gameOver) return;

        if (player.getHealth() <= 0) gameOver = true;
        if (keyMap.get(KeyEvent.VK_W)) player.setVelocity(new Vector2(player.getVelocity().getX(), player.getVelocity().getY() - player.getAcceleration()));
        if (keyMap.get(KeyEvent.VK_S)) player.setVelocity(new Vector2(player.getVelocity().getX(), player.getVelocity().getY() + player.getAcceleration()));
        if (keyMap.get(KeyEvent.VK_A)) player.setVelocity(new Vector2(player.getVelocity().getX() - player.getAcceleration(), player.getVelocity().getY()));
        if (keyMap.get(KeyEvent.VK_D)) player.setVelocity(new Vector2(player.getVelocity().getX() + player.getAcceleration(), player.getVelocity().getY()));

        if (player.getVelocity().magnitude() > 7.5) {
            player.getVelocity().normalize(7.5);
        }
//        Change the player's position in the world

        if (playerShootCooldown > 0) playerShootCooldown--;


        player.setPosition(new Vector2(player.getPosition().getX() + player.getVelocity().getX(), player.getPosition().getY() + player.getVelocity().getY()));
        player.getVelocity().scale(0.85);

        for (GameObject o : nonPlayerObjects) {
            if (o instanceof Enemy) {
//                KARIS NEW ->
//              Change its position on the screen
                o.setPosition(new Vector2(o.getPosition().getX() + o.getVelocity().getX(), o.getPosition().getY() + o.getVelocity().getY()));
//                o.setScreenPosition(new Vector2(o.getPosition().getX() - player.getPosition().getX(), o.getPosition().getY() - player.getPosition().getY()));
//                Nothing yet to change its position in the world...
                if (((Enemy) o).getShootTimer().getTime() == 0) {
                    int shootIndex = weightedDraw(((Enemy) o).getShootActions());
                    for (Projectile p : ((Enemy) o).getShootActions().get(shootIndex).shootProjectile(o.getPosition())) {
                        projectiles.add(p);
                    }
                    ((Enemy) o).getShootTimer().reset();
                } else {
                    ((Enemy) o).getShootTimer().setTime(((Enemy) o).getShootTimer().getTime() - 1);
                }

                if (((Enemy) o).getMoveTimer().getTime() == 0) {
                    ArrayList<MoveAction> approvedActions = new ArrayList<>();
                    for (MoveAction a : ((Enemy) o).getMoveActions()) {
                        a.setTarget(player.getTruePosition());
                        a.setSelf((Enemy) o);
                        if (a.checkCondition()) approvedActions.add(a);
                    }
                    if (!approvedActions.isEmpty()) {
                        int moveIndex = weightedDraw(approvedActions);
                        o.setVelocity(approvedActions.get(moveIndex).move(o.getPosition()));
                    }
                    ((Enemy) o).getMoveTimer().reset();
                } else {
                    ((Enemy) o).getMoveTimer().setTime(((Enemy) o).getMoveTimer().getTime() - 1);
                }
//                <-
            }
        }

        // Patrick Jin - added sound functions
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            p.life--;
            if (p.life <= 0) {
                projectiles.remove(p);
                i--;
            } else
            {
                if (!p.isFriendly()) {
                    double distanceToPlayer = new Vector2(p.getPosition().getX() - (player.getTruePosition().getX()), p.getPosition().getY() - (player.getTruePosition().getY())).magnitude();
                    if (distanceToPlayer < player.getSize().magnitude() * 0.65) {
//                        player.hit(p);
                        playSound("playerDamage.wav");
                        if (player.getHealth() < 0) {
                            gameOver = true;
                            player.setSize(0);
                        }
                        projectiles.remove(p);
                        i--;
                    }
                } else {
                    for (int j = 0; j < nonPlayerObjects.size(); j++) {
                        GameObject o = nonPlayerObjects.get(j);
                        if (o instanceof Enemy) {
                            double distanceToEnemy = new Vector2(p.getPosition().getX() - (o.getPosition().getX()), p.getPosition().getY() - (o.getPosition().getY())).magnitude();
//                        Use a value here for enemies with different sizes
                            if (distanceToEnemy < o.getSize().magnitude() * 0.8) {
                                ((Enemy) o).hit(p);
                                playSound("monsterPain.wav");
                                if (((Enemy) o).getHealth() <= 0) {
                                    nonPlayerObjects.remove(o);
                                    j--;
                                }
                                projectiles.remove(p);
                                i--;
                            }
                        }
                    }
                }

                p.setPosition(new Vector2(p.getPosition().getX() + p.getVelocity().getX(), p.getPosition().getY() + p.getVelocity().getY()));
            }

//            Add something here to cull projectiles that have gone too far
        }

//         Patrick Jin
        if (mousePressed) {
            if (playerShootCooldown == 0) {
                playerShootCooldown = 10;
//                Karis Jones - projectile creation
                Projectile projectile = new Projectile(5, new Color(0, 150, 200), true, 12, 10);
                projectile.setPosition(player.getTruePosition());
                projectile.setSize(10);
                projectile.setVelocity(Vector2.AtoB(
                        new Vector2(crosshair.x, crosshair.y),
                        new Vector2((double) WIDTH / 2, (double) HEIGHT / 2)
                ));
                int inAccuracy = 20;
                projectile.setVelocity(Vector2.add(projectile.getVelocity(), new Vector2(random.nextDouble(inAccuracy) - (double) inAccuracy / 2)));
                projectile.getVelocity().normalize(12);
                projectile.setFriendly(true);
                projectiles.add(projectile);
                playSound("laserSound.wav");
            }
        }
    }

//    KARIS NEW ->
    public static int weightedDraw(ArrayList<?> input) {
        if (input.size() == 1) return 0;
        int output = 1;
        ArrayList<Integer> weightRanges = new ArrayList<>();
        int weightSum = 0;
        for (int i = 0; i < input.size(); i++) {
            Action action = (Action) input.get(i);
            weightSum += action.getWeight();
            weightRanges.add(weightSum);
        }

        if (weightSum < 1) weightSum = 1;
        int randomInt = random.nextInt(weightSum) + 1;

        for (int i = 0; i < weightRanges.size(); i++) {
            if (randomInt <= weightRanges.get(i)) {
                output = i;
                break;
            }
        }

        return output;
    }
//    <-

    Color darkened(Color input) {
        double scale = 1.75;
        int r = (int) (input.getRed() / scale);
        int g = (int) (input.getGreen() / scale);
        int b = (int) (input.getBlue() / scale);
        return new Color(r, g, b);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keyMap.put(keyCode, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keyMap.put(keyCode, false);
    }

    public void mousePress(MouseEvent e) {
//        for (int i = 0; i < random.nextInt(2) + 1; i++) {
        Projectile projectile = new Projectile(5, new Color(0, 150, 200), true, 12, 10);
        projectile.setPosition(player.getTruePosition());
        projectile.setSize(10);
        projectile.setVelocity(Vector2.AtoB(new Vector2(e.getX(), e.getY()), new Vector2((double) WIDTH / 2, (double) HEIGHT / 2)));
        int inAccuracy = 20;
        projectile.setVelocity(Vector2.add(projectile.getVelocity(), new Vector2(random.nextDouble(inAccuracy) - (double) inAccuracy / 2)));
//            projectile.getVelocity().normalize(random.nextDouble(1) + 7);
        projectile.getVelocity().normalize(12);
//            projectile.setVelocity(Vector2.add(projectile.getVelocity(), player.getVelocity()));
        projectile.setFriendly(true);
        projectiles.add(projectile);
//       Patrick Jin
        playSound("laserSound.wav");

//        }
    }
}


/*
 *
 *
 *
 */