package Graphics;
import com.sun.tools.javac.Main;

import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MainGraphics extends JFrame {
    public class TruckImage
    {
        private int x = 100;
        private int y = 100;
        public int getX() {return this.x;}
        public int getY() {return this.y;}
        public void setX(int x) {this.x = x;}
        public void setY(int y) {this.y = y;}
    }
    public static void main(String[] args) throws IOException{
        new MainGraphics();
    }

    public MainGraphics() throws HeadlessException, IOException
    {
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new DrawArea());
        setVisible(true);
    }

    class DrawArea extends JPanel
    {
        BufferedImage image = null;
        TruckImage truck = new TruckImage();
        public DrawArea() throws IOException
        {
            image = ImageIO.read(new File(System.getProperty("user.dir") + "/src/Graphics/truck.png"));
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    animateTruck();
                }
            };
            Timer timer = new Timer(100 ,taskPerformer);
            timer.setRepeats(true);
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawImage(image, truck.getX(), truck.getY(), 100, 100, null);
        }

        private void animateTruck()
        {
            truck.setX(truck.getX()+10);
            this.repaint();
        }
    }
}
