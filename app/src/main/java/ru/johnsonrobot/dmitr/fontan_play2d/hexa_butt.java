package ru.johnsonrobot.dmitr.fontan_play2d;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by user on 09.02.17.
 */

public class hexa_butt
{
    private float sY,sX;
    private Bitmap imgMain;
    private Bitmap[] imgState;
    private hexa_part[] hexblow;
    private  Resources r;
    private int stepblowpiece ; //определяем как долго разлетаются осколки
    private int blowcntdwn;
    public boolean isBlow ;
    private Matrix mat;
    private Paint paint;
    private int blowX, blowY;

    public hexa_butt(Resources res, int idBitmap1, int idBitmap2,  float X, float Y)
    {
        imgState = new Bitmap[2];
        imgState[0] = BitmapFactory.decodeResource(res, idBitmap1);
        imgState[1] = BitmapFactory.decodeResource(res, idBitmap2);
        imgMain = imgState[0];
        sX = X;
        sY = Y;
        hexblow = new hexa_part[6];
        r = res;
        blowcntdwn = 50;
        stepblowpiece = 0;
        isBlow = false;
        mat = new Matrix();
        paint  = new Paint();
        mat.postScale(1.0f, 1.0f);
        mat.postTranslate(X,Y);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        blowX = (int)sX + (imgMain.getHeight()/2 - 61); //61 это hex heigth/2
        blowY = (int)sY + (imgMain.getWidth()/2 - 60);
        createNewPiece(blowX,blowY);

    }

    public hexa_butt(Resources res, int idBitmap1, int idBitmap2, float X, float Y, int blwcnt)
    {
        imgState = new Bitmap[2];
        imgState[0] = BitmapFactory.decodeResource(res, idBitmap1);
        imgState[1] = BitmapFactory.decodeResource(res, idBitmap2);
        imgMain = imgState[0];
        sX = X;
        sY = Y;
        hexblow = new hexa_part[6];
        r = res;
        blowcntdwn = blwcnt;
        stepblowpiece = 0;
        isBlow = false;
        mat = new Matrix();
        paint  = new Paint();
        mat.postScale(1.0f, 1.0f);
        mat.postTranslate(X,Y);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        blowX = (int)sX + (imgMain.getHeight()/2 - 61); //61 это hex heigth/2
        blowY = (int)sY + (imgMain.getWidth()/2 - 60);
        createNewPiece(blowX,blowY);

    }




    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (hexblow != null) hexblow = null;
    }

    private void createNewPiece(int cX, int cY)
    {
        hexblow = new hexa_part[6];//создаем куски гексагоны для моделирования взрыва
        hexblow[0]= new hexa_part(r, R.drawable.seastar_s, cX, cY , (Math.PI/180)*45, 25);
        hexblow[1]= new hexa_part(r, R.drawable.seastar_s, cX, cY , (Math.PI/180)*90, 25);
        hexblow[2]= new hexa_part(r, R.drawable.seastar_s, cX, cY , (Math.PI/180)*135, 25);
        hexblow[3]= new hexa_part(r, R.drawable.seastar_s, cX, cY , (Math.PI/180)*225, 25);
        hexblow[4]= new hexa_part(r, R.drawable.seastar_s, cX, cY , (Math.PI/180)*270, 25);
        hexblow[5] = new hexa_part(r, R.drawable.seastar_s, cX, cY , (Math.PI/180)*315, 25);

    }

    public boolean isBlowing(){ return isBlow; }

    public boolean isInBound(int X, int Y)
    {
        boolean touchdown = false;
        int h = imgMain.getHeight();
        int w = imgMain.getWidth();
        if ((X>sX) && (X<sX+w) && (Y>sY) && (Y<sY+h)) touchdown = true;
        return touchdown;
    }

    private void drawInfonum(Rect r, int num, Canvas c)
    {
        Paint fPnt = new Paint(Paint.ANTI_ALIAS_FLAG);
        fPnt.setColor(Color.GREEN);
        fPnt.setTextSize(30.0f);


        String sMess = String.format(" БАБААХХ %d", num);

        int x = r.left+r.width();
        int y = r.top;

        //x = x + r.width();
        //y = y + r.width();

        c.drawText(sMess, (float)x, (float) y, fPnt);

    }


    public  void onDraw(Canvas c)
    {

        if (isBlow && (stepblowpiece < blowcntdwn))
        {//взрыв начало
            for (int i = 0; i<6;i++)
            {
                hexblow[i].onDraw(c);
                hexblow[i].onUpdate();

                //drawInfonum(hexblow[i].getImageRectangle(), i, c);

            }
            stepblowpiece++;
        }
        if (stepblowpiece >= blowcntdwn)
        {
            isBlow = false;
            stepblowpiece = 0;
            hexblow = null;
            createNewPiece(blowX, blowY);
        }

        if (isBlow)
           // mat.preRotate(2.0f, imgMain.getWidth() / 2, imgMain.getHeight() / 2);
           imgMain = imgState[1];
        else
           imgMain = imgState[0];
        c.drawBitmap(imgMain, mat, paint );
    }


}
