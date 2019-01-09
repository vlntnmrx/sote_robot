package hsurobots.components;

public class Steuerung extends Stearing {

    @Override
    public void move(double deltaTimeSec) {
        posX = posX + (Math.cos(orientation) * velocity * deltaTimeSec);
        posY = posY + (Math.sin(orientation) * velocity * deltaTimeSec);
    }

    @Override
    public void accelerate(int amount) {
        this.velocity += amount;
    }

    @Override
    public void decelerate(int amount) {
        accelerate(-amount);
    }

    @Override
    public void setInitialPose(int posX, int posY, double orientation) {
        this.posX = posX;
        this.posY = posY;
        this.orientation = orientation;
    }

    @Override
    public void rotate(double rotate) {
        this.orientation += rotate;
        if (this.orientation > 2. * Math.PI) {
            this.orientation = (this.orientation - 2. * Math.PI);
        }
        if (this.orientation < 0) {
            this.orientation += Math.PI * 2.;
        }
    }
}
