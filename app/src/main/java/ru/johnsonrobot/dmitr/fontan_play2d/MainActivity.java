package ru.johnsonrobot.dmitr.fontan_play2d;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

//=============== INNER CLASS




public class MainActivity extends Activity {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float initX, initY, radius;
    private boolean drawing = false;
    private int cntDown;
    private int sceneN;  //номер отображаемой сцены
    private int idleCnt; //время в простое, после которого надо сменить сцену на 1-ю
    private static final int IDLE = 500;
    private int blowX, blowY;


    public class MySurfaceThread extends Thread {
        private SurfaceHolder myThreadSurfaceHolder;
        private MySurfaceView myThreadSurfacewView;
        private boolean myThreadRun = false;

        public MySurfaceThread(SurfaceHolder surfaceHolder, MySurfaceView surfaceView)
        {
            myThreadSurfaceHolder = surfaceHolder;
            myThreadSurfacewView = surfaceView;

        }

        public void setRunning(boolean b)
        {
            myThreadRun = b;
        }

        @Override
        public void run() {
            //super.run();

            while(myThreadRun)
            {
                Canvas c = null;
                try {
                    c = myThreadSurfaceHolder.lockCanvas(null);
                    synchronized (myThreadSurfaceHolder) {
                        //myThreadSurfacewView.draw(c);
                        myThreadSurfacewView.onDraw(c);
                    }
                }
                finally {
                     if ( c!= null) myThreadSurfaceHolder.unlockCanvasAndPost(c);
                }
            }

        }
    }





    public  class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

        private MySurfaceThread thread;
        private Bitmap hexBitmap; //главная заставка
        private long prevTime;
        private Matrix matrix;
        private float axisYRot;
        private dolphi[] smallFish;
        private hexa_butt[] hexButt;
        private hexa_butt mainButt;
        private Random rnd;
        private int idxLucky;
        private boolean canChangeScene;



        private void drawDebugInfo( Canvas c, String mess)
        {
            Paint fPnt = new Paint(Paint.ANTI_ALIAS_FLAG);
            fPnt.setColor(Color.WHITE);
            fPnt.setTextSize(20.0f);


            String sMess = mess;//String.format("%d", num);

            int x = c.getWidth() - 200;
            int y = c.getHeight();

            //x = x - 33;
            //y = y + r.width();

            c.drawText(sMess, (float)x, (float) y, fPnt);

        }

