/*
 * Karis Jones
 * Action
 * This class creates an AI action, as shown in javaGameAIGOAP.
 * An action has a condition to be checked, a method to carry it out, and a weight that determines its probability of being picked out of a list of other actions.
 */

public abstract class Action {
    private int weight;
    public abstract boolean checkCondition();
    public abstract void doAction();
    public int getWeight() { return weight; };
    public void setWeight(int input) { weight = input; };
}
