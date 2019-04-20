package scripts;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.GameObject;

import java.awt.*;
import java.util.HashMap;

import static scripts.CommonUtils.Tools.*;

@Script.Manifest(name = "Parkour", description = "Agility script")
public class Parkour extends PollingScript<ClientContext> implements MessageListener, PaintListener {
    private int LOGBALANCE_ID = 23145;
    private int OBSTACLEENET_ONE_ID = 23134;
    private int TREEBRANCH_ONE_ID = 23559;
    private int BALANCINGROPE_ID = 23557;
    private int TREEBRANCH_TWO_ID = 23560;
    private int OBSTACLEENET_TWO_ID = 23135;
    private int OBSTACLEPIPE_WEST_ID = 23138;
    private int OBSTACLEPIPE_EAST_ID = 23139;

    private final int[] LOGBALANCE_BOUNDS = {76, 56, 48, 16, 0, 112};
    private final int[] OBSTACLENET_ONE_BOUNDS = {-40, 32, -196, -80, -56, 32};
    private final int[] TREEBRANCH_ONE_BOUNDS = {-12, 12, -104, -232, -32, 32};
    private final int[] BALANCINGROPE_BOUNDS = {0, 120, 8, 0, 52, 80};
    private final int[] TREEBRANCH_TWO_BOUNDS = {-76, -4, -148, -68, -32, 32};
    private final int[] OBSTACLENET_TWO_BOUNDS = {-68, 104, -180, 0, -32, 32};
    private final int[] OBSTACLEPIPE_BOUNDS = {-44, 48, -172, 0, -68, 32};

    //Area tile format is (SW, NE)
    private final Area LOGBALANCE_AREA = new Area(new Tile(2468, 3436), new Tile(2488, 3439));
    private final Area OBSTACLENET_ONE_AREA = new Area(new Tile(2471, 3426), new Tile(2477, 3430));
    private final Area TREEBRANCH_ONE_AREA = new Area(new Tile(2471, 3422, 1), new Tile(2476, 3424, 1));
    private final Area BALANCINGROPE_AREA = new Area(new Tile(2472, 3418, 2), new Tile(2479, 3422, 2));
    private final Area TREEBRANCH_TWO_AREA = new Area(new Tile(2483, 3418, 2), new Tile(2488, 3421, 2));
    private final Area OBSTACLENET_TWO_AREA = new Area(new Tile(2483, 3420, 0), new Tile(2488, 3425, 0));
    private final Area OBSTACLEPIPE_AREA = new Area(new Tile(2479, 3427, 0), new Tile(2479, 3434, 0), new Tile(2482, 3434), new Tile(2482, 3430),
            new Tile(2489, 3430), new Tile(2489, 3427));

    private GameObject logBalance;
    private GameObject obstacleNetOne;
    private GameObject treeBranchOne;
    private GameObject balancingRope;
    private GameObject treeBranchTwo;
    private GameObject obstacleNetTwo;
    private GameObject obstaclePipe;

    private Obstacle logBalance_O;
    private Obstacle obstacleNetOne_O;
    private Obstacle treeBranchOne_O;
    private Obstacle balancingRope_O;
    private Obstacle treeBranchTwo_O;
    private Obstacle obstacleNetTwo_O;
    private Obstacle obstaclePipe_O;

    private Obstacle[] obstacles = new Obstacle[8];

    private String currentLocation = "";

    private long start = System.currentTimeMillis();
    private long abStart;
    private long abWait;
    private long abElapsed = 0;
    private long startXp = 0;

    @Override
    public void start() {
        start = abStart = System.currentTimeMillis(); //Set all our timers to current time
        startXp = ctx.skills.experience(Constants.SKILLS_AGILITY);
        abWait = Random.nextGaussian(5, 280, 60, 100);
    }

    @Override
    public void messaged(MessageEvent messageEvent) {

    }


