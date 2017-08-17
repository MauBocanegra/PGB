package propulsar.qroo.PresentationLayer.CustomViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.io.InputStream;

/**
 * Created by maubocanegra on 25/03/17.
 */

public class PlayGifImageView extends AppCompatImageView {

    private boolean mIsPlayingGif = false;

    private GifDecoder mGifDecoder;

    private Bitmap mTmpBitmap;

    final Handler mHandler = new Handler();

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            if (mTmpBitmap != null && !mTmpBitmap.isRecycled()) {
                //GifDecoderView.this.setImageBitmap(mTmpBitmap);
            }
        }
    };

    public PlayGifImageView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public PlayGifImageView(Context context, InputStream stream) {
        super(context);
        playGif(stream);


    }

    private void playGif(InputStream stream) {
        mGifDecoder = new GifDecoder();
        mGifDecoder.read(stream);
        mIsPlayingGif = true;
    }
}
