package model;

/** هر برخورد → افزایش نویز به مقدار ثابت. */
public class NoiseOnImpactComponent implements PacketComponent {
    private final int noisePerHit;

    public NoiseOnImpactComponent(int noisePerHit){ this.noisePerHit = noisePerHit; }

    @Override
    public void onCollision(Packet self, Packet other) {
        self.applyNoise(noisePerHit);
    }
}
