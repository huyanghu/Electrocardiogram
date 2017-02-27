package com.liuqingwei.electrocardiogram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * 图像绘制类
 * Created by Meteoral.Liu On MacOS
 * User: Meteoral
 * WebSite: http://www.liuqingwei.com
 * QQ: 120599662
 */
public class ChartView extends View{

    /** 获取当前页的数据 */
    public static final int GET_THIS_PAGE = 0;
    /** 获取上一页的数据 */
    public static final int GET_PRE_PAGE = 1;
    /** 获取下一页的数据 */
    public static final int GET_NEXT_PAGE = 2;
    /** 控件的宽 */
    private int width;
    /** 控件的高 */
    private int height;
    /** 渲染配置器 */
    private Renderer render;
    /** 控件的Rect */
    private Rect rect;
    /** 控件Context */
    private Context con;
    /** 绘制路径 */
    private Path path;
    /** 滑动事件横向坐标 */
    private float eventX;
    /** 数据集 */
    private float[] drawData;
    /** 数据集中是否存在数据 */
    private boolean hasData;

    public ChartView(Context context) {
        super(context);
        con = context;
        render = new Renderer();
    }
    public ChartView(Context context, AttributeSet attrs){
        super(context,attrs);
        con = context;
        render = new Renderer();
    }
    public ChartView(Context context,AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        con = context;
        render = new Renderer();
    }
    public ChartView(Context context, Renderer renderer) {
        super(context);
        con = context;
        render = renderer;
    }
    public void setRender(Renderer render) {
        this.render = render;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawBackgroud(canvas);
        if(drawData==null) {
            getDrawData(GET_THIS_PAGE);
        }
        if(hasData) {
            DrawChart(canvas);
        }
    }


    /**
     * 绘制心率图的背景
     * @param canvas 画布
     */
    public void DrawBackgroud(Canvas canvas)
    {
        /* 创建背景色画笔 */
        Paint background = new Paint();
        background.setColor(Renderer.BACKGROUND_COLOR);//设置心率图灰色背景
        Paint backP = new Paint();
        backP.setColor(render.getECGAxesColor());
        backP.setAlpha(100);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        rect = canvas.getClipBounds();
        canvas.drawRect(0, 0, width, height, background);
        /* 是否绘制横纵轴 */
        if(render.isECGShowAxes()) {
            /* 心电图每小格40ms
             *  6000/40 = 屏幕横向共150个小格
             *  150个小格平均分布在width宽上
             *  横纵grid的步距 */
            final int gridStep = width / 150;
            /* 绘制竖向的红色grid */
            for (int k = 0; k < width / gridStep; k++) {
                if (k % 5 == 0) {//每隔5个格子粗体显示
                    backP.setStrokeWidth(2);
                    canvas.drawLine(k * gridStep, 0, k * gridStep, height, backP);
                } else {
                    backP.setStrokeWidth(1);
                    canvas.drawLine(k * gridStep, 0, k * gridStep, height, backP);
                }
            }
            /* 绘制横向的红色grid */
            for (int g = 0; g < height / gridStep; g++) {
                if (g % 5 == 0) {
                    backP.setStrokeWidth(2);
                    canvas.drawLine(0, g * gridStep, width, g * gridStep, backP);
                } else {
                    backP.setStrokeWidth(1);
                    canvas.drawLine(0, g * gridStep, width, g * gridStep, backP);
                }
            }
        }
        /* 是否绘制Label标签 */
        if (render.isECGShowLabel()) {
            Paint labelPaint = new Paint();
            labelPaint.setColor(Renderer.TEXT_COLOR);
            labelPaint.setTypeface(render.getECGTextTypeface());
            labelPaint.setTextSize(render.getECGChartTextSize());
            labelPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(render.getECGChartLabel(), width / 2, height - 10, labelPaint);
        }
    }

    /**
     * 绘制曲线
     * @param canvas 画布
     */
    public void DrawChart(Canvas canvas)
    {
        Paint p = new Paint();
        canvas.saveLayerAlpha(new RectF(rect), 0xFF, Canvas.MATRIX_SAVE_FLAG);
        p.setColor(Color.BLACK);
        p.setStrokeWidth(3);

        canvas.drawLines(drawData,p);
    }

