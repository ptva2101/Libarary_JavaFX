package com.example.librarymanagementsystem2;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.Random;

public class SnowEffect {
    private final AnchorPane snowContainer;
    private final ArrayList<Snowflake> snowflakes;
    private final Random random;
    private final AnimationTimer timer;
    private boolean isRunning = false;

    private static class Snowflake {
        Circle circle;
        double speed;
        double wind;

        Snowflake(Circle circle, double speed, double wind) {
            this.circle = circle;
            this.speed = speed;
            this.wind = wind;
        }
    }

    public SnowEffect(AnchorPane anchorPane, int numberOfSnowflakes) {
        this.snowContainer = anchorPane;
        this.snowflakes = new ArrayList<>();
        this.random = new Random();

        for (int i = 0; i < numberOfSnowflakes; i++) {
            snowflakes.add(createSnowflake());
        }

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateSnowflakes();
            }
        };
    }

    private Snowflake createSnowflake() {
        double size = random.nextDouble() * 4 + 2; // Kích thước 2-6
        Circle circle = new Circle(size, Color.WHITE);

        // Đặt vị trí ban đầu
        circle.setTranslateX(random.nextDouble() * snowContainer.getWidth());
        circle.setTranslateY(random.nextDouble() * snowContainer.getHeight());

        // Thiết lập tốc độ và hướng gió
        double speed = random.nextDouble() * 2 + 1; // Tốc độ 1-3
        double wind = random.nextDouble() * 2 - 1; // Gió -1 đến 1

        return new Snowflake(circle, speed, wind);
    }

    private void updateSnowflakes() {
        double width = snowContainer.getWidth();
        double height = snowContainer.getHeight();

        snowflakes.forEach(snowflake -> {
            // Cập nhật vị trí
            snowflake.circle.setTranslateY(snowflake.circle.getTranslateY() + snowflake.speed);
            snowflake.circle.setTranslateX(snowflake.circle.getTranslateX() + snowflake.wind);

            // Xử lý khi bông tuyết rơi xuống đáy
            if (snowflake.circle.getTranslateY() > height) {
                snowflake.circle.setTranslateY(-5);
                snowflake.circle.setTranslateX(random.nextDouble() * width);
            }

            // Xử lý khi bông tuyết bay ra ngoài hai bên
            if (snowflake.circle.getTranslateX() < 0) {
                snowflake.circle.setTranslateX(width);
            } else if (snowflake.circle.getTranslateX() > width) {
                snowflake.circle.setTranslateX(0);
            }
        });
    }

    // Phương thức bắt đầu hiệu ứng
    public void startSnowfall() {
        if (!isRunning) {
            // Thêm tất cả bông tuyết vào snowContainer
            snowflakes.forEach(snowflake -> {
                if (!snowContainer.getChildren().contains(snowflake.circle)) {
                    snowContainer.getChildren().add(snowflake.circle);
                }
            });
            timer.start();
            isRunning = true;
        }
    }

    // Phương thức dừng hiệu ứng
    public void stopSnowfall() {
        if (isRunning) {
            timer.stop();
            isRunning = false;
        }
    }

    // Phương thức xóa hiệu ứng
    public void clearSnowfall() {
        stopSnowfall();
        snowContainer.getChildren().removeAll(
                snowflakes.stream()
                        .map(snowflake -> snowflake.circle)
                        .toList()
        );
    }

    // Phương thức điều chỉnh số lượng bông tuyết
    public void setNumberOfSnowflakes(int number) {
        boolean wasRunning = isRunning;
        if (wasRunning) {
            stopSnowfall();
        }

        clearSnowfall();
        snowflakes.clear();

        for (int i = 0; i < number; i++) {
            snowflakes.add(createSnowflake());
        }

        if (wasRunning) {
            startSnowfall();
        }
    }
}