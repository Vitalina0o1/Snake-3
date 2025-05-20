import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Random;

/**
 * Класс игрового поля для игры "Змейка"
 */
public class GameField extends JPanel implements ActionListener {

    private final int SIZE = 320;        // Размер поля
    private final int DOT_SIZE = 16;     // Размер одной части змейки и яблока
    private final int ALL_DOTS = 400;    // Максимальное число частей змейки

    private Image dot;
    private Image apple;

    private int appleX;
    private int appleY;

    private int[] x = new int[ALL_DOTS];
    private int[] y = new int[ALL_DOTS];

    private int dots;
    private Timer timer;

    // Направление движения змейки
    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;

    private boolean inGame = true; // Статус игры

    public GameField() {
        setBackground(Color.BLACK);
        loadImages();
        initGame();
        addKeyListener(new FieldKeyListener());
        setFocusable(true);
    }

    /**
     * Инициализация игры
     */
    public void initGame() {
        dots = 3;
        for (int i = 0; i < dots; i++) {
            x[i] = 48 - i * DOT_SIZE;
            y[i] = 48;
        }
        timer = new Timer(250, this);
        timer.start();
        createApple();
    }

    /**
     * Создание яблока в случайной позиции
     */
    public void createApple() {
        Random rand = new Random();
        appleX = rand.nextInt(19) * DOT_SIZE;
        appleY = rand.nextInt(19) * DOT_SIZE;
    }

    /**
     * Загрузка изображений для змейки и яблока
     */
    public void loadImages() {
        ImageIcon iia = new ImageIcon("apple.png");
        apple = iia.getImage();
        ImageIcon iid = new ImageIcon("dot.png");
        dot = iid.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (inGame) {
            g.drawImage(apple, appleX, appleY, this);
            for (int i = 0; i < dots; i++) {
                g.drawImage(dot, x[i], y[i], this);
            }
        } else {
            showGameOver(g);
        }
    }

    /**
     * Отображение сообщения о завершении игры
     */
    private void showGameOver(Graphics g) {
        String str = "Game Over";
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(str, 100, SIZE / 2);
    }

    /**
     * Передвижение змейки
     */
    public void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (left) {
            x[0] -= DOT_SIZE;
        }
        if (right) {
            x[0] += DOT_SIZE;
        }
        if (up) {
            y[0] -= DOT_SIZE;
        }
        if (down) {
            y[0] += DOT_SIZE;
        }
    }

    /**
     * Проверка попадания змейки на яблоко
     */
    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            dots++;
            createApple();
            try {
                playSound("May/cat.wav");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Проверка столкновений змейки с границами и собой
     */
    public void checkCollisions() {
        // Столкновение с телом
        for (int i = 1; i < dots; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
                inGame = false;
            }
        }

        // Выход за границы поля
        if (x[0] >= SIZE || x[0] < 0 || y[0] >= SIZE || y[0] < 0) {
            inGame = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollisions();
            move();
        }
        repaint();
    }

    /**
     * Обработка нажатий клавиш для управления
     */
    class FieldKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT && !right) {
                left = true;
                up = false;
                down = false;
            } else if (key == KeyEvent.VK_RIGHT && !left) {
                right = true;
                up = false;
                down = false;
            } else if (key == KeyEvent.VK_UP && !down) {
                up = true;
                right = false;
                left = false;
            } else if (key == KeyEvent.VK_DOWN && !up) {
                down = true;
                right = false;
                left = false;
            }
        }
    }

    /**
     * Воспроизведение звука
     */
    private void playSound(String soundFileName) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(soundFileName)));
            clip.start();
        } catch (Exception e) {
            System.err.println("Ошибка воспроизведения: " + soundFileName);
        }
    }
}