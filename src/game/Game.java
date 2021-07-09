package game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Objects;
import java.util.Random;

public class Game extends JFrame {
    private final JLabel[] holes = new JLabel[16];
    private final boolean[] board = new boolean[16];
    private int score = 0;
    private JLabel lblScore;
    private JLabel lblTimeLeft;
    private JLabel lblHighscore;
    private int timeLeft = 30;
    private int highscore = 0;
    private JButton btnStart;
    private Timer timer;

    public static void main(String[] args) {
        Game frame = new Game();
        frame.setVisible(true);
    }

    public Game() {
        initGUI();
        clearBoard();
        initEvents();
        loadHighscore();
    }

    private void initGUI() {
        setTitle("My first frame");
//        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 100, 608, 720);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(0, 51, 0));
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel. setLayout(null);

        JLabel lblTitle = new JLabel("Title in frame");

        lblTitle.setForeground(new Color(153, 204, 0));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 20));
        lblTitle.setBounds(0, 0, 602, 47);

        contentPanel.add(lblTitle);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 102, 0));
        panel.setBounds(32, 105, 535, 546);
        panel.setLayout(null);
        panel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                loadImage("hammer.png").getImage(),
                new Point(0, 0), "custom cursor1"));
        contentPanel.add(panel);

        for (int i = 0, x = 0, y = 396; i < 16; i++) {
            holes[i] = new JLabel(String.valueOf(i));
            holes[i].setName(String.valueOf(i));
            holes[i].setBounds(x, y, 132, 132);
            panel.add(holes[i]);
            x = (x + 132) % 528;
            y = x == 0 ? y - 132 : y;
        }

        lblScore = new JLabel("Score: 0");
        lblScore.setForeground(new Color(135, 206, 250));
        lblScore.setHorizontalAlignment(SwingConstants.TRAILING);
        lblScore.setFont(new Font("Cambria", Font.BOLD, 14));
        lblScore.setBounds(423, 54, 144, 33);
        contentPanel.add(lblScore);

        lblTimeLeft = new JLabel("30");
        lblTimeLeft.setHorizontalAlignment(SwingConstants.CENTER);
        lblTimeLeft.setForeground(new Color(240, 128, 128));
        lblTimeLeft.setFont(new Font("Cambria Math", Font.BOLD, 24));
        lblTimeLeft.setBounds(232, 54, 144, 33);
        contentPanel.add(lblTimeLeft);

        lblHighscore = new JLabel("Highscore: 0");
        lblHighscore.setHorizontalAlignment(SwingConstants.TRAILING);
        lblHighscore.setForeground(new Color(255, 255, 0));
        lblHighscore.setFont(new Font("Cambria", Font.BOLD, 14));
        lblHighscore.setBounds(433, 18, 134, 33);
        contentPanel.add(lblHighscore);

        btnStart = new JButton("Start");
        btnStart.setBackground(Color.WHITE);
        btnStart.setBounds(32, 60, 110, 33);
        contentPanel.add(btnStart);

        setContentPane(contentPanel);
    }

    private void clearBoard() {
        ImageIcon holeImage = loadImage("moleIn.png");

        for (int i = 0; i < holes.length; i++) {
            holes[i].setIcon(holeImage);
            board[i] = false;
        }
    }

    private ImageIcon loadImage(String path) {
        Image image = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(path))).getImage();
        Image scaledImage = image.getScaledInstance(132, 132, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private void genRandMole() {
        int moleID = new Random(System.currentTimeMillis()).nextInt(16);
        board[moleID] = true;
        holes[moleID].setIcon(loadImage("moleOut.png"));
    }

    private void initEvents() {
        for (JLabel hole : holes) {
            hole.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    JLabel lbl = (JLabel) e.getSource();
                    int id = Integer.parseInt(lbl.getName());
                    pressedButton(id);
                }
            });
        }

        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnStart.setEnabled(false);
                clearBoard();
                genRandMole();
                timer.start();
            }
        });

        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(timeLeft == 0){
                    lblTimeLeft.setText("" + timeLeft);
                    timer.stop();
                    gameOver();
                }
                lblTimeLeft.setText("" + timeLeft);
                timeLeft--;

                clearBoard();
                genRandMole();
            }
        });
    }

    private void pressedButton(int id) {
        if (board[id]) {
            score++;
        } else {
            score--;
        }
        lblScore.setText("Score: " + score);
        clearBoard();
        genRandMole();
    }

    private void gameOver() {
        btnStart.setEnabled(true);
        if (highscore < score) {
            highscore = score;
            lblHighscore.setText("Highscore: " + highscore);
            JOptionPane. showMessageDialog(this, "Your final score is: " + score, "You beat the high score!", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(this, "Your final score is: " + score, "Game Over!", JOptionPane.INFORMATION_MESSAGE);
        }

        score = 0;
        timeLeft = 30;
        lblScore.setText("Score: 0");
        lblTimeLeft.setText("30");

        clearBoard();
        saveHighscore();
    }

    private void saveHighscore() {
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/res/highscore.txt", false));
            bw.write("" + highscore);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error while saving highscore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHighscore() {
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/res/highscore.txt"));
            line = br.readLine();
            br.close();
            if (!line.isEmpty()) {
                highscore = Integer.parseInt(line);
                lblHighscore.setText("Highscore: " + highscore);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error while reading highscore", JOptionPane.ERROR_MESSAGE);
        }
    }
}
