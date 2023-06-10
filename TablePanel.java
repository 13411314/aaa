package Final;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Vector;
 
public class TablePanel extends JPanel {
 
    //Panel的大小
    final int TABLE_WIDTH = 700;
    final int TABLE_HEIGHT = 580;
 
    final int NUM = 15;             //棋盘线的条数
 
    final int OFFSET_X = 30;  //棋盘左上角相对于panel左上角的偏移量(棋盘的起始位置)
    final int OFFSET_Y = 80;
 
    final int SP = 33; //棋盘每条线的间隔
 
    final int RECT_SIZE = 6;    //棋盘上五个提示点的位置
 
    final int OVAL_SIZE = 32;   //棋子的大小
 
    int[][] table = new int[NUM][NUM];  //二维数字记录棋盘上每个位置上的棋子 （0无棋子  1白子  2黑子）
 
    int step;
 
    int oval_type = 2; //所要下的棋子的颜色 1白 2黑
 
    int mouse_X;
    int mouse_Y;
 
    int select_X = -10;
    int select_Y = -10;
 
    //定义一个Vector，存储每次下的位置，来实现悔棋功能
    Vector<Integer> last_xy = new Vector<>();
 
    boolean isWin;  //是否赢
 
    int isStart;    //是否开始游戏 0未开始 1 2 3
 
    int robot_x;
    int robot_y;
 
    BasicStroke bs; //定义画笔宽度（因为不止一个方法用，就定义在外面）
 
    SpringLayout springLayout = new SpringLayout(); //设置springLayout布局，方便按钮位置的部署
 
    Dimension buttonSize = new Dimension(130, 30);    //设置按钮大小
    //设置字体的形状
    Font font1 = new Font("华文行楷", Font.PLAIN, 30);
    Font font2 = new Font("楷体", Font.PLAIN, 20);
    Font font3 = new Font("华文行楷", Font.PLAIN, 50);
    Font font4 = new Font("华文行楷", Font.PLAIN, 35);
    //定义一系列button和label
    JLabel titleLabel = new JLabel("哈狗五子棋");
    JLabel selectLabel = new JLabel("模式选择:");
    JButton rrBtn = new JButton("人人对战");
    JButton rjbBtn = new JButton("人机.持黑");
    JButton rjwBtn = new JButton("人机.持白");
    JLabel elseLabel = new JLabel("其他设置:");
    JButton regretBtn = new JButton("悔棋");
    JButton restartBtn = new JButton("重新游戏");
    JButton endBtn = new JButton("结束游戏");
 
