

package model;

import util.Vector2D;
import view.Skin;

import java.util.*;

public class Packet {

    // ===== مشخصات پایه پکت ها بالاخره  =====
    private final String id = UUID.randomUUID().toString();
    private final PacketSpec spec;
    private final Skin skin;

    // ===== وضعیت لحظه‌ای =====
    private Vector2D position;
    private Vector2D velocity;
    private double   scalarSpeed;

    private int     noise;
    private boolean lost;
    private String  lostReason;

    private double timeOnWire;
    private double speedAtEntrance;

    // رفتار کلی
    private MovementStrategy movement;
    private final List<PacketComponent> components = new ArrayList<>();
    private final LossPolicy lossPolicy;


    private final Map<String, Object> tags = new HashMap<>();


    public Packet(PacketSpec spec, Skin skin, Vector2D pos, Vector2D initialVel) {
        this.spec = Objects.requireNonNull(spec, "spec");
        this.skin = Objects.requireNonNull(skin, "skin");

        this.position = (pos != null) ? pos : new Vector2D(0,0);
        this.velocity = (initialVel != null) ? initialVel : new Vector2D(0,0);
        this.scalarSpeed = (initialVel != null) ? initialVel.length() : 0.0;

        this.noise = 0;
        this.lost = false;
        this.lostReason = null;

        this.timeOnWire = 0.0;
        this.speedAtEntrance = 0.0;


        this.movement = spec.movement();
        List<PacketComponent> fromSpec = spec.components();
        if (fromSpec != null) this.components.addAll(fromSpec);
        this.lossPolicy = spec.lossPolicy();
    }


    public void update(double dt) {
        if (lost) return;
        timeOnWire += dt;

        if (movement != null) movement.update(this, dt);
        for (PacketComponent c : components) c.onUpdate(this, dt);


    }

    /** سازگاری با کد قبلیه */
    public void updateMovement(double dt) { update(dt); }

    //  نویز/برخورد/بر
    public void applyNoise(int amount) {
        if (lost || amount <= 0) return;
        noise += amount;
        if (noise >= spec.sizeUnits()) markLost("noise-overflow");
    }

    public void applyImpulse(Vector2D impulse) {
        if (lost || impulse == null) return;
        this.velocity = this.velocity.add(impulse);
        this.scalarSpeed = velocity.length();
    }

    public void collide(Packet other) {
        if (other == null || this.lost || other.lost) return;

        // نویز پایه
        this.applyNoise(1);
        other.applyNoise(1);

        // جداسازی جزئی کنم برای جلوگیری از هم‌پوشانی
        Vector2D d = this.position.subtract(other.position);
        if (d.length() == 0) d = new Vector2D(1, 0);
        Vector2D n = d.normalize();
        double off = 0.1;
        this.position = this.position.add(n.multiply(off));
        other.position = other.position.subtract(n.multiply(off));

        // دِلگِیت به کامپوننت‌ها
        for (PacketComponent c : components) c.onCollision(this, other);
    }

    public void onWireEnter(Wire wire, boolean fromCompat, boolean toCompat) {
        timeOnWire = 0.0;
        if (movement != null) movement.onWireEnter(wire, fromCompat, toCompat, this);
        for (PacketComponent c : components) c.onWireEnter(wire, this);
    }

    public void onWireExit(Wire wire) {
        if (movement != null) movement.onWireExit(wire, this);
        for (PacketComponent c : components) c.onWireExit(wire, this);
    }

    public void onEnterSystem(SystemNode node, Port in) {
        for (PacketComponent c : components) c.onEnterSystem(this, node, in);
    }

    public void onLeaveSystem(SystemNode node, Port out) {
        for (PacketComponent c : components) c.onLeaveSystem(this, node, out);
    }


    public void setTag(String key, Object value) {
        if (key == null || key.isEmpty()) return;
        if (value == null) tags.remove(key);
        else tags.put(key, value);
    }

    public Object getTag(String key) { return (key == null) ? null : tags.get(key); }

    public <T> T getTag(String key, Class<T> type) {
        Object v = getTag(key);
        return (type != null && type.isInstance(v)) ? type.cast(v) : null;
    }
    public boolean hasTag(String key) { return key != null && tags.containsKey(key); }
    public void removeTag(String key) { if (key != null) tags.remove(key); }


    public String getId() { return id; }
    public PacketSpec getSpec() { return spec; }
    public Skin getSkin() { return skin; }

    public ShapeType getShape() { return spec.shape(); }
    public int getSize() { return spec.sizeUnits(); }
    public int getCoins() { return spec.coinsOnEnter(); }

    public boolean isLost() { return lost; }
    public void markLost() { this.lost = true; }
    public void markLost(String reason) { this.lost = true; this.lostReason = reason; }
    public String getLostReason() { return lostReason; }

    public int getNoise() { return noise; }

    public int getOriginalSize() { return spec.sizeUnits(); }
    public int getCurrentNoise() { return noise; }

    public Vector2D getPosition() { return position; }
    public void setPosition(Vector2D p) { this.position = (p != null) ? p : this.position; }

    public Vector2D getVelocity() { return velocity; }
    public void setVelocity(Vector2D v) {
        this.velocity = (v != null) ? v : new Vector2D(0,0);
        this.scalarSpeed = this.velocity.length();
    }

    public double getScalarSpeed() { return scalarSpeed; }
    public void setScalarSpeed(double v) { this.scalarSpeed = v; }

    public double getTimeOnWire() { return timeOnWire; }
    public void setTimeOnWire(double t) { this.timeOnWire = t; }
    public void addTimeOnWire(double dt) { this.timeOnWire += dt; }

    public double getSpeedAtEntrance() { return speedAtEntrance; }

    public void setSpeedAtEntrance(double v) { this.speedAtEntrance = v; }

    public MovementStrategy getMovement() { return movement; }
    public void setMovement(MovementStrategy m) { this.movement = m; }     // برای Factory/Behaviorهااتخر بزن

    public List<PacketComponent> getComponents() { return Collections.unmodifiableList(components); }
    public void addComponent(PacketComponent c) { if (c != null) components.add(c); }

    public LossPolicy getLossPolicy() { return lossPolicy; }

    @Override
    public String toString() {
        return "[Packet " + id.substring(0, 5) +
                " shape=" + spec.shape() +
                " size=" + spec.sizeUnits() +
                " pos=" + position +
                " v=" + velocity +
                " speed=" + scalarSpeed +
                " noise=" + noise +
                " lost=" + lost +
                " tWire=" + timeOnWire +
                " vIn=" + speedAtEntrance + "]";
    }
}

