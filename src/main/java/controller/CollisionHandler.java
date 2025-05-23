package controller;
import model.Packet;
import util.Vector2D;
import util.Constants;
import java.util.List;

public class CollisionHandler {

    public void handleCollision(Packet packet1, Packet packet2, List<Packet> allPackets) {
        packet1.applyNoise(1);
        packet2.applyNoise(1);

        Vector2D collisionVector = packet1.getPosition().subtract(packet2.getPosition()).normalize();
        double offset = Constants.COLISION1_OFFSET;


        packet1.setPosition(packet1.getPosition().add(collisionVector.multiply(offset)));
        packet1.setPosition(packet2.getPosition().subtract(collisionVector.multiply(offset)));


        for (Packet other : allPackets) {
            if (other == packet1 || other == packet2 || other.isLost()) continue;
            double distance = other.getPosition().distanceTo(midpoint(packet1.getPosition(), packet2.getPosition()));

            if (distance <= Constants.IMPACT_RADIUS) {
                double impactFactor = (Constants.IMPACT_RADIUS - distance) / Constants.IMPACT_RADIUS;
                int noiseAmount = (int) Math.ceil(impactFactor * Constants.IMPACT_WAVE_MAX_NOISE); // بجای MAX_IMPACT_NOISE
                if (noiseAmount > 0) {
                    other.applyNoise(noiseAmount);

                }
            }

        }
    }
public static Vector2D midpoint(Vector2D pos1,Vector2D pos2){
            return new Vector2D((pos1.getX()+pos2.getX())/2,(pos1.getX()+pos2.getY())/2);

        }


}
/////////