    public TablePanel() {
        setLayout(springLayout);    //设置弹性布局方式
        setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT)); //设置组件的首选大小
        setBackground(Color.pink); //设置背景颜色
        initBtn();  //初始化按钮
        init(); //初始化一些属性
        isStart = 0;
        addMouseListener(mouseAdapter); //添加鼠标监听
        addMouseMotionListener(mouseAdapter);
    }
 
    //初始化一些属性
    private void init() {
        //初始化二维数组
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                table[i][j] = 0;
            }
        }
 
        //初始化step
        step = 0;
 
        isWin = false;
 
        oval_type = 2;
 
        //初始化list
        last_xy.clear();
    }
 
    @Override
    public void paint(Graphics g) {
        //定义一个Graphics2D
        Graphics2D gg = (Graphics2D) g;
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
 
        //画棋盘
        initPaint(g, gg);
        //画棋子
        ovalPaint(gg);
        //画提示框
        sidePaint(gg);
    }
 
    private void ovalPaint(Graphics2D gg) {
        //画棋子
        //每次点击后，会刷新一下棋盘，根据table的值画黑或白字
 
        //画实体棋子
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                int x = OFFSET_X + SP * i - OVAL_SIZE / 2;
                int y = OFFSET_Y + SP * j - OVAL_SIZE / 2;
                if (table[i][j] == 2) {
                    gg.setColor(Color.BLACK);
                    gg.fillOval(x, y, OVAL_SIZE, OVAL_SIZE);
                } else if (table[i][j] == 1) {
                    gg.setColor(Color.WHITE);
                    gg.fillOval(x, y, OVAL_SIZE, OVAL_SIZE);
                } else if (table[i][j] == 3) {
                    gg.setColor(Color.RED);
                    gg.drawOval(x, y, OVAL_SIZE, OVAL_SIZE);
                }
            }
        }
 
        if (isWin) {
            //赢了就把选择框隐藏起来
            select_X = -10;
            select_Y = -10;
        } else {
            bs = new BasicStroke(1);       // 画笔宽度为1
            gg.setStroke(bs);
            //画选择框
            gg.setColor(Color.RED);
            gg.drawOval(OFFSET_X + SP * select_X - OVAL_SIZE / 2,
                    OFFSET_Y + SP * select_Y - OVAL_SIZE / 2,
                    OVAL_SIZE, OVAL_SIZE);
        }
    }
 
    //画棋盘
    private void initPaint(Graphics g, Graphics2D gg) {
        super.paint(g);
        //画棋盘的线
        g.setColor(Color.BLACK);
        for (int i = 0; i < NUM; i++) {
            g.drawLine(OFFSET_X + SP * i, OFFSET_Y, OFFSET_X + SP * i, OFFSET_Y + SP * (NUM - 1));
        }
        for (int i = 0; i < NUM; i++) {
            g.drawLine(OFFSET_X, OFFSET_Y + SP * i, OFFSET_X + SP * (NUM - 1), OFFSET_Y + SP * i);
        }
 
        //加点点缀
        //五个定位的小方块
        g.fillRect(OFFSET_X + SP * 3 - RECT_SIZE / 2, OFFSET_Y + SP * 3 - RECT_SIZE / 2, RECT_SIZE, RECT_SIZE);
        g.fillRect(OFFSET_X + SP * 11 - RECT_SIZE / 2, OFFSET_Y + SP * 3 - RECT_SIZE / 2, RECT_SIZE, RECT_SIZE);
        g.fillRect(OFFSET_X + SP * 3 - RECT_SIZE / 2, OFFSET_Y + SP * 11 - RECT_SIZE / 2, RECT_SIZE, RECT_SIZE);
        g.fillRect(OFFSET_X + SP * 11 - RECT_SIZE / 2, OFFSET_Y + SP * 11 - RECT_SIZE / 2, RECT_SIZE, RECT_SIZE);
        g.fillRect(OFFSET_X + SP * 7 - RECT_SIZE / 2, OFFSET_Y + SP * 7 - RECT_SIZE / 2, RECT_SIZE, RECT_SIZE);
        //再加几条粗一点的线
        bs = new BasicStroke(3);       // 画笔宽度为5
        gg.setStroke(bs);
        gg.drawRect(OFFSET_X - 7, OFFSET_Y - 7, (NUM - 1) * SP + 14, (NUM - 1) * SP + 14);
        bs = new BasicStroke(2);
        gg.setStroke(bs);
        for (int i = 1; i < NUM; i = i + 4) {
            gg.drawLine(OFFSET_X + SP * i, OFFSET_Y, OFFSET_X + SP * i, OFFSET_Y + SP * (NUM - 1));
        }
        for (int i = 1; i < NUM; i = i + 4) {
            gg.drawLine(OFFSET_X, OFFSET_Y + SP * i, OFFSET_X + SP * (NUM - 1), OFFSET_Y + SP * i);
        }
    }
 
    //画侧面（右上角）的提示框
    private void sidePaint(Graphics2D gg) {
        if (isStart != 0) {
            //开始游戏时
            if (isWin) {
                //赢了后
                gg.setColor((oval_type == 1 ? Color.black : Color.white));
                gg.setFont(font3);
                gg.drawString((oval_type == 1 ? "黑方赢" : "白方赢"), 520, 170);
            } else {
                //没赢之前
                gg.setColor(Color.red);
                gg.setFont(font4);
                gg.drawString("轮到：", 520, 105);
 
                if (oval_type == 2) {
                    gg.setColor(Color.black);
                } else if (oval_type == 1) {
                    gg.setColor(Color.white);
                }
                gg.drawString((oval_type == 2 ? "黑方" : "白方"), 530, 150);
                gg.fillOval(610, 125, 40, 40);
 
                gg.setColor(Color.red);
                gg.drawString("步数：", 520, 200);
                gg.setColor(Color.black);
                gg.drawString(step + "", 620, 200);
            }
        } else {
            gg.setColor(Color.RED);
            gg.setFont(font4);
            gg.drawString("请选择游", 525, 150);
            gg.drawString("戏类型", 525, 190);
        }
    }
 
 
    private void initBtn() {
        //将button和label设置各自的属性
        selectLabel.setFont(font1);
        rrBtn.setPreferredSize(buttonSize);
        rrBtn.setFont(font2);
        rjbBtn.setPreferredSize(buttonSize);
        rjbBtn.setFont(font2);
        rjwBtn.setPreferredSize(buttonSize);
        rjwBtn.setFont(font2);
        elseLabel.setFont(font1);
        regretBtn.setPreferredSize(buttonSize);
        regretBtn.setFont(font2);
        restartBtn.setPreferredSize(buttonSize);
        restartBtn.setFont(font2);
        endBtn.setPreferredSize(buttonSize);
        endBtn.setFont(font2);
        titleLabel.setFont(font3); // 标题
 
        //给按钮加上监听
        rrBtn.addActionListener(actionListener);
        rjbBtn.addActionListener(actionListener);
        rjwBtn.addActionListener(actionListener);
        regretBtn.addActionListener(actionListener);
        restartBtn.addActionListener(actionListener);
        endBtn.addActionListener(actionListener);
 
        //将其放入
        add(selectLabel);
        add(rrBtn);
        add(rjbBtn);
        add(rjwBtn);
        add(elseLabel);
        add(regretBtn);
        add(restartBtn);
        add(endBtn);
        add(titleLabel);
 
        //设置各自的位置，使用弹性布局
 
        //将标题放置到中建位置
        int offsetX = Spring.width(titleLabel).getValue() / 2;
        springLayout.putConstraint(SpringLayout.WEST, titleLabel, -offsetX,
                SpringLayout.HORIZONTAL_CENTER, this);
        springLayout.putConstraint(SpringLayout.NORTH, titleLabel, 10, SpringLayout.NORTH, this);
 
 
        springLayout.putConstraint(SpringLayout.WEST, selectLabel, 525,
                SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.NORTH, selectLabel, 260, SpringLayout.NORTH, this);
 
        springLayout.putConstraint(SpringLayout.WEST, rrBtn, 5,
                SpringLayout.WEST, selectLabel);
        springLayout.putConstraint(SpringLayout.NORTH, rrBtn, 5, SpringLayout.SOUTH, selectLabel);
 
        springLayout.putConstraint(SpringLayout.WEST, rjbBtn, 0,
                SpringLayout.WEST, rrBtn);
        springLayout.putConstraint(SpringLayout.NORTH, rjbBtn, 5, SpringLayout.SOUTH, rrBtn);
 
        springLayout.putConstraint(SpringLayout.WEST, rjwBtn, 0,
                SpringLayout.WEST, rjbBtn);
        springLayout.putConstraint(SpringLayout.NORTH, rjwBtn, 5, SpringLayout.SOUTH, rjbBtn);
 
        springLayout.putConstraint(SpringLayout.WEST, elseLabel, 0,
                SpringLayout.WEST, selectLabel);
        springLayout.putConstraint(SpringLayout.NORTH, elseLabel, 10, SpringLayout.SOUTH, rjwBtn);
 
        springLayout.putConstraint(SpringLayout.WEST, regretBtn, 5,
                SpringLayout.WEST, elseLabel);
        springLayout.putConstraint(SpringLayout.NORTH, regretBtn, 5, SpringLayout.SOUTH, elseLabel);
 
        springLayout.putConstraint(SpringLayout.WEST, restartBtn, 0,
                SpringLayout.WEST, regretBtn);
        springLayout.putConstraint(SpringLayout.NORTH, restartBtn, 5, SpringLayout.SOUTH, regretBtn);
 
        springLayout.putConstraint(SpringLayout.WEST, endBtn, 0,
                SpringLayout.WEST, restartBtn);
        springLayout.putConstraint(SpringLayout.NORTH, endBtn, 5, SpringLayout.SOUTH, restartBtn);
 
        regretBtn.setEnabled(false);
        restartBtn.setEnabled(false);
        endBtn.setEnabled(false);
    }
 
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton jButton = (JButton) e.getSource();
            String text = jButton.getText();
            if ("重新游戏".equals(text)) {
                init();
 
                if(isStart==3){
                    machine();
                    step++;
                    table[robot_x][robot_y] = 2;
                    oval_type = 1;
                }
                regretBtn.setEnabled(false);
                restartBtn.setEnabled(false);
            } else if ("悔棋".equals(text)) {
                int x = last_xy.get(last_xy.size() - 2);
                int y = last_xy.get(last_xy.size() - 1);
                table[x][y] = 0;
                last_xy.remove(last_xy.size() - 2);
                last_xy.remove(last_xy.size() - 1);
                oval_type = oval_type % 2 + 1;
                if (isStart == 2 || isStart == 3) {
                    x = last_xy.get(last_xy.size() - 2);
                    y = last_xy.get(last_xy.size() - 1);
                    table[x][y] = 0;
                    last_xy.remove(last_xy.size() - 2);
                    last_xy.remove(last_xy.size() - 1);
                    oval_type = oval_type % 2 + 1;
                }
 
                if (oval_type == 2||isStart==3) {
                    step--;
                }
                if (isWin) {
                    isWin = false;
                }
                if (last_xy.size() == 0) {
                    regretBtn.setEnabled(false);
                    restartBtn.setEnabled(false);
                }
            } else if ("结束游戏".equals(text)) {
                isStart = 0;
                init();
                rrBtn.setEnabled(true);
                rjbBtn.setEnabled(true);
                rjwBtn.setEnabled(true);
                regretBtn.setEnabled(false);
                restartBtn.setEnabled(false);
                endBtn.setEnabled(false);
            } else {
                //上面三个按钮
                if ("人人对战".equals(text)) {
                    isStart = 1;
                } else if ("人机.持黑".equals(text)) {
                    isStart = 2;
                } else if ("人机.持白".equals(text)) {
                    isStart = 3;
                    machine();
                    step++;
                    table[robot_x][robot_y] = 2;
                    oval_type = 1;
                }
                rrBtn.setEnabled(false);
                rjbBtn.setEnabled(false);
                rjwBtn.setEnabled(false);
                endBtn.setEnabled(true);
            }
            repaint();
        }
    };
 
    MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            //赢的时候不能用
            if (!isWin) {
                if (isStart == 1) {
                    //来判断是否在棋盘内
                    if (e.getX() > OFFSET_X - OVAL_SIZE / 2 && e.getX() < OFFSET_X + (NUM - 1) * SP + OVAL_SIZE / 2
                            && e.getY() > OFFSET_Y - OVAL_SIZE / 2 && e.getY() < OFFSET_Y + (NUM - 1) * SP + OVAL_SIZE / 2) {
 
                        //将坐标转换为二维数组的i和j
                        mouse_X = (e.getX() - OFFSET_X + OVAL_SIZE / 2) / SP;
                        mouse_Y = (e.getY() - OFFSET_Y + OVAL_SIZE / 2) / SP;
                        if (table[mouse_X][mouse_Y] == 0) {
                            table[mouse_X][mouse_Y] = oval_type;
                            if (oval_type == 2) {
                                oval_type = 1;
                                step++; //根据黑棋下的次数来增加总步数
                            } else if (oval_type == 1) {
                                oval_type = 2;
                            }
                            last_xy.add(mouse_X);
                            last_xy.add(mouse_Y);
                            //如果下了棋子，才能使用悔棋和重新游戏的按钮
 
                            restartBtn.setEnabled(true);
                            regretBtn.setEnabled(true);
 
                            judge(oval_type % 2 + 1, mouse_X, mouse_Y);
 
                        }
                        repaint();
                    }
                } else if (isStart == 2) {
 
                    if (e.getX() > OFFSET_X - OVAL_SIZE / 2 && e.getX() < OFFSET_X + (NUM - 1) * SP + OVAL_SIZE / 2
                            && e.getY() > OFFSET_Y - OVAL_SIZE / 2 && e.getY() < OFFSET_Y + (NUM - 1) * SP + OVAL_SIZE / 2) {
                        mouse_X = (e.getX() - OFFSET_X + OVAL_SIZE / 2) / SP;
                        mouse_Y = (e.getY() - OFFSET_Y + OVAL_SIZE / 2) / SP;
                        if (table[mouse_X][mouse_Y] == 0) {
                            table[mouse_X][mouse_Y] = 2;
                            oval_type = 1;
                            last_xy.add(mouse_X);
                            last_xy.add(mouse_Y);
                            repaint();
                            judge(2, mouse_X, mouse_Y);
                            if (!isWin) {
                                machine();
                                table[robot_x][robot_y] = 1;
                                oval_type = 2;
                                judge(1, robot_x, robot_y);
                                last_xy.add(robot_x);
                                last_xy.add(robot_y);
                            }
                            step++;
                            restartBtn.setEnabled(true);
                            regretBtn.setEnabled(true);
                        }
                    }
                } else if (isStart == 3) {
                    if (e.getX() > OFFSET_X - OVAL_SIZE / 2 && e.getX() < OFFSET_X + (NUM - 1) * SP + OVAL_SIZE / 2
                            && e.getY() > OFFSET_Y - OVAL_SIZE / 2 && e.getY() < OFFSET_Y + (NUM - 1) * SP + OVAL_SIZE / 2) {
                        mouse_X = (e.getX() - OFFSET_X + OVAL_SIZE / 2) / SP;
                        mouse_Y = (e.getY() - OFFSET_Y + OVAL_SIZE / 2) / SP;
                        if (table[mouse_X][mouse_Y] == 0) {
                            table[mouse_X][mouse_Y] = 1;
                            oval_type = 2;
                            last_xy.add(mouse_X);
                            last_xy.add(mouse_Y);
                            repaint();
                            judge(1, mouse_X, mouse_Y);
                            if (!isWin) {
                                machine();
                                table[robot_x][robot_y] = 2;
                                oval_type = 1;
                                judge(2, robot_x, robot_y);
                                last_xy.add(robot_x);
                                last_xy.add(robot_y);
                            }
                            step++;
                            restartBtn.setEnabled(true);
                            regretBtn.setEnabled(true);
                        }
                    }
                }
            }
        }
 
        @Override
        public void mouseMoved(MouseEvent e) {
            if (!isWin) {
                if (isStart > 0) {
                    if (e.getX() > OFFSET_X - OVAL_SIZE / 2 && e.getX() < OFFSET_X + (NUM - 1) * SP + OVAL_SIZE / 2
                            && e.getY() > OFFSET_Y - OVAL_SIZE / 2 && e.getY() < OFFSET_Y + (NUM - 1) * SP + OVAL_SIZE / 2) {
                        select_X = (e.getX() - OFFSET_X + OVAL_SIZE / 2) / SP;
                        select_Y = (e.getY() - OFFSET_Y + OVAL_SIZE / 2) / SP;
 
                    } else {
                        select_X = -10;
                        select_Y = -10;
                    }
                }
            }
            repaint();
        }
    };
 
    //判断谁赢，扫描整个棋盘，来判断是否练成五个
    private void judge(int type, int x, int y) {
        //传入参数，来判断是黑（2）或白（1）子
 
        int sum;
        //判断四个方向
        //1.左 右
        sum = 0;
        for (int k = x - 1; k >= 0; k--) {
            if (table[k][y] == type) {
                sum++;
            } else {
                break;
            }
        }
        for (int k = x + 1; k < NUM; k++) {
            if (table[k][y] == type) {
                sum++;
            } else {
                break;
            }
        }
        if (sum >= 4) {
            isWin = true;
            return;
        }
 
        //2.上 下
        sum = 0;
        for (int k = y - 1; k >= 0; k--) {
            if (table[x][k] == type) {
                sum++;
            } else {
                break;
            }
        }
        for (int k = y + 1; k < NUM; k++) {
            if (table[x][k] == type) {
                sum++;
            } else {
                break;
            }
        }
        if (sum >= 4) {
            isWin = true;
            return;
        }
 
        //3。左上 右下
        sum = 0;
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
            if (table[i][j] == type) {
                sum++;
            } else {
                break;
            }
        }
        for (int i = x + 1, j = y + 1; i < NUM && j < NUM; i++, j++) {
            if (table[i][j] == type)
                sum++;
            else {
                break;
            }
        }
        if (sum >= 4) {
            isWin = true;
            return;
        }
 
        //3。右上 左下
        sum = 0;
        for (int i = x - 1, j = y + 1; i >= 0 && j < NUM; i--, j++) {
            if (table[i][j] == type) {
                sum++;
            } else {
                break;
            }
        }
        for (int i = x + 1, j = y - 1; i < NUM && j >= 0; i++, j--) {
            if (table[i][j] == type)
                sum++;
            else {
                break;
            }
        }
        if (sum >= 4) {
            isWin = true;
            //return;
        }
    }
 
 
    //来写自动下棋的方法
    private void machine() {
        //传入棋子种类，判断颜色
 
        int[][] ts = new int[NUM][NUM]; //来记录每个点上的得分
 
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                ts[i][j] = 0;
            }
        }
 
        int wn; //白色个数
        int bn; //黑色个数
 
        //分4种情况
        //横向
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM - 4; j++) {
                wn = 0;
                bn = 0;
                //5个
                for (int k = j; k < j + 5; k++) {
                    if (table[i][k] == 1) {
                        wn++;
                    } else if (table[i][k] == 2) {
                        bn++;
                    }
                }
                for (int k = j; k < j + 5; k++) {
                    if (table[i][k] == 0) {
                        ts[i][k] += score(wn, bn);
                    }
                }
            }
        }
 
        //纵向
        for (int j = 0; j < NUM; j++) {
            for (int i = 0; i < NUM - 4; i++) {
                wn = 0;
                bn = 0;
                for (int k = i; k < i + 5; k++) {
                    if (table[k][j] == 1) {
                        wn++;
                    } else if (table[k][i] == 2) {
                        bn++;
                    }
                }
                for (int k = i; k < i + 5; k++) {
                    if (table[k][i] == 0) {
                        ts[k][i] += score(wn, bn);
                    }
                }
            }
        }
 
        //左上 右下
        for (int i = 0; i < NUM - 4; i++) {
            for (int j = 0; j < NUM - 4; j++) {
                wn = 0;
                bn = 0;
                for (int ki = i, kj = j; ki < i + 5; ki++, kj++) {
                    if (table[ki][kj] == 1) {
                        wn++;
                    } else if (table[ki][kj] == 2) {
                        bn++;
                    }
                }
                for (int ki = i, kj = j; ki < i + 5; ki++, kj++) {
                    if (table[ki][kj] == 0) {
                        ts[ki][kj] += score(wn, bn);
                    }
                }
            }
        }
 
        //右上 左下
        for (int i = 4; i < NUM; i++) {
            for (int j = 0; j < NUM - 4; j++) {
                wn = 0;
                bn = 0;
                for (int ki = i, kj = j; kj < j + 5; ki--, kj++) {
                    if (table[ki][kj] == 1) {
                        wn++;
                    } else if (table[ki][kj] == 2) {
                        bn++;
                    }
                }
                for (int ki = i, kj = j; kj < j + 5; ki--, kj++) {
                    if (table[ki][kj] == 0) {
                        ts[ki][kj] += score(wn, bn);
                    }
                }
            }
        }
 
        Vector<Integer> vv = new Vector<>();
        int max = Integer.MIN_VALUE;
 
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                if (ts[i][j] > max) {
                    max = ts[i][j];
                }
            }
        }
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                if (ts[i][j] == max) {
                    vv.add(i);
                    vv.add(j);
                }
            }
        }
        Random random = new Random();
        int r = random.nextInt(vv.size() / 2);
        robot_x = vv.get(r * 2);
        robot_y = vv.get(r * 2 + 1);
        vv.clear();
    }
 
    private int score(int w, int b) {
        if (w > 0 && b > 0) {
            return 0;
        }
        if (w == 0 && b == 0) {
            return 7;
        }
        if (w == 1) {
            return 35;
        }
        if (w == 2) {
            return 800;
        }
        if (w == 3) {
            return 15000;
        }
        if (w == 4) {
            return 800000;
        }
        if (b == 1) {
            return 15;
        }
        if (b == 2) {
            return 400;
        }
        if (b == 3) {
            return 1800;
        }
        if (b == 4) {
            return 100000;
        }
 
        return -1;
    }
}