package scripts;

import org.powerbot.script.Client;
import org.powerbot.script.Condition;
import org.powerbot.script.Locatable;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Game;

import java.awt.*;
import java.util.concurrent.Callable;

class CommonUtils {
    static class Tools {

        static void step(ClientContext ctx, Locatable loc) {
            final Callable<Boolean> notMoving = () -> (ctx.players.local().animation() == -1 && !ctx.players.local().inMotion());
            if (loc.tile().matrix(ctx).inViewport()) {
                loc.tile().matrix(ctx).interact("Walk here");
            } else {
                ctx.movement.step(loc);
            }
            Condition.wait(notMoving, 250, 40); //Wait some more if we are still moving for some reason
            Condition.sleep(100); //Double check with 100ms buffer prevents momentary animation==-1 in obstacle interaction from breaking wait
            Condition.wait(notMoving, 250, 40); //Wait some more if we are still moving for some reason
        }

        static long perHour(ClientContext ctx, long startValue, long currentValue, long startTime) {
            long valueGained = currentValue - startValue;
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            double hoursElapsed = (float) elapsed / 3600;
            return Math.round((1 / hoursElapsed) * valueGained);
        }

        static String formatTime(long _elapsed) { //Format time to an hours, minutes, seconds string
            long elapsed = _elapsed / 1000;
            String elapsedSeconds = Long.toString(elapsed % 60);
            String elapsedMinutes = Long.toString((elapsed / 60) % 60);
            String elapsedHours = Long.toString((elapsed / 3600) % 24);
            return elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds;
        }

        static void doAntiban(ClientContext ctx) {
            int decision = Random.nextGaussian(0, 7, 1, 25);
            decision = Math.round(decision);
            switch (decision) {
                case 0:
                    moveMouseRandom(ctx);
                    break;
                case 1:
                    moveMouseOffscreenRandom(ctx);
                    break;
                case 2:
                    ctx.camera.turnTo(ctx.objects.select().nearest().poll());
                    break;
                case 3:
                    System.out.println("Changing camera angle");
                    ctx.camera.angle(Random.nextInt(0, 300));
                    break;
                case 4:
                    System.out.println("Checking stats");
                    ctx.game.tab(Game.Tab.STATS);
                    break;
                case 6:
                    afkBreak(ctx);
                    break;
            }

        }

        static void moveMouseRandom(ClientContext ctx) {
            int x = Random.nextInt(0, ctx.game.dimensions().width - 1);
            int y = Random.nextInt(0, ctx.game.dimensions().height - 1);
            ctx.input.move(new Point(x, y));
        }

        static void moveMouseOffscreenRandom(ClientContext ctx) {
            System.out.println("moving mouse off screen random");
            int direction = Random.nextGaussian(0, 4, 1, 50);
            int x, y;
            x = y = 0;
            switch (direction) {
                case 0:
                    //move up
                    System.out.println("Moving up");
                    x = Random.nextInt(0, ctx.game.dimensions().width - 1);
                    y = -10;
                    break;
                case 1:
                    //move left
                    System.out.println("Moving left");
                    x = -10;
                    y = Random.nextInt(0, ctx.game.dimensions().height - 1);
                    break;

                case 2:
                    //move right
                    System.out.println("Moving right");
                    x = ctx.game.dimensions().width + 10;
                    y = Random.nextInt(0, ctx.game.dimensions().height - 1);
                    break;
                case 3:
                    //move down
                    System.out.println("Moving down");
                    x = Random.nextInt(0, ctx.game.dimensions().width - 1);
                    y = Random.nextInt(0, ctx.game.dimensions().height + 10);
                    break;

            }
            ctx.input.move(new Point(x, y));

        }

        static void afkBreak(ClientContext ctx) { //Take a break to simulate going AFK or getting distracted
            System.out.println("Going afk for a bit");
            if (Random.nextInt(0, 5) < 4) { //Usually move mouse off screen before going afk
                moveMouseOffscreenRandom(ctx);
            }
            Condition.sleep(Random.nextGaussian(20000, 180000, 45000, 100)); //Simulate a break/distraction (TODO: check for anti logout timer while breaking or have auto relog if possible)
        }
    }
}
