package hellotvxlet;


import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Timer;
import javax.tv.xlet.*;
import org.havi.ui.event.*;
import org.dvb.event.*;
import java.awt.event.*;
import org.havi.ui.*;
import java.util.Random;



public class HelloTVXlet implements Xlet,UserEventListener, ObserverInterface{
    
    static HScene scene = null;  
    static Subject publisher = null;
    blokken blok = null;
    balls cube = null;
    blokken ending = null;
    Rectangle[][] doelenGrenzen = new Rectangle[5][3];
    blokken[][] doelen = new blokken[5][3];
    Random rndNmbr = new Random();

    Timer timer;
    boolean space = false;
    
    private HText pointsNbr;
    private HText score;
    private HText deadText;
    private HText winningText;
    private HText suckText;
    int points = 0;
    int snelheid = 5;

   public static HScene getScene(){
    return scene;
    }
    public static Subject getPublisher(){
    return publisher;
    }
    public void destroyXlet(boolean unconditional) throws XletStateChangeException {
        
    }
    public void CollideBalk(){

        Rectangle blokGrens = blok.getRect();
        Rectangle cubeGrens = cube.getRect();
        if( dead() == 0) {
            endGame();
        }
        if(cubeGrens.y > 720)
        {
            endGame();
        }
        if(cubeGrens.intersects(blokGrens))
        {
            cube.setYDir(-1);
        }
        
        for(int i=0;i<5;i++){
            for(int j=0;j<3;j++){
                    if(cubeGrens.intersects(doelenGrenzen[i][j])){
                                               
                        Point pointRight = new Point(cubeGrens.x + cubeGrens.width + 1, cubeGrens.y);
                        Point pointLeft = new Point(cubeGrens.x - 1, cubeGrens.y);
                        Point pointTop = new Point(cubeGrens.x, cubeGrens.y - 1);
                        Point pointBottom = new Point(cubeGrens.x, cubeGrens.y + cubeGrens.height + 1);
                        if(!doelen[i][j].isDestroyed()){
                            
                            points++;
                            
                            scene.remove(pointsNbr);                                                     
                            pointsNbr = new HText("Score: " + points);
                            pointsNbr.setLocation(600,490);
                            pointsNbr.setSize(100,50);
                            pointsNbr.setBordersEnabled(false);
                            scene.add(pointsNbr);
                            
                            if (doelenGrenzen[i][j].contains(pointTop)) {
                            cube.setYDir(1);
                                if(cube.getXDir() == 1){
                                    cube.setXDir(1);
                                }
                                else{
                                    cube.setXDir(-1);
                                }                                     
                            } 
                            if (doelen[i][j].contains(pointBottom)) {
                                cube.setYDir(-1);
                                if(cube.getXDir() == 1){
                                    cube.setXDir(-1);
                                }
                                else{
                                    cube.setXDir(1);
                                }
                            }
                            if (doelenGrenzen[i][j].contains(pointRight)) {
                            cube.setXDir(-1);
                                
                            } else if (doelenGrenzen[i][j].contains(pointLeft)) {
                            cube.setXDir(1);
                            }
                        }
                        doelen[i][j].setDestroyed(true);                  
                        scene.remove(doelen[i][j]);       
                        scene.repaint();
                        scene.validate();
                        scene.setVisible(true);
                      
                    }
            }
        }
    }

    public void initXlet(XletContext ctx) throws XletStateChangeException {
        
        points = 0;       
        scene  = HSceneFactory.getInstance().getDefaultHScene();
        publisher = new Subject();
        timer = new Timer();
        timer.scheduleAtFixedRate(publisher,0, snelheid);
        pointsNbr = new HText("Score: " + points);
        pointsNbr.setLocation(600,490);
        pointsNbr.setSize(100,50);
        pointsNbr.setBordersEnabled(false);
        scene.add(pointsNbr);

        blok = new blokken(280,510,150,15, Color.BLUE);
        scene.add(blok);
        int width = 60;
        int height = 15;
               for(int i=0;i<5;i++){
                   for(int j=0;j<3;j++){
                      blokken bricks = new blokken(60+120*i,60+16*j,width,height, Color.CYAN);
                      scene.add(bricks);
                      doelenGrenzen[i][j] = bricks.getRect();
                      doelen[i][j] = bricks;
                   }
               }
        cube =  new balls(350,400,13,13);
        scene.add(cube);        
        scene.repaint();
        scene.validate();
        scene.setVisible(true);
        publisher.register(this);

    }
    
    public void pauseXlet() {
        
    }

    public void startXlet() throws XletStateChangeException {
        
        EventManager  mngr = EventManager.getInstance();
        UserEventRepository repo = new UserEventRepository("Keys");
        repo.addAllArrowKeys();
        repo.addKey(HRcEvent.VK_SPACE);
        mngr.addUserEventListener(this, repo);     
    }

    public void userEventReceived(UserEvent e) {
        int value = rndNmbr.nextInt(3); 
      if(e.getType() == KeyEvent.KEY_PRESSED){
      switch(e.getCode()){

            case HRcEvent.VK_SPACE:  
                if (space == false)
                {
                    publisher.register(cube);
                    cube.setXDir(value -1);
                    space = true;
                }
               
              break;               
           case HRcEvent.VK_RIGHT: 
              blok.MoveRight();
              break; 
           case HRcEvent.VK_LEFT: 
              blok.MoveLeft();
              break;
            }
      }
    }
     public void endGame(){
        
        scene.remove(blok);
        scene.remove(cube);
        score = new HText("Score: " + points);
        ending = new blokken(200,200,300,200,Color.WHITE);
        
        if( dead() == 1) {
            
            for(int i=0;i<5;i++){
                   for(int j=0;j<3;j++){
                       scene.remove(doelen[i][j]);
                   }
            }
            scene.remove(pointsNbr); 
        score.setForeground(Color.BLACK);
        score.setLocation(250,250);
        score.setSize(200,50);
        score.setBordersEnabled(false);
        scene.add(score);
        
        deadText = new HText("GAME OVER");
         
        deadText.setForeground(Color.BLACK);
        deadText.setLocation(250,175);
        deadText.setSize(200,100);
        deadText.setBordersEnabled(false);
        scene.add(deadText);
        suckText = new HText("You suck!");
        
        suckText.setLocation(300,325);
       suckText.setBackground(Color.BLACK);
        suckText.setBackgroundMode(HVisible.BACKGROUND_FILL);
        suckText.setSize(100,50);
   
        scene.add(suckText); 
        
        scene.add(ending);
       
        scene.validate();
        scene.setVisible(true);
        scene.repaint();
        }
        else
        {
         
            winningText = new HText("Congrats, you finished!");
             winningText.setForeground(Color.BLACK);
        winningText.setLocation(155 ,250);
        winningText.setSize(400,50);
        winningText.setBordersEnabled(false);
        scene.add(winningText);
          score.setForeground(Color.BLACK);
        score.setLocation(250,300);
        score.setSize(200,50);
        score.setBordersEnabled(false);
        scene.add(score);
        scene.add(ending);
        }  
    }  
    
    public void update(int tijd) {
       CollideBalk();
       
       
    }
    
    public int dead() {
        
        int x;
        x = 0;
        
        for(int i=0;i<5;i++){
           
            for(int j=0;j<3;j++){
                
                if(!doelen[i][j].isDestroyed())
                {
                    x = 1;
                }
            }
       }
        return x;   
    }
    
}
