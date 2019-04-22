package scripts;

import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static scripts.CommonUtils.Tools.*;

@Script.Manifest(name = "Parkour", description = "Agility script")
public class Parkour extends PollingScript<ClientContext> implements MessageListener, PaintListener {
    /* VARROCK ROOFTOPS CONSTANTS */
    private Tile VARROCK_END_LOCATION = new Tile(3236, 3417);
    private Tile VARROCK_START_LOCATION = new Tile(3222, 3414);

    private int ROUGHWALL_ID = 10586;
    private int CLOTHESLINE_ID = 10587;
    private int GAP_ONE_ID = 10642;
    private int WALL_ID = 10777;
    private int GAP_TWO_ID = 10778;
    private int GAP_THREE_ID = 10779;
    private int GAP_FOUR_ID = 10780;
    private int LEDGE_ID = 10781;
    private int EDGE_ID = 10817;

    private int[] ROUGHWALL_BOUNDS = {12, 32, -100, -52, 60, 120};
    private int[] CLOTHESLINE_BOUNDS = {-52, 60, 84, 0, -32, 32};
    private int[] GAP_ONE_BOUNDS = {-36, 92, 0, 60, -32, 32};
    private int[] WALL_BOUNDS = {-188, 84, -52, 0, -32, 32};
    private int[] GAP_TWO_BOUNDS = {-40, 100, -64, 0, -32, 32};
    private int[] GAP_THREE_BOUNDS = {-32, 32, -64, 0, -200, 32};
    private int[] GAP_FOUR_BOUNDS = {-120, 28, -8, 52, -112, 36};
    private int[] LEDGE_BOUNDS = {-36, 160, -60, 0, -52, 32};
    private int[] EDGE_BOUNDS = {-264, 156, -52, 0, -32, 32};

    //Area tile format is (SW, NE)
    private final Area ROUGHWALL_AREA = new Area(new Tile(3221, 3412), new Tile(3230, 3419));
    private final Area CLOTHESLINE_AREA = new Area(new Tile(3214, 3410, 3), new Tile(3219, 3417, 3));
    private final Area GAP_ONE_AREA = new Area(new Tile(3201, 3413, 3), new Tile(3208, 3417, 3));
    private final Area WALL_AREA = new Area(new Tile(3193, 3415, 1), new Tile(3197, 3417, 1));
    private final Area GAP_TWO_AREA = new Area(new Tile(3192, 3402, 3), new Tile(3198, 3406, 3));
    private final Area GAP_THREE_AREA = new Area(new Tile(3183, 3393, 3), new Tile(3183, 3398, 3), new Tile(3202, 3398, 3), new Tile(3202, 3403, 3), new Tile(3208, 3403, 3), new Tile(3208, 3393, 3));
    private final Area GAP_FOUR_AREA = new Area(new Tile(3218, 3393, 3), new Tile(3233, 3403, 3));
    private final Area LEDGE_AREA = new Area(new Tile(3236, 3403, 3), new Tile(3240, 3408, 3));
    private final Area EDGE_AREA = new Area(new Tile(3236, 3410, 3), new Tile(3240, 3415, 3));

    private GameObject roughWall;
    private GameObject clothesline;
    private GameObject gapOne;
    private GameObject wall;
    private GameObject gapTwo;
    private GameObject gapThree;
    private GameObject gapFour;
    private GameObject ledge;
    private GameObject edge;

    private Obstacle roughWall_O;
    private Obstacle clothesline_O;
    private Obstacle gapOne_O;
    private Obstacle wall_O;
    private Obstacle gapTwo_O;
    private Obstacle gapThree_O;
    private Obstacle gapFour_O;
    private Obstacle ledge_O;
    private Obstacle edge_O;

    /* GNOME AGILITY CONSTANTS */
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

    private ArrayList obstacles = new ArrayList();

