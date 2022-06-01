package ru.housekeeper;

public class Data {
    private int angle;
    private int strength;

    public Data(int angle, int strength) {
        this.angle = angle;
        this.strength = strength;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
