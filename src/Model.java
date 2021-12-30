import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Dimension d; //lebar dan tinggi dari field
	private final Font smallFont = new Font("Arial", Font.BOLD, 14); // tampilan teks pada game
	private boolean inGame = false;
	private boolean dying = false;
	
	private final int Blocks_size = 36; // luas blocks pada game
	private final int Blocks_num = 15; // jumlah blocks. tinggi 15. lebar 15
	private final int Screen_size = Blocks_size * Blocks_num; // ukuran screen
	private final int Max_Germs = 12; // jumlah maksimum dari kuman
	private final int Cooman_Speed = 6; // kecepatan dari cooman
	
	private int Germs_sum = 6; // kuman berjumlah 6
	private int lives, score, level=1;
	private int[] dx, dy; // posisi dari kuman
	private int[] germs_x, germs_y, germs_dx, germs_dy, germs_speed;
	
	private Image heart, germs;
	private Image up, down, left, right;
	
	private int cooman_x, cooman_y; // koordinat dari cooman 
	private int cooman_dx, cooman_dy; // arah vertikal dan horizontal dari cooman
	private int req_dx, req_dy;
	
	private final short levelData[] = {
			 19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
	         21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 24, 24, 24, 24, 20,
	         21, 0, 0, 0, 17, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	         21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 18, 18, 18, 18, 20,
	         17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
	         17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
	         17, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 20,
	         17, 16, 16, 20,  0,  0,  0,  0,  0,  0,  0, 17, 16, 24, 20,
	         17, 16, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
	         17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
	         17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
	         25, 24, 24, 24, 24, 16, 16, 18, 16, 16, 16, 16, 28, 0, 21,
	         0,  0,  0,  0,  0,  17, 16, 16, 16, 16, 16, 20, 0, 0, 21,
	         19, 18, 18, 18, 18, 16, 16, 16, 16, 16, 16, 16, 18, 18, 20,
	         25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
	};
	
	private final int validSpeeds[] = {1, 2, 3, 4, 5, 6};
	private final int maxSpeed = 6;
	
	private int currentSpeed = 3;
	private short[] screenData;
	private Timer timer;
	
	public Model() {
		loadImages();
		initVariables();
		addKeyListener(new TAdapter());
		setFocusable(true);
		initGame();
	}
	
	private void loadImages() { // memuat gambar yang muncul pada game
		down = new ImageIcon("C:\\Users\\Alya Shofa\\Documents\\TUGAS\\SMT 3\\PBO\\FPFPFPPFPFPF\\down.gif").getImage();
    	up = new ImageIcon("C:\\Users\\Alya Shofa\\Documents\\TUGAS\\SMT 3\\PBO\\FPFPFPPFPFPF\\up.gif").getImage();
    	left = new ImageIcon("C:\\Users\\Alya Shofa\\Documents\\TUGAS\\SMT 3\\PBO\\FPFPFPPFPFPF\\left.gif").getImage();
    	right = new ImageIcon("C:\\Users\\Alya Shofa\\Documents\\TUGAS\\SMT 3\\PBO\\FPFPFPPFPFPF\\right.gif").getImage();
    	germs = new ImageIcon("C:\\Users\\Alya Shofa\\Documents\\TUGAS\\SMT 3\\PBO\\FPFPFPPFPFPF\\kuman.gif").getImage();
        heart = new ImageIcon("C:\\Users\\Alya Shofa\\Documents\\TUGAS\\SMT 3\\PBO\\FPFPFPPFPFPF\\heart.png").getImage();
	}
	
	private void initVariables() {
		screenData = new short[Blocks_size * Blocks_num];
		d = new Dimension (550, 580);
		germs_x = new int[Max_Germs];
		germs_dx = new int[Max_Germs];
		germs_y = new int [Max_Germs];
		germs_dy = new int [Max_Germs];
		germs_speed = new int [Max_Germs];
		dx = new int [4];
		dy = new int[4];
		
		timer = new Timer(40, this);
		timer.start();
	}
	
	private void playGame(Graphics2D g2d) {
		if(dying) {
			death(); // apabila cooman mati maka memanggil fungsi death
		}else {
			moveCooman();
			drawCooman(g2d);
			moveGerms(g2d);
			checkMaps();
		}
	}
	
	private void showIntroScreen(Graphics2D g2d) {
		String start = "Press SPACE to start";
		g2d.setColor(Color.yellow);
		g2d.drawString(start, (Screen_size)/3 + 22, 270);
	}
	
	private void drawScore(Graphics2D g) {
		g.setFont(smallFont);
		g.setColor(new Color(5, 181, 79));
		String s = "Score: " + score;
		g.drawString(s, Screen_size / 2 + 190, Screen_size + 20);
		
		String l = "Level: " +level; // menambah level
		g.drawString(l, Screen_size / 2 - 30, Screen_size + 20);
		
		for(int i = 0; i < lives; i++) { //cek sisa dari lives
			g.drawImage(heart, i*28+8, Screen_size + 1, this);
		}
	}
	
	private void checkMaps() {
		int i = 0;
		boolean finished = true;
		
		while(i< Blocks_num * Blocks_num && finished) {
			if((screenData[i] & 48) !=0) {  // cek sisa makanan dari maps
				finished = false;
			}
			i++;
		}
		
		if(finished) {
			score +=50;
			level++; // increase level
			
			if(Germs_sum < Max_Germs) {
				Germs_sum++;
			}
			
			if(currentSpeed < maxSpeed) {
				currentSpeed++; //kecepatan ghost bertambah
			}
			
			initLevel();
		}
	}
	
	private void death() {
		lives--; //cooman mati, lives berkurang dan game akan terus berjalan sampai lives = 0
		
		if(lives==0) {
			inGame = false;
		}
		
		continueLevel();
	}
	
	private void moveGerms(Graphics2D g2d) {
		int pos;
		int count;
		
		for(int i = 0; i < Germs_sum; i++) { //atur posisi germs
			if(germs_x[i] % Blocks_size == 0 && germs_y[i] % Blocks_size == 0) {
				pos = germs_x[i] / Blocks_size + Blocks_num * (int)(germs_y[i] / Blocks_size);
				
				count = 0;
				
				if((screenData[pos] & 1) == 0 && germs_dx[i]!= 1) {
					dx[count] = -1;
					dy[count] = 0;
					count++;
				}
				
				if((screenData[pos] & 2) == 0 && germs_dx[i]!= 1) {
					dx[count] = 0;
					dy[count] = -1;
					count++;
				}
				
				if((screenData[pos] & 4) == 0 && germs_dx[i]!= -1) {
					dx[count] = 1;
					dy[count] = 0;
					count++;
				}
				
				if((screenData[pos] & 8) == 0 && germs_dx[i]!= -1) {
					dx[count] = 0;
					dy[count] = 1;
					count++;
				}
				
				if(count == 0) {
					if((screenData[pos] & 15) == 15) {
						germs_dx[i] = 0;
						germs_dy[i] = 0;
					}else {
						germs_dx[i] = -germs_dx[i];
						germs_dy[i] = -germs_dy[i];
					}
					
				}else {
					count = (int)(Math.random()*count);
					
					if(count > 3) {
						count = 3;
					}
					
					germs_dx[i] = dx[count];
					germs_dy[i] = dy[count];
				}
			}
			
			
			germs_x[i] = germs_x[i] + (germs_dx[i]*germs_speed[i]);
			germs_y[i] = germs_y[i] + (germs_dy[i]*germs_speed[i]);
			drawGerms(g2d, germs_x[i]+1, germs_y[i]+1);
			
			if(cooman_x > (germs_x[i]-12) && cooman_x < (germs_x[i]+12)
					&& cooman_y > (germs_y[i]-12) && cooman_y < (germs_y[i]+12) 
					&& inGame) {
				
				dying = true; 
			}
		}
	}
	
	private void drawGerms (Graphics2D g2d, int x, int y) {
		g2d.drawImage(germs,  x,  y , this);
	}
	
	private void moveCooman() { //menentukan posisi cooman
		
		int pos;
		short ch;
		
		if(cooman_x % Blocks_size == 0 && cooman_y % Blocks_size == 0) {
			pos = cooman_x / Blocks_size + Blocks_num * (int)(cooman_y / Blocks_size);
			ch = screenData[pos];
			
			if ((ch & 16) != 0) { //apabila cooman berada pada posisi 16 maka score bertambah
				screenData[pos] = (short)(ch & 15);
				score++;
			}
			
			if(req_dx != 0 || req_dy != 0) {
				if(!((req_dx == -1 && req_dy == 0 && (ch & 1)!= 0)
						|| (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
						|| (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
						|| (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
					cooman_dx = req_dx;
					cooman_dy = req_dy;
				}
			}
			
			//Check for standstill
			if((cooman_dx == -1 && cooman_dy == 0 && (ch & 1)!= 0)
					|| (cooman_dx == 1 && cooman_dy == 0 && (ch & 4) != 0)
					|| (cooman_dx == 0 && cooman_dy == -1 && (ch & 2) != 0)
					|| (cooman_dx == 0 && cooman_dy == 1 && (ch & 8) != 0)) {
				cooman_dx = 0;
				cooman_dy = 0;
			}
		}	
		cooman_x = cooman_x + Cooman_Speed * cooman_dx;
		cooman_y = cooman_y + Cooman_Speed * cooman_dy;
	}
	
	private void drawCooman(Graphics2D g2d) {  
		if(req_dx == -1) {
			g2d.drawImage(left, cooman_x + 1, cooman_y + 1, this);
		}else if (req_dx == 1) {
			g2d.drawImage(right, cooman_x + 1, cooman_y + 1, this);
		}else if (req_dy == -1) {
			g2d.drawImage(up, cooman_x + 1, cooman_y + 1, this);
		}else{
			g2d.drawImage(down, cooman_x + 1, cooman_y + 1, this);
		}
	}
	
	private void drawMaps(Graphics2D g2d) {
		short i = 0;
		int x, y;
		
		for(y = 0; y < Screen_size; y += Blocks_size) {
			for (x = 0; x < Screen_size; x += Blocks_size) {
				
				g2d.setColor(new Color(0, 72, 251));
				g2d.setStroke(new BasicStroke(5));
				
				if((levelData[i] == 0)) {
					g2d.fillRect(x, y, Blocks_size, Blocks_size);
				}
				
				if((screenData[i] & 1) != 0) {
					g2d.drawLine(x, y, x, y + Blocks_size - 1);
				}
				
				if((screenData[i] & 2) != 0) {
					g2d.drawLine(x, y, x + Blocks_size - 1, y);
				}
				
				if((screenData[i] & 4) != 0) {
					g2d.drawLine(x + Blocks_size - 1, y, x + Blocks_size - 1, y + Blocks_size - 1);
				}
				
				if((screenData[i] & 8) != 0) {
					g2d.drawLine(x, y + Blocks_size - 1, x + Blocks_size - 1 , y + Blocks_size - 1);
				}
				
				if((screenData[i] & 16) != 0) {
					g2d.setColor(new Color(255, 255, 255));
					g2d.fillOval(x + 10, y + 10, 6, 6);
				}
				
				i++;
			}
		}
	}
	
	private void initGame() {
		lives = 3;
		score = 0;
		initLevel();
		Germs_sum = 6;
		currentSpeed = 3;
	}
	
	private void initLevel() {
		int i;
		for(i = 0; i < Blocks_num * Blocks_num; i ++) {
			screenData[i] = levelData[i];
		}
		
		continueLevel();
	}
	
	private void continueLevel() {
		int dx = 1;
		int random;
		
		for(int i = 0; i < Germs_sum; i++) {
			germs_y[i] = 4 * Blocks_size;
			germs_x[i] = 4 * Blocks_size;
			germs_dy[i] = 0;
			germs_dx[i] = dx;
			dx = -dx;
			random = (int)(Math.random()*(currentSpeed + 1));
			
			if(random > currentSpeed) {
				random = currentSpeed;
			}
			germs_speed[i] = validSpeeds[random];
		}
		
		cooman_x = 7 * Blocks_size; // posisi awal cooman
		cooman_y = 11 * Blocks_size;
		cooman_dx = 0;
		cooman_dy = 0;
		req_dx = 0;
		req_dy = 0;
		dying = false;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, d.width, d.height);
		
		drawMaps(g2d);
		drawScore(g2d);
		
		if(inGame) {
			playGame(g2d);
		}else {
			showIntroScreen(g2d);
		}
		
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	}
	
	//controls
	class TAdapter extends KeyAdapter{
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			int key = e.getKeyCode();
			if(inGame) {
				if(key == KeyEvent.VK_LEFT) {
					req_dx = -1;
					req_dy = 0;
				} else if (key == KeyEvent.VK_RIGHT) {
					req_dx = 1;
					req_dy = 0;
				} else if (key == KeyEvent.VK_UP) {
					req_dx = 0;
					req_dy = -1;
				} else if (key == KeyEvent.VK_DOWN) {
					req_dx = 0;
					req_dy = 1;
				} else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
					inGame = false;
				}
			} else {
				if(key == KeyEvent.VK_SPACE) {
					inGame = true;
					initGame();
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}