    @Override
    public void repaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        int nextLvl = ctx.skills.level(Constants.SKILLS_AGILITY) + 1;
        int nextLvlXpAt = ctx.skills.experienceAt(nextLvl);
        int currentXp = ctx.skills.experience(Constants.SKILLS_AGILITY);
        int currentXpAt = ctx.skills.experienceAt(nextLvl - 1);
        int xpGainedThisLevel = currentXp - currentXpAt;
        int xpDifferenceNextLevel = nextLvlXpAt - currentXpAt;
        int xpPercentToLvl = ((xpGainedThisLevel * 100) / (xpDifferenceNextLevel));
        Rectangle backBar = new Rectangle(50, 235, 150, 20);
        Rectangle progressBar = new Rectangle(50, 235, (int) (xpPercentToLvl * 1.5), 20);
        long elapsed = System.currentTimeMillis() - start;
        abElapsed = (System.currentTimeMillis() - abStart) / 1000;
        long xpGained = ctx.skills.experience(Constants.SKILLS_AGILITY) - startXp;
        String abWaitString = Long.toString(abWait);
        int remainingXp = nextLvlXpAt - currentXp;
        g.drawString("Agility 0.11 by Lyocell", 50, 75);
        g.drawString("Current locatiion: " + currentLocation, 50, 100);
        g.drawString("Time Running: " + formatTime(elapsed) + " seconds", 50, 125);
        g.drawString("Next antiban: " + abElapsed + "/" + abWaitString, 50, 150);
        g.drawString("XP/Hour: " + xpHr(ctx, startXp, start), 50, 175);
        g.drawString("XP Gained: " + xpGained, 50, 200);
        g.drawString("XP To Level " + nextLvl + ": " + remainingXp, 50, 225);