    /**
     * 发送请求得到Json类型的数据
     * @param dataType 获取方式(本页、上一页、下一页)
     * Date from https://archive.ics.uci.edu/ml/datasets/MHEALTH+Dataset
     * @return true有数据，false无数据
     */
    public boolean getDrawData(int dataType)
    {
        float[] data = { -0.087912f,-0.10884f,-0.10047f,0.071167f,1.0633f,-0.07954f,-0.63632f,-0.28885f,-0.041863f,0.0083726f,0.083726f,0.10047f,0.16745f,0.23443f,0.3056f,0.35165f,0.427f,0.42282f,0.32234f,0.17582f,0.012559f,-0.050235f,-0.066981f,-0.092098f,-0.092098f,-0.092098f,-0.087912f,-0.07954f,-0.066981f,-0.075353f,-0.083726f,-0.096285f,-0.087912f,-0.087912f,-0.092098f,-0.10466f,-0.087912f,-0.087912f,-0.092098f,-0.087912f,-0.087912f,-0.07954f,-0.066981f,-0.075353f,-0.083726f,-0.096285f,0.020931f,-0.041863f,-0.062794f,-0.075353f,-0.092098f,-0.03349f,0.83307f,0.94192f,-0.61957f,-0.43119f,-0.046049f,0.041863f,0.16327f,0.15908f,0.2428f,0.31397f,0.41026f,0.49398f,0.59027f,0.59027f,0.50235f,0.30141f,0.13396f,0.016745f,-0.037677f,-0.041863f,-0.041863f,-0.03349f,-0.03349f,-0.025118f,-0.037677f,-0.03349f,-0.046049f,-0.025118f,-0.058608f,-0.058608f,-0.062794f,-0.058608f,-0.066981f,-0.062794f,-0.058608f,-0.083726f,-0.071167f,-0.066981f,-0.071167f,-0.071167f,-0.041863f,0f,-0.050235f,-0.07954f,-0.07954f,-0.062794f,0.11722f,0.99215f,0.012559f,-0.71586f,-0.33909f,-0.041863f,0.012559f,0.07954f,0.12977f,0.20094f,0.28885f,0.37258f,0.46886f,0.51073f,0.51073f,0.41026f,0.20931f,0.075353f,-0.016745f,-0.062794f,-0.075353f,-0.075353f,-0.071167f,-0.046049f,0.012559f,-0.058608f,-0.054422f,-0.050235f,-0.062794f,-0.066981f,-0.066981f,-0.066981f,-0.020931f,-0.054422f,-0.071167f,-0.075353f,-0.066981f,-0.075353f,-0.062794f,-0.037677f,-0.050235f,-0.041863f,0.0083726f,-0.029304f,-0.062794f,-0.07954f,-0.046049f,-0.016745f,0.9461f,0.75353f,-0.67818f,-0.41444f,-0.037677f,-0.0041863f,0.058608f,0.12559f,0.18001f,0.27211f,0.38095f,0.44375f,0.51073f,0.49817f,0.41444f,0.26374f,0.13815f,0.029304f,-0.025118f,-0.046049f,-0.058608f,-0.054422f,-0.03349f,-0.050235f,-0.041863f,-0.054422f,-0.062794f,-0.075353f,-0.075353f,-0.10884f,-0.075353f,-0.087912f,-0.087912f,-0.096285f,-0.087912f,-0.087912f,-0.10884f,-0.11303f,-0.10047f,-0.092098f,-0.10884f,-0.0083726f,-0.020931f,-0.07954f,-0.10466f,-0.11303f,-0.07954f,0.15489f,1.0215f,-0.3977f,-0.71167f,-0.32653f,-0.046049f,0.016745f,0.07954f,0.15489f,0.22187f,0.31397f,0.41026f,0.50654f,0.54003f,0.56934f,0.41444f,0.23443f,0.071167f,-0.012559f,-0.046049f,-0.066981f,-0.062794f,-0.062794f,-0.041863f,-0.050235f,-0.066981f,-0.071167f,-0.066981f,-0.096285f,-0.092098f,-0.092098f,-0.083726f,-0.083726f,-0.083726f,-0.07954f,-0.083726f,-0.087912f,-0.087912f,-0.087912f,-0.075353f,-0.066981f,-0.07954f,-0.0083726f,-0.0083726f,-0.07954f,-0.075353f,-0.092098f,-0.075353f,0.071167f,0.98796f,0.012559f,-0.7033f,-0.32653f,-0.041863f,0.020931f,0.087912f,0.12559f,0.19257f,0.25536f,0.3349f,0.43537f,0.44375f,0.46468f,0.3349f,0.17582f,0.03349f,-0.025118f,-0.054422f,-0.092098f,-0.083726f,-0.07954f,-0.037677f,-0.016745f,-0.03349f,-0.029304f,-0.037677f,-0.062794f,-0.041863f,-0.046049f,-0.029304f,-0.03349f,-0.020931f,-0.03349f,-0.012559f,-0.016745f,-0.020931f,-0.029304f,-0.029304f,-0.0083726f,-0.029304f,-0.03349f,-0.041863f,0.071167f,0.0083726f,-0.029304f,-0.046049f,-0.012559f,-0.0083726f,0.7033f,1.4401f,-0.41026f,-0.41863f,-0.12559f,0.012559f,0.071167f,0.1214f,0.1842f,0.27211f,0.34328f,0.43537f,0.51073f,0.5484f,0.53585f,0.38095f,0.20931f,0.071167f,0f,-0.020931f,-0.037677f,-0.03349f,-0.012559f,-0.025118f,-0.016745f,-0.0083726f,-0.037677f,-0.046049f,-0.071167f,-0.071167f,-0.066981f,-0.087912f,-0.087912f,-0.087912f,-0.10047f,-0.087912f,-0.096285f,-0.083726f,-0.07954f,-0.087912f,-0.087912f,0.029304f,-0.025118f,-0.071167f,-0.10884f,-0.10466f,-0.062794f,0.56934f,1.147f,-0.70748f,-0.60283f,-0.16745f,0.029304f,0.075353f,0.16327f,0.22187f,0.30141f,0.40188f,0.49817f,0.59445f,0.59445f,0.52747f,0.36839f,0.14233f,0.012559f,-0.037677f,-0.058608f,-0.062794f,-0.058608f,-0.025118f,-0.029304f,-0.025118f,-0.041863f,-0.037677f,-0.066981f,-0.066981f,-0.075353f,-0.062794f,-0.066981f,-0.087912f,-0.062794f,-0.10466f,-0.07954f,-0.071167f,-0.07954f,-0.066981f,-0.071167f,0.029304f,-0.012559f,-0.083726f,-0.062794f,-0.096285f,-0.066981f,0.28048f,1.0256f,-0.4898f,-0.66143f,-0.23443f,0.012559f,0.075353f,0.096285f,0.17582f,0.23862f,0.31816f,0.38932f,0.48561f,0.51491f,0.51073f,0.35583f,0.1842f,0.058608f,-0.029304f,-0.050235f,-0.041863f,-0.071167f,-0.062794f,-0.071167f,-0.054422f,-0.029304f,-0.062794f,-0.066981f,-0.083726f,-0.10047f,-0.087912f,-0.087912f,-0.062794f,-0.075353f,-0.087912f,-0.07954f,-0.092098f,-0.075353f,-0.062794f,-0.075353f,-0.054422f,0.0083726f,-0.050235f,-0.083726f,-0.10466f,-0.087912f,-0.025118f,0.89168f,0.26792f,-0.73679f,-0.40188f,-0.07954f,-0.012559f,0.046049f,0.11722f,0.15908f,0.23862f,0.32653f,0.3349f,0.38932f,0.38095f,0.32653f,0.18838f,0.066981f,-0.037677f,-0.037677f,-0.058608f,-0.071167f,-0.054422f,-0.03349f,-0.025118f,-0.037677f,-0.041863f,-0.037677f,-0.075353f,-0.083726f,-0.10047f,-0.10466f,-0.096285f,-0.096285f,-0.096285f,-0.07954f,-0.075353f,-0.071167f,-0.071167f,-0.092098f,-0.071167f,-0.016745f,-0.071167f,-0.07954f,-0.087912f,-0.07954f,0.0083726f,1.0424f,0.28048f,-0.56096f,-0.30141f,-0.037677f,0.0083726f,0.046049f,0.07954f,0.13815f,0.18838f,0.2428f,0.26374f,0.31397f,0.31397f,0.24699f,0.14652f,0.07954f,0f,-0.016745f,-0.037677f,-0.050235f,-0.041863f,-0.029304f,-0.046049f,-0.041863f,-0.041863f,-0.066981f,-0.058608f,-0.03349f,-0.071167f,-0.071167f,-0.071167f,-0.066981f,-0.058608f,-0.075353f,-0.07954f,-0.07954f,-0.066981f,-0.083726f,0.029304f,-0.020931f,-0.046049f,-0.083726f,-0.066981f,-0.03349f,0.89587f,1.0005f,-0.61957f,-0.42282f,-0.062794f,0.016745f,0.058608f,0.092098f,0.15908f,0.23862f,0.30979f,0.38095f,0.42282f,0.43537f,0.39351f,0.2428f,0.096285f,0.0041863f,-0.050235f,-0.071167f,-0.041863f,-0.071167f,-0.062794f,-0.071167f,-0.062794f,-0.087912f,-0.07954f,-0.092098f,-0.07954f,-0.10047f,-0.083726f,-0.07954f,-0.092098f,-0.087912f,-0.07954f,-0.075353f,-0.071167f,-0.050235f,0.0083726f,-0.041863f,-0.071167f,-0.092098f,-0.071167f,0.025118f,0.96285f,-0.020931f,-0.71586f,-0.34746f,-0.025118f,0.046049f,0.10466f,0.17582f,0.2763f,0.3056f,0.38514f,0.46468f,0.47305f,0.46049f,0.36002f,0.19676f,0.058608f,-0.025118f,-0.041863f,-0.054422f,-0.054422f,-0.041863f,-0.046049f,-0.037677f,-0.03349f,-0.046049f,-0.071167f,-0.083726f,-0.083726f,-0.087912f,-0.087912f,-0.083726f,-0.087912f,-0.037677f,-0.071167f,-0.071167f,-0.058608f,-0.10466f,0.03349f,-0.012559f,-0.066981f,-0.07954f,-0.087912f,-0.03349f,0.56096f,1.2224f,-0.55259f,-0.54003f,-0.15071f,-0.012559f,0.046049f,0.12559f,0.17582f,0.25955f,0.31816f,0.38932f,0.46049f,0.47305f,0.41863f,0.3056f,0.14233f,0.029304f,-0.016745f,-0.050235f,-0.050235f,-0.058608f,-0.054422f,-0.037677f,-0.046049f,-0.058608f,-0.07954f,-0.071167f,-0.066981f,-0.07954f,-0.058608f,-0.071167f,-0.066981f,-0.075353f,-0.075353f,-0.075353f,-0.071167f,-0.071167f,-0.054422f,-0.062794f,0.012559f,-0.037677f,-0.071167f,-0.075353f,-0.062794f,-0.012559f,0.93354f,0.55678f,-0.69492f,-0.3977f,-0.046049f,0.020931f,0.066981f,0.12977f,0.18838f,0.26792f,0.34746f,0.41026f,0.45631f,0.43537f,0.41863f,0.23862f,0.10047f,0.016745f,-0.03349f,-0.029304f,-0.041863f,-0.016745f,0f,-0.012559f,0.0041863f,-0.0083726f,-0.012559f,-0.016745f,-0.037677f,-0.037677f,-0.029304f,-0.037677f,-0.03349f,-0.03349f,-0.03349f,-0.029304f,-0.046049f,-0.041863f,-0.050235f,-0.03349f,0.037677f,-0.03349f,-0.050235f,-0.025118f,-0.046049f,0f,0.96285f,0.3977f,-0.68655f,-0.3977f,-0.016745f,0.029304f,0.071167f,0.12559f,0.18001f,0.26792f,0.34746f,0.43537f,0.50654f,0.51073f,0.42282f,0.24699f,0.07954f,-0.0041863f,-0.058608f,-0.092098f,-0.092098f,-0.071167f,-0.054422f,-0.050235f,-0.050235f,-0.058608f,-0.058608f,-0.071167f,-0.07954f,-0.07954f,-0.096285f,-0.083726f,-0.075353f,-0.083726f,-0.075353f,-0.075353f,-0.062794f,-0.07954f,-0.0083726f,0.0083726f,-0.058608f,-0.087912f,-0.066981f,-0.075353f,0.1214f,1.0089f,-0.3349f,-0.74935f,-0.29304f,-0.0083726f,0.054422f,0.10047f,0.17582f,0.27211f,0.33072f,0.41444f,0.48561f,0.48561f,0.43119f,0.3349f,0.16327f,0.050235f,-0.020931f,-0.050235f,-0.054422f,-0.058608f,-0.050235f,-0.050235f,-0.058608f,-0.046049f,-0.0041863f,-0.066981f,-0.054422f,-0.087912f,-0.071167f,-0.087912f,-0.087912f,-0.075353f,-0.07954f,-0.058608f,-0.054422f,-0.071167f,-0.062794f,-0.096285f,0f,-0.016745f,-0.071167f,-0.096285f,-0.075353f,-0.062794f,0.2135f,1.0842f,-0.37258f,-0.67399f,-0.24699f,0.012559f,0.071167f,0.11303f,0.17582f,0.23443f,0.33072f,0.44793f,0.50235f,0.57771f,0.56096f,0.42282f,0.21769f,0.050235f,-0.041863f,-0.083726f,-0.10047f,-0.096285f,-0.07954f,-0.083726f,-0.062794f,-0.087912f,-0.07954f,-0.092098f,-0.10466f,-0.10466f,-0.11303f,-0.10884f,-0.11303f,-0.13396f,-0.11722f,-0.10884f,-0.1214f,-0.096285f,-0.11303f,0.016745f,-0.03349f,-0.083726f,-0.10466f,-0.10466f,-0.087912f,0.49817f,1.0675f,-0.75353f,-0.66562f,-0.22606f,-0.025118f,0.041863f,0.07954f,0.17582f,0.27211f,0.34746f,0.45212f,0.52747f,0.58189f,0.5191f,0.31816f,0.12977f,0.029304f,-0.03349f,-0.054422f,-0.092098f,-0.062794f,-0.050235f,-0.050235f,-0.016745f,-0.03349f,-0.03349f,-0.054422f,-0.046049f,-0.041863f,-0.03349f,-0.037677f,-0.03349f,-0.046049f,-0.050235f,-0.041863f,-0.041863f,-0.03349f,-0.020931f,-0.020931f,0.0041863f,-0.025118f,0.087912f,0.020931f,0f,-0.029304f,-0.012559f,0.029304f,0.84563f,1.3187f,-0.60283f,-0.44375f,-0.025118f,0.092098f,0.13396f,0.19257f,0.24699f,0.34746f,0.43956f,0.56934f,0.60701f,0.62794f,0.59027f,0.3977f,0.20931f,0.083726f,0.0083726f,-0.016745f,-0.0083726f,-0.012559f,0f,0.0041863f,0.0041863f,-0.0083726f,-0.0083726f,0.0041863f,-0.029304f,-0.03349f,-0.03349f,-0.050235f,-0.050235f,-0.046049f,-0.046049f,-0.054422f,-0.058608f,-0.046049f,-0.037677f,-0.037677f,-0.054422f,0.046049f,-0.020931f,-0.046049f,-0.058608f,-0.050235f,-0.025118f,0.74097f,1.1219f,-0.72004f,-0.52747f,-0.050235f,0.03349f,0.087912f,0.16327f,0.22606f,0.29723f,0.40188f,0.48142f,0.55259f,0.56096f,0.47305f,0.29723f,0.10884f,-0.012559f,-0.054422f,-0.087912f,-0.096285f,-0.083726f,-0.092098f,-0.046049f,-0.058608f,-0.058608f,-0.041863f,-0.087912f,-0.083726f,-0.087912f,-0.083726f,-0.087912f,-0.087912f,-0.087912f,-0.083726f,-0.096285f,-0.083726f,-0.10466f,-0.096285f,-0.096285f,-0.10047f,-0.096285f,-0.075353f,-0.050235f,-0.07954f,0.046049f,0f,-0.041863f,-0.075353f,-0.058608f,-0.046049f,0.47305f,1.1722f,-0.52329f,-0.50654f,-0.13815f,0.025118f,0.10466f,0.11722f,0.18001f,0.2428f,0.3056f,0.3977f,0.46468f,0.4898f,0.46886f,0.35583f,0.20513f,0.087912f,0.016745f,-0.020931f,-0.03349f,-0.041863f,-0.016745f,-0.025118f,-0.0041863f,0.03349f,-0.0083726f,-0.0083726f,-0.012559f,-0.020931f,-0.029304f,-0.03349f,-0.041863f,-0.020931f,-0.029304f,-0.016745f,-0.012559f,-0.025118f,-0.025118f,-0.0041863f,-0.0041863f };
        for(int i = 0;i<data.length;i++){
            data[i] = data[i]*200f;
        }
        hasData = true;
        int step = 1;//步进值，默认为3，值越大步进越多，绘制越快，精度越低
        final int counts = (data.length / width) * step;//每个横向像素点所需步进的纵向值
        final int halfHeight = height / 2;
        drawData = new float[(width / step) * 4];
        for (int i = 0; i < width / step; i++) {
            drawData[i * 4] = i * step;
            drawData[i * 4 + 1] = -(data[i * counts] * 0.5f) + halfHeight;
            drawData[i * 4 + 2] = (i + 1) * step;
            drawData[i * 4 + 3] = -(data[(i + 1) * counts] * 0.5f) + halfHeight;
        }
//            switch (dataType)
//            {
//                case GET_THIS_PAGE:{
//                    new PostRequest().execute(GET_THIS_PAGE);
//                }
//                break;
//                case GET_PRE_PAGE:{
//                    new PostRequest().execute(GET_PRE_PAGE);
//                }
//                break;
//                case GET_NEXT_PAGE:{
//                    new PostRequest().execute(GET_NEXT_PAGE);
//                }
//                break;
//                default:
//                    new PostRequest().execute(GET_THIS_PAGE);
//            }

        return hasData;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(render.isECGScrollable()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    eventX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    if (eventX - event.getX() > width / 4) {
                        getDrawData(GET_PRE_PAGE);
                        if (hasData) {
                            Toast.makeText(con, "上一页", Toast.LENGTH_SHORT).show();
                        } else {
//                            setPage(getPage() + 1);
                        }
                    } else if (event.getX() - eventX > width / 4) {
                        getDrawData(GET_NEXT_PAGE);
                        if (hasData) {
                            Toast.makeText(con, "下一页", Toast.LENGTH_SHORT).show();
                        } else {
//                            setPage(getPage() - 1);
                        }
                    }
                    break;
            }
        }
        return true;
    }

    /*class PostRequest extends AsyncTask<Integer, String, String> {

        *//**
         * 无参数构造发送请求函数
         * 默认构造url为http://www.bit-health.com/ecg/drawEcg.php
         * 默认起始页第0页
         *//*
        public PostRequest(){
        }

        public PostRequest(String url,String eventId,Integer page)
        {
            _eventId = eventId;
            _page = page;
            _url = url;
        }

        @Override
        protected String doInBackground(Integer... type) {
            String responseString = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            try {
                String urlParm = null;
                if(type[0]==GET_THIS_PAGE) {
                    urlParm = "?type="+_page+"&EventId="+_eventId;
                }
                else if(type[0]==GET_NEXT_PAGE) {
                    _page++;
                    urlParm = "?type="+_page+"&EventId="+_eventId;
                }
                else if(type[0]==GET_PRE_PAGE) {
                    _page--;
                    if(_page<0) _page=0;//页数不能小于0
                    urlParm = "?type="+_page+"&EventId="+_eventId;
                }
                response = httpclient.execute(new HttpGet(getUrl()+urlParm));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject obj = new JSONObject(result);
                if (0 == obj.getInt("flag") || 1 == obj.getInt("flag"))//得到的回复中含有数据
                {
                    JSONArray data = obj.getJSONArray("data");//得到的心电图数据
                    hasData = true;
                    int step = render.getSiatLineStep();//步进值，默认为3，值越大步进越多，绘制越快，精度越低
                    final int counts = (data.length() / width) * step;//每个横向像素点所需步进的纵向值
                    final int halfHeight = height / 2;
                    drawData = new float[(width / step) * 4];
                    for (int i = 0; i < width / step; i++) {
                        drawData[i * 4] = i * step;
                        drawData[i * 4 + 1] = (-data.getInt(i * counts) * 0.5f) + halfHeight;
                        drawData[i * 4 + 2] = (i + 1) * step;
                        drawData[i * 4 + 3] = (-data.getInt((i + 1) * counts) * 0.5f) + halfHeight;
                    }
                } else if (2 == obj.getInt("flag"))//没有数据了
                {
                    hasData = false;
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            invalidate();
        }
    }*/

}
