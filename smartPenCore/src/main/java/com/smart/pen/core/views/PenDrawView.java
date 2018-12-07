package com.smart.pen.core.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import com.smart.pen.core.views.MultipleCanvasView.PenModel;


/**
 * 笔书写view
 *
 * @author Xiaoz
 * @date 2015年10月15日 下午2:38:17
 * <p>
 * Description
 */
public class PenDrawView extends View {
    private Path mPath = new Path();
    private Bitmap mBitmap;
    private Canvas mCanvas; // canvas
    private PenModel mPenModel = PenModel.None;
    private int mPenWeight = 1;
    private int mDownMoveNum = 0;    // Number of pen movements

    private int mLastX;//The coordinates of the last recorded point
    private int mLastY;

    public PenDrawView(Context context) {
        super(context);

    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) return;

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    /**
     * 根据坐标绘制
     *
     * @param x
     * @param y
     * @param isRoute Is it writing?
     */
    public void drawLine(int x, int y, boolean isRoute, Paint paint) {
        // Is it ready to write whether the tip is in contact?
        if (isRoute) {
            // Is it move?
            if (mLastX != 0 && mLastY != 0) {
                double speed = Math.sqrt(Math.pow(mLastX - x, 2) + Math.pow(mLastY - y, 2));
                // Pressing the moving distance is greater than a certain distance to start calculating the weight
                if (mDownMoveNum > 3 * mPenWeight && mPenModel != PenModel.None) {
                    // Calculate handwriting thickness/fine according to speed
                    int fix = (int) (speed / 10);
                    float weight = mPenWeight - fix;

                    // If the distance is less than the calculated weight, then it will not be processed
                    if (speed <= weight) return;
                    if (weight < 1) weight = 1;
                    paint.setStrokeWidth(weight);
                } else if (speed <= mPenWeight) {
                    // If the distance is less than weight, then it will not be processed
                    return;
                }

                if (mPenModel == PenModel.Pen) {
                    mCanvas.drawLine(mLastX, mLastY, x, y, paint);
                } else {
                    mPath.quadTo(mLastX, mLastY, (mLastX + x) / 2, (mLastY + y) / 2);
                    mCanvas.drawPath(mPath, paint);
                }
                mDownMoveNum++;
                invalidate();
            } else {
                mDownMoveNum = 0;
                paint.setStrokeWidth(mPenWeight);

                if (mPenModel == PenModel.Pen) {
                    mCanvas.drawPoint(x, y, paint);
                } else {
                    mPath.reset();
                    mPath.moveTo(x, y);
                    mCanvas.drawPath(mPath, paint);
                }
            }

            mLastX = x;
            mLastY = y;
        } else {
            mPath.reset();
            // Not writing
            mLastX = 0;
            mLastY = 0;
        }
    }

    public void init(int width, int height) {
        if (mBitmap != null && !mBitmap.isRecycled()) mBitmap.recycle();
        this.mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        this.mCanvas = new Canvas();
        this.mCanvas.setBitmap(mBitmap);
    }

    public void drawBitmap(Bitmap bitmap) {
        this.mCanvas.drawBitmap(bitmap, 0, 0, null);
    }

    /**
     * Set pen mode
     *
     * @param model
     */
    public void setPenModel(PenModel model) {
        this.mPenModel = model;
    }

    /**
     * Set the stroke width
     *
     * @param weight
     */
    public void setPenWeight(int weight) {
        this.mPenWeight = weight;
    }
}
