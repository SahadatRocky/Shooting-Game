
package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author CR7
 */
  public class GamePanel extends JPanel implements Runnable,KeyListener{
    //variable
     
    public static int width=500;
    public static int height=500;
    
    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private Graphics2D g;
    private int FPS= 30;
    private double averageFPS;
    
    public static Player player;
    public static ArrayList<Bullet>bullets;
    public static ArrayList<Enemy>enemies;
    public static ArrayList<PowerUp>powerup;
    
    private long levelStartTimer;
    private long levelStartTimerDiff;
    private int levelNumber;
    private boolean levelStart;
    private int levelDelay=2000;
   
    
    //constractor
    
    public GamePanel(){
        super();
        setPreferredSize(new Dimension(width,height));
        setFocusable(true);
        requestFocus();      
    }
    
    public void setLeft(boolean b){boolean left = b;}
    public void setRight(boolean b){boolean right = b;}
    public void setUp(boolean b){boolean up = b;}
    public void setDown(boolean b){boolean down = b;}
    public void setFiring(boolean b){boolean firing = b;}
    //function 
    public void addNotify(){
         super.addNotify();
         if(thread==null){
         thread=new Thread(this);
         thread.start();
       }
          addKeyListener(this);
    }

    @Override
    public void run() {
       running=true;
       image=new BufferedImage( width, height,BufferedImage.TYPE_INT_RGB);
       g=(Graphics2D)image.getGraphics();
       
       player=new Player();
       bullets=new ArrayList<Bullet>();
       enemies=new ArrayList<Enemy>();
       powerup=new ArrayList<PowerUp>();
       
      /* for(int i=0;i<5;i++){
          enemies.add(new Enemy(1,1));
       }
       */
          levelStartTimer=0;
         levelStartTimerDiff=0;
         levelStart=true;
         levelNumber=0;
        
      
       long startTime;
       long TimeMillis;
       long waitTime;
       long totalTime=0;
       long tergetTime=1000/FPS;
       
       int frameCount=0;
       int maxFramecount=30;
       
       
       // Game loop
       while(running){
            gameUpdate();
            gameRender();
            gameDraw();
            startTime=System.nanoTime();
            TimeMillis=(System.nanoTime()-startTime)/1000000;
            
            waitTime=tergetTime-TimeMillis;
            try{
             Thread.sleep(waitTime);
            }
            catch(Exception e){
              
            }
             totalTime +=System.nanoTime()-startTime;
            frameCount++;
            
            if(frameCount==maxFramecount){
                averageFPS=1000.0/((totalTime/frameCount)/1000000);
                    frameCount=0;
                    totalTime=0;
            }
            
       }
        
       g.setColor(Color.BLUE);
       g.fillRect(0, 0, WIDTH, HEIGHT);
       g.setColor(Color.WHITE);
       g.setFont(new Font("Century Gothic",Font.PLAIN,20));
       String s=" G A M E    O V E R  ";
       int length=(int)g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s,(width-length)/2,height/2);
        gameDraw();
    }

    private void gameUpdate() {
        //new level
        if(levelStartTimer==0 && enemies.size()==0){
           levelNumber++;
           levelStart=false;
           levelStartTimer=System.nanoTime();
        }
        else{
        
         levelStartTimerDiff=(System.nanoTime()-levelStartTimer)/1000000;
         if(levelStartTimerDiff>levelDelay){
             levelStart=true;
             levelStartTimer=0;
             levelStartTimerDiff=0;
          }
        }
        
        //  create enemies
         if(levelStart && enemies.size()==0){
            createNewEnemies();
         }
        
       //player update
       player.update();
       
       //Bullet update
       for(int i=0;i<bullets.size();i++){
         boolean remove= bullets.get(i).update();
            if(remove){
                bullets.remove(i);
                 i--;            
            }
       }
        //Enemy update
          for(int i=0;i<enemies.size();i++){
              enemies.get(i).update();
          }
          
          //power up update
          
          for(int i=0;i<powerup.size();i++){
         boolean remove = powerup.get(i).update();
         if(remove){
           powerup.remove(i);
           i--;
              }
          }
      
        /*
          //player powerup collision  
      int plx=player.getX();
      int ply=player.getY();
      int plr=player.getR();
      
      for(int i=0; i<powerup.size();i++){
       
          PowerUp p=powerup.get(i);
          double x=p.getX();
          double y=p.getY();
          double r=p.getR();
          double dx=plx-x;
          double dy=ply-y;
         double dist=Math.sqrt(dx*dx+dy*dy);
         
         //collected powerup
         
         if(dist<plr+r){
           
             int type=p.getType();
             
             if(type==1){
                player.gainLife();
             }
             if(type==2){
                player.increasePower(1);
             }
             if(type==3){
                 player.increasePower(2);
             }
             
         
         }
         
      }
      */
          
    }

    private void gameRender(){
        
        //draw backgroung
       g.setColor(Color.BLACK);
       g.fillRect(0, 0, width, height);
       
       /*
        g.setColor(Color.BLACK);
         g.drawString("FPS:"+averageFPS,10,10);
         g.drawString("num of bullets:"+bullets.size(),10,20);
       */
        
       
        
        //draw player
        player.draw(g);
        
        //draw Bullet
        for(int i=0;i< bullets.size();i++){
          bullets.get(i).draw(g);
        }
        //draw Enemy
        for(int i=0;i<enemies.size();i++){
              enemies.get(i).draw(g);
        }
        //draw power up
        for(int i=0;i<powerup.size();i++){
         powerup.get(i).draw(g);
        }
        
      /*  //draw player power
        g.setColor(Color.YELLOW);
        g.fillRect(20, 40,player.getPower()*8,8);
        g.setColor(Color.YELLOW.darker());
        g.setStroke(new BasicStroke(2));
        for(int i=0;i<player.getRequiredPower();i++){
         g.drawRect(20+8*i, 40, 8, 8);
        }
        g.setStroke(new BasicStroke(1));
        */
   
      //bullet Enemy Collision
        for(int i=0;i<bullets.size();i++){
         Bullet b=bullets.get(i);
         double bx=b.getX();
         double by=b.getY();
         double br=b.getR();
        for(int j=0;j<enemies.size();j++){
          Enemy e=enemies.get(j);
          double ex=e.getX();
          double ey=e.getY();
          double er=e.getR();
          
          double dx=bx-ex;
          double dy=by-ey;
          double dist=Math.sqrt(dx*dx+dy*dy);
          if(dist<br+er){
          e.hit();
          bullets.remove(i);
          i--;
          break;
             }
          }
        }
      
      //check dead enemies
       for(int i=0;i<enemies.size();i++){
           if(enemies.get(i).Dead()){
              Enemy e=enemies.get(i);
              
              // Roll for powerup
                 double rand=Math.random();
               if(rand<0.001) powerup.add(new PowerUp(1,e.getX(),e.getY()));
              else if(rand<0.020) powerup.add(new PowerUp(3,e.getX(),e.getY()));
               else if(rand<0.120) powerup.add(new PowerUp(2,e.getX(),e.getY()));
              
              
               
              player.addScore(e.getType()+e.getRank());
           enemies.remove(i);
           i--;
           e.explode();
           }
           
           //check dead player
           if(player.isDead()){ 
           running=false; 
           
           }
           
           
           
           
           //player-Enemy Collision
           if(!player.Recovery()){
              int px=player.getX();
               int py=player.getY();
              int pr=player.getR();
          for(int k=0; k<enemies.size(); k++){
               
               Enemy e=enemies.get(k);
               double ex=e.getX();
               double ey=e.getY();
               double er=e.getR();
                
               double dx=px-ex;
               double dy=py-ey;
               double dist=Math.sqrt(dx*dx +dy*dy);
               
               if(dist<pr+er){
               player.lostLife();
                 }
 
              }
              
           }
           
           
           
       }
        
       //draw level number
       if(levelStartTimer !=0){
       g.setFont(new Font("Century Gothic",Font.PLAIN,18));
       String s="   level   "+levelNumber +"  ";
       int length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
       int alpha=(int)(255*Math.sin(3.14 *levelStartTimerDiff/levelDelay));
       if(alpha>255)alpha=255;
       g.setColor(new Color(255,255,255,alpha)); 
       g.drawString(s,width/2-length/2,height/2);
       }
      //draw player lives
      for(int i=0;i<player.getLives();i++){
          g.setColor(Color.WHITE);
          g.fillOval(20 +(20*i), 20,player.getR()*2,player.getR()*2);
          g.setStroke(new BasicStroke(3));
          g.setColor(Color.WHITE.darker());
          g.drawOval(20 +(20*i), 20,player.getR()*2,player.getR()*2);
          g.setStroke(new BasicStroke(1));
      }
      //draw player score
      g.setColor(Color.WHITE);
      g.setFont(new Font("Century Gothic",Font.PLAIN,14));
       g.drawString("Score :"+player.getScore(),width-100,30) ;
    }

    
    private void gameDraw() {
       Graphics g2=this.getGraphics();
      g2.drawImage(image,0,0, null);
       g2.dispose();
    }

    
    private void createNewEnemies(){
      
       enemies.clear();
       Enemy e;
       
       if(levelNumber==1){
          for(int i=0;i<5;i++){
             enemies.add(new Enemy(1,1));
          }
       }
       if(levelNumber==2){
         for(int i=0;i<8;i++){
           enemies.add(new Enemy(1,1));
         }
         enemies.add(new Enemy(1,2));
         enemies.add(new Enemy(1,2));
       }
       
       if(levelNumber==3){
           for(int i=0;i<4;i++){
           enemies.add(new Enemy(1,1));
         }
          enemies.add(new Enemy(1,2));
          enemies.add(new Enemy(1,2));
        
         
       }
       if(levelNumber==4){
           enemies.add(new Enemy(1,3));
          enemies.add(new Enemy(1,4));
           for(int i=0;i<4;i++){
           enemies.add(new Enemy(2,1));
         }
           
           if(levelNumber==5){
               enemies.add(new Enemy(1,4));          
           enemies.add(new Enemy(1,3));
          enemies.add(new Enemy(2,3));
           
         }
           if(levelNumber==6){
               enemies.add(new Enemy(1,3));
               for(int i=0;i<4;i++){
           enemies.add(new Enemy(2,1));
          enemies.add(new Enemy(3,1));
           
              }
          
           }
         if(levelNumber==7){
               enemies.add(new Enemy(1,3));
           enemies.add(new Enemy(2,3));
          enemies.add(new Enemy(3,3));
           
              
          
           } 
         
         if(levelNumber==8){
               enemies.add(new Enemy(1,4));
           enemies.add(new Enemy(2,4));
          enemies.add(new Enemy(3,4));
           
              
          
           } 
         if(levelNumber==9){
             running =false;
           
              
          
           } 
       }
       
       
    }
    
    
    
    
    @Override
    public void keyTyped(KeyEvent e) {
       
    }

    @Override
    public void keyPressed(KeyEvent e) {
       
        int keyCode=e.getKeyCode();
        if(keyCode==KeyEvent.VK_LEFT){
        player.setLeft(true);
        }
        
        if(keyCode==KeyEvent.VK_RIGHT){
        player.setRight(true);
        }
        
        if(keyCode==KeyEvent.VK_UP){
        player.setUp(true);
        }
        
        if(keyCode==KeyEvent.VK_DOWN){
        player.setDown(true);
        }
        if(keyCode==KeyEvent.VK_Z){
        player.setFiring(true);
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
       
        int keyCode=e.getKeyCode();
        if(keyCode==KeyEvent.VK_LEFT){
        player.setLeft(false);
        }
        
        if(keyCode==KeyEvent.VK_RIGHT){
        player.setRight(false);
        }
        
        if(keyCode==KeyEvent.VK_UP){
        player.setUp(false);
        }
        
        if(keyCode==KeyEvent.VK_DOWN){
        player.setDown(false);
        }
        if(keyCode==KeyEvent.VK_Z){
        player.setFiring(false);
        }
        
    }
    
    
}