    private String currentLocation = "";
    private String course;

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
        int agilityLevel = ctx.skills.level(Constants.SKILLS_AGILITY);
        if(agilityLevel < 30){
            course = "GNOME";
        } else {
            course = "VARROCK";
        }
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
            //Enable run
            int energyThreshold = Random.nextGaussian(0, 100, 70, 50); //Energy threshold to enable run
            if (ctx.movement.energyLevel() > energyThreshold) {
                if (!ctx.movement.running()) {
                    ctx.movement.running(true);
                    Condition.sleep(Random.nextGaussian(100, 1000, 250, 50));
                }
            }
        }
        /*Gnome agility course object setup */
        switch(course){
            case "GNOME":
                setupGnomeObstacles();
                break;
            case "VARROCK":
                setupVarrockObstacles();
                /*Run back to start if we've fallen off an obstacle, or finished the course*/
                if(ctx.players.local().tile().floor() == 0){
                    if(!ROUGHWALL_AREA.contains(ctx.players.local())){
                        ctx.movement.step(VARROCK_START_LOCATION.derive(2, 2));
                        Condition.sleep(Random.nextGaussian(500, 2000, 1000, 100));
                        Condition.wait(() -> (ctx.players.local().animation() == -1 && !ctx.players.local().inMotion()), 500, 20);
                    }
                }
                break;
        }

        /* Cycle obstacles and traverse the course */
        int i;
        for (i = 0; i < obstacles.size(); i++) {
            if (obstacles.get(i) != null) {
                Obstacle obstacle = (Obstacle) obstacles.get(i);
                if (obstacle.getGameArea().containsOrIntersects(ctx.players.local())) {
                    currentLocation = obstacle.getGameObject().name();
                    /* Pick up mark of grace if one is in our area */
                    GroundItem mark = ctx.groundItems.select().name("Mark of grace").poll();
                        if(mark != null && mark.valid()){
                        mark.interact("Take");
                        Condition.sleep(Random.nextGaussian(500, 2000, 1500, 100));
                        Condition.wait(() -> (ctx.players.local().animation() == -1), 500, 5);
                    }
                    /* HANDLE adjusting camera for rough wall so we don't click on the wrong side of the wall */
                    if(currentLocation.equals("Rough wall")) {
                        System.out.println("at rough wall");
                        if (ctx.camera.yaw() > 130 || ctx.camera.yaw() < 30) {
                            ctx.camera.turnTo(roughWall);
                        }
                    }
                    GameObject obj = obstacle.getGameObject();
                    if(obstacle.getObjectBounds() != null) {
                        obj.bounds(obstacle.getObjectBounds()); //Not sure if this is necessary?
                    }
                    if (obj.inViewport()) {
                        if (!obj.interact(obstacle.getAction())) {
                            //Something is wrong, we need to refresh our object
                            GameObject tmp = ctx.objects.select().name(obj.name()).nearest().poll();
                            tmp.interact(obstacle.getAction());
                            System.out.println("[DEBUG] interacting with tmp");
                        }
                        /* [HANDLE] comments denote object-specific actions (TODO: encapsulate in object class) */

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

    private void setupVarrockObstacles() {
        if (ROUGHWALL_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Rough wall";
            if (roughWall == null) {
                roughWall = ctx.objects.select().id(ROUGHWALL_ID).poll();
                roughWall.bounds(ROUGHWALL_BOUNDS);
                roughWall_O = new Obstacle(ROUGHWALL_ID, roughWall, ROUGHWALL_AREA, ROUGHWALL_BOUNDS, "Climb");
                obstacles.add(roughWall_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (CLOTHESLINE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Clothesline";
            if (clothesline == null) {
                clothesline = ctx.objects.select().id(CLOTHESLINE_ID).nearest().poll();
                clothesline.bounds(CLOTHESLINE_BOUNDS);
                clothesline_O = new Obstacle(CLOTHESLINE_ID, clothesline, CLOTHESLINE_AREA, CLOTHESLINE_BOUNDS, "Cross");
                obstacles.add(clothesline_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (GAP_ONE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Gap One";
            if (gapOne == null) {
                gapOne = ctx.objects.select().id(GAP_ONE_ID).nearest().poll();
                gapOne.bounds(GAP_ONE_BOUNDS);
                gapOne_O = new Obstacle(GAP_ONE_ID, gapOne, GAP_ONE_AREA, GAP_ONE_BOUNDS, "Leap");
                obstacles.add(gapOne_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (WALL_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Wall";
            if (wall == null) {
                wall = ctx.objects.select().id(WALL_ID).nearest().poll();
                wall.bounds(GAP_ONE_BOUNDS);
                wall_O = new Obstacle(WALL_ID, wall, WALL_AREA, WALL_BOUNDS, "Balance");
                obstacles.add(wall_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (GAP_TWO_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Gap Two";
            if (gapTwo == null) {
                gapTwo = ctx.objects.select().id(GAP_TWO_ID).nearest().poll();
                gapTwo.bounds(GAP_TWO_BOUNDS);
                gapTwo_O = new Obstacle(GAP_TWO_ID, gapTwo, GAP_TWO_AREA, GAP_TWO_BOUNDS, "Leap");
                obstacles.add(gapTwo_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (GAP_THREE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Gap Three";
            if (gapThree == null) {
                gapThree = ctx.objects.select().id(GAP_THREE_ID).nearest().poll();
                gapThree.bounds(GAP_THREE_BOUNDS);
                gapThree_O = new Obstacle(GAP_THREE_ID, gapThree, GAP_THREE_AREA, GAP_THREE_BOUNDS, "Leap");
                obstacles.add(gapThree_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (GAP_FOUR_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Gap Four";
            if (gapFour == null) {
                gapFour = ctx.objects.select().id(GAP_FOUR_ID).nearest().poll();
                gapFour.bounds(GAP_FOUR_BOUNDS);
                gapFour_O = new Obstacle(GAP_FOUR_ID, gapFour, GAP_FOUR_AREA, GAP_FOUR_BOUNDS, "Cross");
                obstacles.add(gapFour_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (LEDGE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Ledge";
            if (ledge == null) {
                ledge = ctx.objects.select().id(LEDGE_ID).nearest().poll();
                ledge.bounds(LEDGE_BOUNDS);
                ledge_O = new Obstacle(LEDGE_ID, ledge, LEDGE_AREA, LEDGE_BOUNDS, "Hurdle");
                obstacles.add(ledge_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (EDGE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Edge";
            if (edge == null) {
                edge = ctx.objects.select().id(EDGE_ID).nearest().poll();
                edge.bounds(EDGE_BOUNDS);
                edge_O = new Obstacle(EDGE_ID, edge, EDGE_AREA, EDGE_BOUNDS, "Jump-off");
                obstacles.add(edge_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        }
    }

    private void setupGnomeObstacles() {
        if (LOGBALANCE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Log Balance";
            if (logBalance == null) {
                logBalance = ctx.objects.select().id(LOGBALANCE_ID).poll();
                logBalance.bounds(LOGBALANCE_BOUNDS);
                logBalance_O = new Obstacle(LOGBALANCE_ID, logBalance, LOGBALANCE_AREA, LOGBALANCE_BOUNDS, "Walk-across");
                obstacles.add(logBalance_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (OBSTACLENET_ONE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Obstacle Net One";
            if (obstacleNetOne == null) {
                obstacleNetOne = ctx.objects.select().id(OBSTACLEENET_ONE_ID).nearest().poll();
                obstacleNetOne.bounds(OBSTACLENET_ONE_BOUNDS);
                obstacleNetOne_O = new Obstacle(OBSTACLEENET_ONE_ID, obstacleNetOne, OBSTACLENET_ONE_AREA, OBSTACLENET_ONE_BOUNDS, "Climb-over");
                obstacles.add(obstacleNetOne_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (TREEBRANCH_ONE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Tree Branch One";
            if (treeBranchOne == null) {
                treeBranchOne = ctx.objects.select().id(TREEBRANCH_ONE_ID).poll();
                treeBranchOne.bounds(TREEBRANCH_ONE_BOUNDS);
                treeBranchOne_O = new Obstacle(TREEBRANCH_ONE_ID, treeBranchOne, TREEBRANCH_ONE_AREA, TREEBRANCH_ONE_BOUNDS, "Climb");
                obstacles.add(treeBranchOne_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (BALANCINGROPE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Balancing Rope";
            if (balancingRope == null) {
                balancingRope = ctx.objects.select().id(BALANCINGROPE_ID).poll();
                balancingRope.bounds(BALANCINGROPE_BOUNDS);
                balancingRope_O = new Obstacle(BALANCINGROPE_ID, balancingRope, BALANCINGROPE_AREA, BALANCINGROPE_BOUNDS, "Walk-on");
                obstacles.add(balancingRope_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (TREEBRANCH_TWO_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Tree Branch Two";
            if (treeBranchTwo == null) {
                treeBranchTwo = ctx.objects.select().id(TREEBRANCH_TWO_ID).poll();
                treeBranchTwo.bounds(TREEBRANCH_TWO_BOUNDS);
                treeBranchTwo_O = new Obstacle(TREEBRANCH_TWO_ID, treeBranchTwo, TREEBRANCH_TWO_AREA, TREEBRANCH_TWO_BOUNDS, "Climb-down");
                obstacles.add(treeBranchTwo_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (OBSTACLENET_TWO_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Obstacle Net Two";
            if (obstacleNetTwo == null) {
                obstacleNetTwo = ctx.objects.select().id(OBSTACLEENET_TWO_ID).nearest().poll();
                obstacleNetTwo.bounds(OBSTACLENET_TWO_BOUNDS);
                obstacleNetTwo_O = new Obstacle(OBSTACLEENET_TWO_ID, obstacleNetTwo, OBSTACLENET_TWO_AREA, OBSTACLENET_TWO_BOUNDS, "Climb-over");
                obstacles.add(obstacleNetTwo_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        } else if (OBSTACLEPIPE_AREA.containsOrIntersects(ctx.players.local())) {
            currentLocation = "Obstacle Pipe";
            if (obstaclePipe == null) {
                obstaclePipe = ctx.objects.select().id(OBSTACLEPIPE_EAST_ID).nearest().poll();
                obstaclePipe.bounds(OBSTACLEPIPE_BOUNDS);
                obstaclePipe_O = new Obstacle(OBSTACLEPIPE_EAST_ID, obstaclePipe, OBSTACLEPIPE_AREA, OBSTACLEPIPE_BOUNDS, "Squeeze-through");
                obstacles.add(obstaclePipe_O);
                System.out.println("[DEBUG] Loaded " + currentLocation);
            }
        }
    }

}
