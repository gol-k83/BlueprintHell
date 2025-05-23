package model;

import util.Vector2D;
import model.ShapeType;
import java.awt.*;

public class Packet {

//    public enum ShapeType {
//        SQUARE(2, Color.GREEN),
//        TRIANGLE(3,Color.YELLOW);
//
//        private final int size;
//        private final Color;
//
//        ShapeType(int size,Color color){
//            this.size = size;
//            this.color = color;
//        }
//        public int getSize() {
//            return size;
//        }
//
//        public Color getColor() {
//            return color;
//        }
//    }

    private final String id;               // شناسه منحصر به فرد پکت
    private final ShapeType shape;         // نوع شکل (مربع یا مثلث)
    private final int originalSize;        // اندازه اولیه (2 یا 3)
    private int currentNoise;              // مقدار نویز فعلی
    private Vector2D position;             // موقعیت فعلی
    private Vector2D velocity;             // بردار حرکت فعلی
    private boolean lost;                  // وضعیت سالم یا از دست رفته

    // سرعت‌ها برای مکانیک حرکت شتاب‌دار
    private double currentSpeed;
    private double targetSpeed;

    public Packet(String id, ShapeType shape, Vector2D startPosition, Vector2D initialVelocity) {
        this.id = id;
        this.shape = shape;
      //  this.originalSize = shape == ShapeType.SQUARE ? 2 : 3;
        this.originalSize = shape.getSize();
        this.currentNoise = 0;
        this.position = startPosition;
        this.velocity = initialVelocity;
        this.lost = false;
        this.currentSpeed = initialVelocity.length();
        this.targetSpeed = currentSpeed;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public ShapeType getShape() {
        return shape;
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public int getCurrentNoise() {
        return currentNoise;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D getVelocity() {
        return velocity;
    }
///////////////////////////////////

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
        this.targetSpeed = velocity.length();
    }

    public boolean isLost() {
        return lost;
    }

    public void applyNoise(int noise) {
        this.currentNoise += noise;
        if (this.currentNoise >= originalSize) {
            this.lost = true;
        }
    }

    public void updateMovement(double deltaTime) {
        // محاسبه حرکت شتاب‌دار
        currentSpeed += (targetSpeed - currentSpeed) * 0.1;

        // نرمال‌سازی بردار سرعت
        Vector2D normalizedVelocity = velocity.normalize();
        velocity = normalizedVelocity.multiply(currentSpeed);

        // آپدیت کردن موقعیت
        Vector2D deltaMove = velocity.multiply(deltaTime);
        position = position.add(deltaMove);
    }


    // برخورد با یک پکت دیگر
    public void collide(Packet otherPacket) {
        applyNoise(1);
        otherPacket.applyNoise(1);

        // منحرف شدن نقطه ثقل در اثر برخورد
        Vector2D collisionVector = this.position.subtract(otherPacket.position).normalize();
        double offset = 0.1; // مقدار درصدی انحراف

        this.position = this.position.add(collisionVector.multiply(offset));
        otherPacket.position = otherPacket.position.subtract(collisionVector.multiply(offset));
    }

    @Override
    public String toString() {
        return "Packet{" +
                "id='" + id + '\'' +
                ", shape=" + shape +
                ", originalSize=" + originalSize +
                ", currentNoise=" + currentNoise +
                ", position=" + position +
                ", velocity=" + velocity +
                ", lost=" + lost +
                '}';
    }
}


























