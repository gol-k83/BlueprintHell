package model;

import util.Vector2D;

/** در برخورد، جهت حرکت پکت را برعکس می‌کند (برای پکت ۱واحدی). *///یادم باشه
public class BounceBackOnCollisionComponent implements PacketComponent {
    @Override
    public void onCollision(Packet self, Packet other) {
        Vector2D v = self.getVelocity();
        self.setVelocity(new Vector2D(-v.getX(), -v.getY()));
    }
}
