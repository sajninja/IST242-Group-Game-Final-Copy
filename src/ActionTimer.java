/*
 * Karis Jones
 * ActionTimer
 * This class creates a timer for enemy AI.
 * Action timers make an enemy act some time within a customizable range of ticks.
 */

public class ActionTimer {
    private int[] timeRange;
    private int timer;

    public ActionTimer(int[] range) {
        setTimeRange(range);
    }

    public void setTimeRange(int[] input) {
        timeRange = new int[] {input[0], input[1]};
        reset();
    }

    public void reset() {
        if (timeRange[0] == timeRange[1]) {
            timer = timeRange[0];
        } else {
            timer = Game.random.nextInt(getMax() - getMin()) + getMin();
        }
    }

    public int getTime() {
        return timer;
    }

    public void setTime(int input) {
        timer = input;
    }

    public int getMin() {
        return timeRange[0];
    }

    public int getMax() {
        return timeRange[1];
    }
}