        private void drawCountDown( Canvas c, int cnt)
        {
            Paint fPnt = new Paint(Paint.ANTI_ALIAS_FLAG);
            fPnt.setColor(Color.WHITE);
            fPnt.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            fPnt.setTextSize(120.0f);


            String sMess = String.format("%d", cnt);

            int w = c.getWidth();
            int h = c.getHeight();

            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.GRAY);
            rectPaint.setAlpha(80);
            c.drawRect((w/2-60)-60,((h*3)/4)-30, w/2+120, ((h*3)/4)+30, rectPaint);
            c.drawText(sMess, w/2 - 60, (h*3)/4, fPnt);

        }

        private void drawScene1(Canvas c, int xLeft, int yTop)
        {
            //первая сцена. основная заставка игры
            Rect mBox = new Rect(xLeft, yTop, c.getWidth(), c.getHeight());
            Paint mBoxPaint = new Paint();

            mBoxPaint.setColor(Color.BLUE);
            mBoxPaint.setAlpha(95);

            //c.drawRect(mBox, mBoxPaint);
            c.drawRect((float) xLeft, (float)yTop, c.getWidth(), c.getHeight(), mBoxPaint);

            mainButt.onDraw(c);
            long now = System.currentTimeMillis();
            long elapseedTime = now - prevTime;
            if (elapseedTime > 24.0f) {
                prevTime = now;

                if (mainButt.isBlow ) {
                    cntDown++;

                } else {
                    cntDown = 0;
                    canChangeScene = true;
                    //idxLucky = rnd.nextInt(5);
                }
            }
        }



        @Override
        protected void onDraw(Canvas canvas) {
            //super.onDraw(canvas);
            //Главная процедура отрисовки всей игры
            //Здесь должны быть размещены все сцены
            /*
            Алгоритм работы:
              отрисовываем n-ю сцену
              отрисовываем n-1 сцену

                  * * *

               отрисовываем 1 сцену

               сцены оформлены в виде процедур

                 1-я процедура - экран приглашения
                 2-я процедура - основной экран


             */

            //подготовка экрана к отрисовке
            canvas.drawColor(Color.BLACK);
            //canvas.drawBitmap(hexBitmap, 0,0,null);
            //рисуем фон
            Rect src = new Rect(0,0,hexBitmap.getWidth(), hexBitmap.getHeight());
            Rect dst = new Rect(0,0, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(hexBitmap, src, dst, null);

            if (sceneN ==2) {
                //==============================================================
                //основная игровая сцена
                //==============================================================
                //рисуем рыбок в аквариуме
                for (int fcnt = 0; fcnt < 3; fcnt++) {
                    smallFish[fcnt].update(canvas);
                    smallFish[fcnt].onDraw(canvas);
                }
                hexButt[0].onDraw(canvas);
                hexButt[1].onDraw(canvas);
                hexButt[2].onDraw(canvas);
                hexButt[3].onDraw(canvas);
                hexButt[4].onDraw(canvas);

                long now = System.currentTimeMillis();
                long elapseedTime = now - prevTime;
                if (elapseedTime > 24.0f) {

                    prevTime = now;
                    String dbgMess = String.format("idelCnt = %d", idleCnt);
                    drawDebugInfo(canvas, dbgMess);
                    idleCnt++;
                    if (idleCnt >= IDLE) {idleCnt = 0; sceneN = 1; mainButt.isBlow = false;} //если долго никто не нажимал на кнопку, то перейти на заставку

                    if (hexButt[0].isBlow || hexButt[1].isBlow || hexButt[2].isBlow || hexButt[3].isBlow || hexButt[4].isBlow) {
                        cntDown++;
                        drawCountDown(canvas, cntDown);
                    } else {
                        cntDown = 0;
                        idxLucky = rnd.nextInt(5);
                    }
                }
            }

            if ( sceneN == 1)
            {
                drawScene1(canvas, 0,0);
                if (mainButt.isBlow) {sceneN = 2; canChangeScene = false;}

            }
            //================================================================
                /*if (isBlow && (stepblowpiece < blowcntdwn))
                {//взрыв начало
                    for (int i = 0; i<6;i++)
                    {
                        hexblow[i].onDraw(canvas);
                        hexblow[i].onUpdate();

                        drawInfonum(hexblow[i].getImageRectangle(), i, canvas);

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

                prevTime = now;
                if (isBlow)
                    matrix.preRotate(2.0f, hexBitmap.getWidth() / 2, hexBitmap.getHeight() / 2);
                //else
                    //matrix.setRotate(0f, hexBitmap.getWidth() / 2, hexBitmap.getHeight() / 2);
                //axisYRot = - axisYRot;
                //matrix.preScale(1.0f, axisYRot);
                canvas.drawBitmap(hexBitmap, matrix, paint);


            }
            canvas.drawBitmap(hexBitmap, matrix, paint);
            */


           // if (drawing) {
               // canvas.drawCircle(initX, initY, radius, paint);

            //}

        }

        //лишний метод
        @Override
        public void draw(Canvas canvas) {
            //super.draw(canvas);
            if (drawing) canvas.drawCircle(initX, initY, radius, paint);
        }



        @Override
        public boolean onTouchEvent(MotionEvent event) {
            //return super.onTouchEvent(event);
            int action = event.getAction();
            if (MotionEvent.ACTION_MOVE == action)
            {
                float x = event.getX();//осталось от примера, нужно убрать !
                float y = event.getY();
                radius = (float) Math.sqrt(Math.pow(x-initX,2) + Math.pow(y - initY,2));
            }
            else if (MotionEvent.ACTION_DOWN == action)
            {
                initX = event.getX();
                initY = event.getY();
                radius = 1;
                drawing = true;
                idleCnt = 0;
                //добавить проверку на нажатие пальцем в нужную область экрана
                //попадаем ли в кнопку или нет
                //кроме того проверяем, была ли уже нажата какая либо кнопка
                if (sceneN == 1)
                {
                    if (mainButt.isInBound((int) initX, (int) initY) && cntDown == 0) mainButt.isBlow = true;
                }
                if (sceneN == 2) {
                    mainButt.isBlow = false;
                    boolean sumCondition = hexButt[0].isBlow & hexButt[1].isBlow & hexButt[2].isBlow & hexButt[3].isBlow & hexButt[4].isBlow;
                    if (hexButt[0].isInBound((int) initX, (int) initY) && (cntDown == 0) && (idxLucky == 0))
                        hexButt[0].isBlow = true;
                    if (hexButt[1].isInBound((int) initX, (int) initY) && (cntDown == 0) && (idxLucky == 1))
                        hexButt[1].isBlow = true;
                    if (hexButt[2].isInBound((int) initX, (int) initY) && (cntDown == 0) && (idxLucky == 2))
                        hexButt[2].isBlow = true;
                    if (hexButt[3].isInBound((int) initX, (int) initY) && (cntDown == 0) && (idxLucky == 3))
                        hexButt[3].isBlow = true;
                    if (hexButt[4].isInBound((int) initX, (int) initY) && (cntDown == 0) && (idxLucky == 4))
                        hexButt[4].isBlow = true;
                }
            } //данная логика была нужна только для тестового примера.
            else if (MotionEvent.ACTION_UP == action)
            {
                drawing = false;
            }

            return true;
        }

        //набор конструкторов
        public MySurfaceView(Context context) {
            super(context);

            init();
        }

        public MySurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);

            init();
        }

        public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            init();
        }


        private void init()
        {
            getHolder().addCallback(this);
            //Необходимо убрать ================================
            //hexBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hextool_e);
            //blowX = 200 + (hexBitmap.getHeight()/2 - 61); //61 это hex heigth/2
            //blowY = 200 + (hexBitmap.getWidth()/2 - 60);
            //createNewPiece(blowX, blowY);
            //stepblowpiece = 0; //первоначальный шаг во взрыве
            //axisYRot = 1.0f;
            //matrix = new Matrix();
            //matrix.postScale(1.0f, axisYRot);
            //matrix.postTranslate(200.0f, 200.0f);

            //paint.setStyle(Paint.Style.STROKE);
            //paint.setStrokeWidth(3);

            //paint.setColor(Color.WHITE);
            //=======================================================
            int glbW = this.getMeasuredWidth();
            int glbH = this.getMeasuredHeight();

            hexBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zastvka);
            hexButt = new hexa_butt[5];

            hexButt[0] = new hexa_butt(getResources(), R.drawable.closeshell, R.drawable.openshell_b, 100, 100);
            hexButt[1] = new hexa_butt(getResources(), R.drawable.closeshell, R.drawable.openshell_b, 400, 400);
            hexButt[2] = new hexa_butt(getResources(), R.drawable.closeshell, R.drawable.openshell_b, 700, 100);
            hexButt[3] = new hexa_butt(getResources(), R.drawable.closeshell, R.drawable.openshell_b,1100, 400);
            hexButt[4] = new hexa_butt(getResources(), R.drawable.closeshell, R.drawable.openshell_b,1400, 100);

            smallFish = new dolphi[3];
            smallFish[0] = new dolphi(getResources(),R.drawable.obitatli_morea_s, 20, 20);
            smallFish[1] = new dolphi(getResources(),R.drawable.fish1, 20, 300);
            smallFish[2] = new dolphi(getResources(),R.drawable.fish1, 600, 20);

            int mX = 1920/2- 307;
            int mY = 1080/2 - 54;
            mainButt = new hexa_butt(getResources(), R.drawable.but12player2d, R.drawable.but11player2d, mX, mY, 10);

            sceneN = 1; //номер отображаемой сцены на экране
            cntDown = 0;
            canChangeScene = false;
            idleCnt = 0;
            //запускаем генератор случайных чисел
            rnd = new Random();
            idxLucky = rnd.nextInt(5);
            thread = new MySurfaceThread(getHolder(), this);
            setFocusable(true);


        }

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
        {

        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            thread.setRunning(true);
            thread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            boolean retry = true;

            thread.setRunning(false);
            while (retry)
            {
                try {
                    thread.join();
                    retry = false;
                }
                catch (InterruptedException e)
                {
                    Log.i("DEBUG", e.getMessage());

                }
            }

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);

        MySurfaceView waterSurface = new MySurfaceView(this);
        setContentView(waterSurface);
    }
}
