package propulsar.qroo.PresentationLayer.Graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import propulsar.qroo.R;

/**
 * Created by mbocanegra on 20/04/16.
 */
public class PieChart extends View {
    /*
    * public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        thisView=this;
    }*/
    int padding=40;
    View thisView;

    boolean hasInit=false;
    float contPathLengths[];
    Path[] guidePaths;
    Path[] paintPaths;
    PathMeasure[] guidePathMeasures;
    Paint[] paints;
    float globalContPathLen=0.0f;
    int notPainting=0;

    int[] vals={25,75};
    int[] colors={R.color.buttonRed, R.color.colorAccent};

    public  PieChart(Context context, AttributeSet attrs){
        super(context, attrs);
        thisView = this;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("debug","threadStoppedNow");
        viewHandler.post(updateView);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas){

        if(hasInit) {
            for(int i=0; i<paintPaths.length; i++){
                //if(contPathLengths[i]<guidePathMeasures[i].getLength())
                //canvas.drawPath(guidePaths[i], paints[i]);
                canvas.drawPath(paintPaths[i],paints[i]);
                /*
                else{
                    Log.d("debug","notPainting["+i+"]");
                    notPainting++;
                }
                */
            }
        }
    }

    //Hanlder e hilo que se encargan de animar el path
    Handler viewHandler = new Handler();
    Runnable updateView = new Runnable(){
        @Override
        public void run(){
            thisView.invalidate();
            updateState();
            if(notPainting<=vals.length) {
                viewHandler.postDelayed(updateView, 0);
            }else{
                Log.d("debug","threadStopped");
            }
        }
    };

    private void init(){
        RectF oval = new RectF();
        oval.set(0 + padding, 0 + padding, getWidth() - padding, getWidth() - padding);

        paints=new Paint[vals.length];
        contPathLengths=new float[vals.length];
        guidePaths = new Path[vals.length];
        guidePathMeasures = new PathMeasure[vals.length];
        paintPaths = new Path[vals.length];
        for(int i=0; i<vals.length; i++){
            contPathLengths[i]=0.0f;
            guidePaths[i]=new Path();
            guidePaths[i].arcTo(oval,lastPositionInDegree(i),percentageToDegree(vals[i]));

            Log.d("", "lastP=" + lastPositionInDegree(i) + " perc=" + percentageToDegree(vals[i]));

            guidePathMeasures[i] = new PathMeasure(guidePaths[i],false);

            paintPaths[i]=new Path();

            paints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paints[i].setStyle(Paint.Style.STROKE);
            paints[i].setColor(ContextCompat.getColor(getContext(), colors[i]));
            paints[i].setStrokeWidth(10);
            paints[i].setStyle(Paint.Style.STROKE);
        }
        /*
        guidePath = new Path();
        guidePath.arcTo(oval, -90, 90, false);
        guidePathMeasure = new PathMeasure(guidePath,false);

        paintPath = new Path();
        */

        hasInit=true;
    }

    private void updateState(){

        if(!hasInit)
            init();

        for(int i=0; i<vals.length; i++){
            contPathLengths[i]+=7.0f;

            if(contPathLengths[i]<=guidePathMeasures[i].getLength()) {
                float[] pos = new float[]{0.0f, 0.0f};
                paintPaths[i].moveTo(getWidth() / 2, getHeight() / 2);
                guidePathMeasures[i].getPosTan(contPathLengths[i], pos, null);
                paintPaths[i].lineTo(pos[0], pos[1]);
            }
        }
        //Log.d("debug","stillUpdating");

        /*
        if(contPathLen==0.0f){
            init();
        }

        float[] pos = new float[]{0.0f,0.0f};
        paintPath.moveTo(getWidth() / 2, getHeight() / 2);
        guidePathMeasure.getPosTan(contPathLen, pos, null);
        paintPath.lineTo(pos[0], pos[1]);

        contPathLen+=5.0f;
        */
    }

    // ---------------------------------
    private int percentageToDegree(int per){
        return (per*36)/10;
    }

    private int lastPositionInDegree(int pos){
        int posInDeg=-90;
        for(int i=0; i<pos; i++){
            posInDeg+=percentageToDegree(vals[i]);
        }
        return posInDeg;
    }

    private float getMaxPathMeasureLength(PathMeasure[] paths){
        float max = -9999.0f;
        for(int i=0; i<paths.length; i++){
            if(paths[i].getLength()>=max){max=paths[i].getLength();}
        }
        return max;
    }

    /*
    @Override
    protected void onDraw(Canvas canvas){
        Paint paints = new Paint(Paint.ANTI_ALIAS_FLAG);
        paints.setStyle(Paint.Style.STROKE);
        paints.setColor(ContextCompat.getColor(getContext(), R.color.Cyan500));
        paints.setStrokeWidth(5);
        paints.setShadowLayer(5, 0, 10, 0x80000000);
        paints.setStyle(Paint.Style.STROKE);
        //getPosTan
        //path.lineTo();
        //path.arcTo(oval, 0, 90);

        if(hasInit) {
            canvas.drawPath(paintPath, paints);
        }
    }

    //Hanlder e hilo que se encargan de animar el path
    Handler viewHandler = new Handler();
    Runnable updateView = new Runnable(){
        @Override
        public void run(){
            thisView.invalidate();
            updateState();
            if(contPathLen<guidePathMeasure.getLength()) {
                viewHandler.postDelayed(updateView, 15l);
            }else{
                Log.d("debug","threadStopped!");
            }
        }
    };

    private void init(){
        RectF oval = new RectF();
        oval.set(0 + padding, 0 + padding, getWidth() - padding, getWidth() - padding);

        guidePath = new Path();
        guidePath.arcTo(oval, -90, 90, false);
        guidePathMeasure = new PathMeasure(guidePath,false);

        paintPath = new Path();

        hasInit=true;
    }

    private void updateState(){

        //Log.d("debug","stillUpdating");

        if(contPathLen==0.0f){
            init();
        }

        float[] pos = new float[]{0.0f,0.0f};
        paintPath.moveTo(getWidth() / 2, getHeight() / 2);
        guidePathMeasure.getPosTan(contPathLen, pos, null);
        paintPath.lineTo(pos[0], pos[1]);

        contPathLen+=5.0f;
    }
    */
}