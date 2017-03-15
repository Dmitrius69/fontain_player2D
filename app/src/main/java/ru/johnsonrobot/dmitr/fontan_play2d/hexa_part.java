package ru.johnsonrobot.dmitr.fontan_play2d;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by dmitr on 07.02.2017.
 */

public class hexa_part {

    private Bitmap imgMain;
    private Bitmap imgReflect;
    private int startX, startY;
    private int iSpeed;
    private double angle;
    private Matrix mat;
    private Paint paint;


    //конструктор класса
    public hexa_part (Resources res, int idBitmap, int X, int Y, double ang, int speed) {
        mat = new Matrix();
        mat.preScale(1, -1);
        imgMain = BitmapFactory.decodeResource(res, idBitmap);
        int width = imgMain.getWidth();
        int height = imgMain.getHeight();
        imgReflect = Bitmap.createBitmap(imgMain, 0,height/2,width,height/2,mat, false);
        angle = ang;
        iSpeed = speed;
        startX = X;
        startY = Y;

    }


    public Rect getImageRectangle()
    {
        int w = imgMain.getWidth();
        int h = imgMain.getHeight();
        Rect r = new Rect(startX, startY, startX + w, startY + h);
        return r;
    }

    public int getSpeed() { return iSpeed; }

    public void setSpeed(int speed) { iSpeed = speed; }

    public double getAngle() { return  angle ;  }

    public  void setAngle(double a)  { angle = a; }

    public void onUpdate()
    {
        //переводим радианы в декартовы координаты
        startX += iSpeed*Math.sin(angle);
        startY += iSpeed*Math.cos(angle);
    }

    public void onDraw(Canvas c)
    {
        c.drawBitmap(imgMain, startX, startY, null);
    }
}
