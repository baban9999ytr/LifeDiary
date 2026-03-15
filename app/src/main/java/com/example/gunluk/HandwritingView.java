package com.example.gunluk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HandwritingView extends View {

    private static class Stroke {
        Path path;
        int color;
        float width;

        Stroke(Path path, int color, float width) {
            this.path = path;
            this.color = color;
            this.width = width;
        }
    }

    private final List<Stroke> strokes = new ArrayList<>();
    private Stroke currentStroke;
    
    private Paint paint;
    private int currentColor = Color.BLACK;
    private float currentWidth = 8f;
    private boolean isEraser = false;

    // Background color of the canvas (used when exporting)
    private final int backgroundColor = Color.parseColor("#FDFBF7"); // Cream color to match app

    private float lastX, lastY;
    private static final float TOUCH_TOLERANCE = 4f;

    public HandwritingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }

    private void setupPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setPenColor(int color) {
        currentColor = color;
        isEraser = false;
    }

    public void setPenWidth(float width) {
        currentWidth = width;
    }

    public void setEraser(boolean eraser) {
        isEraser = eraser;
    }

    public void undo() {
        if (!strokes.isEmpty()) {
            strokes.remove(strokes.size() - 1);
            invalidate();
        }
    }

    public void clear() {
        strokes.clear();
        invalidate();
    }

    public boolean isEmpty() {
        return strokes.isEmpty() && currentStroke == null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw all finished strokes
        for (Stroke stroke : strokes) {
            paint.setColor(stroke.color);
            paint.setStrokeWidth(stroke.width);
            canvas.drawPath(stroke.path, paint);
        }

        // Draw current stroke
        if (currentStroke != null) {
            paint.setColor(currentStroke.color);
            paint.setStrokeWidth(currentStroke.width);
            canvas.drawPath(currentStroke.path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    private void touchStart(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        int color = isEraser ? backgroundColor : currentColor;
        float width = isEraser ? currentWidth * 4 : currentWidth; // Eraser is thicker
        currentStroke = new Stroke(path, color, width);
        lastX = x;
        lastY = y;
    }

    private void touchMove(float x, float y) {
        if (currentStroke == null) return;
        
        float dx = Math.abs(x - lastX);
        float dy = Math.abs(y - lastY);
        
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // Quadratic bezier for smooth curves
            currentStroke.path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
            lastX = x;
            lastY = y;
        }
    }

    private void touchUp() {
        if (currentStroke != null) {
            currentStroke.path.lineTo(lastX, lastY);
            strokes.add(currentStroke);
            currentStroke = null;
        }
    }

    /**
     * Exports the current drawing to a Bitmap.
     */
    public Bitmap exportBitmap() {
        if (getWidth() == 0 || getHeight() == 0) return null;
        
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);
        draw(canvas);
        return bitmap;
    }
}
