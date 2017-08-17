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
import android.widget.LinearLayout;
import android.widget.TextView;

import propulsar.qroo.R;

/**
 * Created by mbocanegra on 20/04/16.
 */
public class PerceCard extends View {
    /*
    * public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        thisView=this;
    }*/
    int padding=40;
    View thisView;

    boolean hasInit=false;
    float contPathLengths[];
    float contCenterPathLength;
    Path[] guidePaths;
    Path centerGuidePath;
    PathMeasure centerGuideMeasure;
    Path[] paintPaths;
    PathMeasure[] guidePathMeasures;
    float incrementCenterGuidePath;
    float[] incrementGuidePaths;
    Paint whiteCirclePaint;
    Paint[] paints;
    float globalContPathLen=0.0f;
    int notPainting=0;

    int[] vals={75};
    int[] colors={R.color.colorAccent};

    TextView textView;
    boolean textMoved=false;

    public PerceCard(Context context, AttributeSet attrs){
        super(context, attrs);
        thisView = this;
    }

    public void setTextView(TextView tv){textView=tv;}

    public void setData(int[] values){
        vals=values;
    }

    public void setColors(int[] colorsArr){
        colors=colorsArr;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("debug","threadStopped");
        Log.d("debug", "padding=" + ((getWidth() / 3) - (getWidth() / 5)));
        padding=((getWidth()/3)-(getWidth()/5));

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        viewHandler.post(updateView);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas){

        if(hasInit) {
            canvas.drawCircle(getWidth()/2, getHeight()/2, (getWidth()-padding/2)/2, whiteCirclePaint);

            for(int i=0; i<paintPaths.length; i++){
                //if(contPathLengths[i]<guidePathMeasures[i].getLength())
                //canvas.drawPath(guidePaths[i], paints[i]);
                canvas.drawPath(paintPaths[i],paints[i]);
                //canvas.drawPath(guidePaths[i],paints[i]);
                //canvas.drawPath(centerGuidePath,paints[i]);
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
                viewHandler.postDelayed(updateView, 5l);
            }else{
                Log.d("debug","threadStopped");
            }
        }
    };

    private void init(){


        RectF oval = new RectF();
        oval.set(0 + padding+(getWidth()/25), 0 + padding+(getWidth()/25), getWidth() - padding-(getWidth()/25), getWidth() - padding-(getWidth()/25));

        RectF innerOval = new RectF();

        innerOval.set(
                ((getWidth() - padding*2+(getWidth()/10))/3) + padding,
                ((getHeight() - padding*2+(getWidth()/10))/3) + padding,
                (getWidth()-padding) - ((getWidth() - padding*2+(getWidth()/10))/3),
                (getWidth()-padding) - ((getHeight() - padding*2+(getWidth()/10))/3)
        );

        //innerOval.set(0, 0, getWidth(), getWidth());
        centerGuidePath=new Path();
        centerGuidePath.arcTo(innerOval, 0, percentageToDegree(vals[0]));
        centerGuideMeasure = new PathMeasure(centerGuidePath, false);
        incrementCenterGuidePath = centerGuideMeasure.getLength()/100;
        contCenterPathLength=0.0f;

        paints=new Paint[vals.length];
        contPathLengths=new float[vals.length];
        guidePaths = new Path[vals.length];
        guidePathMeasures = new PathMeasure[vals.length];
        incrementGuidePaths = new float[vals.length];
        paintPaths = new Path[vals.length];
        for(int i=0; i<vals.length; i++){
            contPathLengths[i]=0.0f;
            guidePaths[i]=new Path();
            guidePaths[i].arcTo(oval,0,percentageToDegree(vals[i]));

            Log.d("", "lastP=" + lastPositionInDegree(i) + " perc=" + percentageToDegree(vals[i]));

            guidePathMeasures[i] = new PathMeasure(guidePaths[i],false);
            incrementGuidePaths[i] = guidePathMeasures[i].getLength()/100;

            paintPaths[i]=new Path();
            paints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paints[i].setStyle(Paint.Style.STROKE);
            paints[i].setColor(ContextCompat.getColor(getContext(), colors[i]));
            paints[i].setStrokeWidth(7);
            paints[i].setStyle(Paint.Style.STROKE);
        }

        whiteCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whiteCirclePaint.setStyle(Paint.Style.STROKE);
        whiteCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        whiteCirclePaint.setShadowLayer(5, -5, 10, 0x30000000);
        whiteCirclePaint.setStrokeWidth(7);
        whiteCirclePaint.setStyle(Paint.Style.FILL);
        /*
        guidePath = new Path();
        guidePath.arcTo(oval, -90, 90, false);
        guidePathMeasure = new PathMeasure(guidePath,false);
        paintPath = new Path();
        */

        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.width=getWidth();
        params.height=getHeight();


        hasInit=true;
    }

    private void updateState(){

        if(!hasInit)
            init();

        if(textView!=null && !textMoved && hasInit) {
            textView.animate()
                    .x(textView.getX() + padding*2)
                    .y(textView.getY() - padding)
                    .setDuration(0)
                    .start();
            textMoved=true;

            textView.setTextSize((padding*10)/25);
            textView.setText(""+vals[0]+"%");
        }

        for(int i=0; i<vals.length; i++){
            contPathLengths[i]+=incrementGuidePaths[i];
            contCenterPathLength+=incrementCenterGuidePath;

            if(contPathLengths[i]<=guidePathMeasures[i].getLength()) {
                float[] pos = new float[]{0.0f, 0.0f};
                centerGuideMeasure.getPosTan(contCenterPathLength,pos,null);
                paintPaths[i].moveTo(pos[0], pos[1]);
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