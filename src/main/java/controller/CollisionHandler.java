package controller;
import model.Packet;
import util.Vector2D;
import util.Constants;
import java.util.List;

public class CollisionHandler {

    public void handleCollision(Packet p1, Packet p2, List<Packet> allPackets) {
        p1.applyNoise(1);
        p2.applyNoise(1);

        Vector2D collisionVector = p1.getPosition().subtract(p2.getPosition()).normalize();
        if (collisionVector.length() == 0) collisionVector = new Vector2D(1, 0);
        // جلوگیری از NaN
        double offset = Constants.COLISION1_OFFSET;


        p1.setPosition(p1.getPosition().add(collisionVector.multiply(offset)));
        p2.setPosition(p2.getPosition().subtract(collisionVector.multiply(offset)));


        for (Packet other : allPackets) {
            if (other == p1 || other == p2 || other.isLost()) continue;
            double distance = other.getPosition().distanceTo(midpoint(p1.getPosition(), p2.getPosition()));

            if (distance <= Constants.IMPACT_RADIUS) {
                double impactFactor = (Constants.IMPACT_RADIUS - distance) / Constants.IMPACT_RADIUS;
                int noiseAmount = (int) Math.ceil(impactFactor * Constants.IMPACT_WAVE_MAX_NOISE); // بجای MAX_IMPACT_NOISE
                if (noiseAmount > 0) {
                    other.applyNoise(noiseAmount);

                }
            }

        }
    }
    public static Vector2D midpoint(Vector2D a, Vector2D b){
        return new Vector2D((a.getX()+b.getX())/2.0, (a.getY()+b.getY())/2.0);
    }



}
/////////