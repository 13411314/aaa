package Final;
 
import javax.swing.*;
 
public class MFrame {
    public static void main(String[] args) {
    	
        JFrame jf = new JFrame("五子棋小游戏");
        
        jf.add(new TablePanel());
        jf.pack();  //自动适配大小
        jf.setLocationRelativeTo(null);     //居中
        jf.setResizable(false); //不可调整大小
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //按×关闭
        jf.setVisible(true);    //是否可见    }
}
}