        g.setColor(new Color(0, 16, 4));
        g.fillRect(backBar.x, backBar.y, backBar.width, backBar.height);
        g.setColor(Color.red);
        g.fillRect(progressBar.x, progressBar.y, progressBar.width, progressBar.height);
        g.setColor(Color.white);
        g.drawString(xpPercentToLvl + "%", 125, 247);
    }

    @Override
    public void poll() {
        /* Handle Antiban */
        abElapsed = (System.currentTimeMillis() - abStart) / 1000;
        if (abElapsed > abWait) {
            doAntiban(ctx);
            abStart = System.currentTimeMillis();
            abWait = Random.nextGaussian(5, 280, 60, 100);
        }
        if (LOGBALANCE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Log Balance";
            if (logBalance == null) {
                logBalance = ctx.objects.select().id(LOGBALANCE_ID).poll();
                logBalance.bounds(LOGBALANCE_BOUNDS);
                logBalance_O = new Obstacle(LOGBALANCE_ID, logBalance, LOGBALANCE_AREA, LOGBALANCE_BOUNDS, "Walk-across");
                obstacles[0] = logBalance_O;
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (OBSTACLENET_ONE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Obstacle Net One";
            if (obstacleNetOne == null) {
                obstacleNetOne = ctx.objects.select().id(OBSTACLEENET_ONE_ID).nearest().poll();
                obstacleNetOne.bounds(OBSTACLENET_ONE_BOUNDS);
                obstacleNetOne_O = new Obstacle(OBSTACLEENET_ONE_ID, obstacleNetOne, OBSTACLENET_ONE_AREA, OBSTACLENET_ONE_BOUNDS, "Climb-over");
                obstacles[1] = obstacleNetOne_O;
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (TREEBRANCH_ONE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Tree Branch One";
            if (treeBranchOne == null) {
                treeBranchOne = ctx.objects.select().id(TREEBRANCH_ONE_ID).poll();
                treeBranchOne.bounds(TREEBRANCH_ONE_BOUNDS);
                treeBranchOne_O = new Obstacle(TREEBRANCH_ONE_ID, treeBranchOne, TREEBRANCH_ONE_AREA, TREEBRANCH_ONE_BOUNDS, "Climb");
                obstacles[2] = treeBranchOne_O;
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (BALANCINGROPE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Balancing Rope";
            if (balancingRope == null) {
                balancingRope = ctx.objects.select().id(BALANCINGROPE_ID).poll();
                balancingRope.bounds(BALANCINGROPE_BOUNDS);
                balancingRope_O = new Obstacle(BALANCINGROPE_ID, balancingRope, BALANCINGROPE_AREA, BALANCINGROPE_BOUNDS, "Walk-on");
                obstacles[3] = balancingRope_O;
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (TREEBRANCH_TWO_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Tree Branch Two";
            if (treeBranchTwo == null) {
                treeBranchTwo = ctx.objects.select().id(TREEBRANCH_TWO_ID).poll();
                treeBranchTwo.bounds(TREEBRANCH_TWO_BOUNDS);
                treeBranchTwo_O = new Obstacle(TREEBRANCH_TWO_ID, treeBranchTwo, TREEBRANCH_TWO_AREA, TREEBRANCH_TWO_BOUNDS, "Climb-down");
                obstacles[4] = treeBranchTwo_O;
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (OBSTACLENET_TWO_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Obstacle Net Two";
            if (obstacleNetTwo == null) {
                obstacleNetTwo = ctx.objects.select().id(OBSTACLEENET_TWO_ID).nearest().poll();
                obstacleNetTwo.bounds(OBSTACLENET_TWO_BOUNDS);
                obstacleNetTwo_O = new Obstacle(OBSTACLEENET_TWO_ID, obstacleNetTwo, OBSTACLENET_TWO_AREA, OBSTACLENET_TWO_BOUNDS, "Climb-over");
                obstacles[5] = obstacleNetTwo_O;
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (OBSTACLEPIPE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Obstacle Pipe";
            if (obstaclePipe == null) {
                obstaclePipe = ctx.objects.select().id(OBSTACLEPIPE_EAST_ID).nearest().poll();
                obstaclePipe.bounds(OBSTACLEPIPE_BOUNDS);
                obstaclePipe_O = new Obstacle(OBSTACLEPIPE_EAST_ID, obstaclePipe, OBSTACLEPIPE_AREA, OBSTACLEPIPE_BOUNDS, "Squeeze-through");
                obstacles[6] = obstaclePipe_O;
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        }
        //Cycle objects
        int i;
        for (i = 0; i < obstacles.length; i++) {
            if (obstacles[i] != null) {
                Obstacle obstacle = obstacles[i];
                if (obstacle.getGameArea().containsOrIntersects(ctx.players.local())) {
                    currentLocation = obstacle.getGameObject().name();
                    GameObject obj = obstacle.getGameObject();
                    obj.bounds(obstacle.getObjectBounds()); //Not sure if this is necessary?
                    System.out.println("At obstacle: " + obj.name());
                    if (obj.inViewport()) {
                        System.out.println("Obstacle in viewport: " + obj.name());

                        if (obj.click(obstacle.getAction())) {
                            System.out.println("Clicked");
                        } else if (obj.interact(obstacle.getAction())) {
                            System.out.println("Interacted");
                        } else { //Looks like we need to refresh our object
                            GameObject tmp = ctx.objects.select().name(obj.name()).nearest().poll();
                            tmp.interact(obstacle.getAction());
                            System.out.println("[DEBUG] interacting with tmp");
                        }
                        /* HANDLE object-specific actions (TODO: encapsulate in object class) */
                        /* HANDLE pipe */

                        if ((ctx.players.local().tile().x() == 2487) || ctx.players.local().tile().x() == 2484) {
                            while ((3430 < ctx.players.local().tile().y()) && (ctx.players.local().tile().y() < 3437)) {
                                Condition.sleep(Random.nextInt(500, 1500));
                            }
                        }
                        Condition.wait(() -> (ctx.players.local().animation() == -1), 500, 5);
                    } else {
                        System.out.println("Obstacle not in viewport: " + obj.name());
                        /* HANDLE obstacle net */
                        if (obj.name().equals("Obstacle net")) {
                            ctx.camera.turnTo(obj);
                        } else {
                            ctx.movement.step(obj.tile().derive(1, 1));
                            Condition.wait(() -> (ctx.players.local().animation() == -1), 500, 5);
                        }
                    }
                    Condition.sleep(Random.nextGaussian(500, 6000, 1000, 100));
                    Condition.wait(() -> (ctx.players.local().animation() == -1 && !ctx.players.local().inMotion()), 500, 5); //Wait some more if we are still moving for some reason

                }

            }
        }
    }

}
