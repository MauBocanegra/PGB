package propulsar.qroo.DomainLayer.Adapters;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import propulsar.qroo.PresentationLayer.Activities.ChatActivity;
import propulsar.qroo.R;

/**
 * Created by maubocanegra on 21/04/17.
 */

public class GalleryAdapter extends BaseAdapter {

    private ChatActivity activity;

    //ArrayList<File> images;
    ArrayList<String> imagesPath;

    public GalleryAdapter(ChatActivity a){
        activity=a;

        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String publicDCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ "/";
                String publicPICS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/";
                String publicDOWNS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/";

                File[] publicDCIMFiles = (new File(publicDCIM)).listFiles();
                File[] publicPICSFiles = (new File(publicPICS)).listFiles();
                File[] publicDOWNSFiles = (new File(publicDOWNS)).listFiles();

                ArrayList<File> files = new ArrayList<File>();

                //-------

                Log.d("files","publicDCIM="+publicDCIM.toString()+" length="+publicDCIMFiles.length);
                Log.d("files","publicPICS="+publicPICS.toString()+" length="+publicPICSFiles.length);

                Log.d("files","-------------");

                int countDCIMFiles=0;
                for(File fileDCIM : publicDCIMFiles){
                    Log.d("files","DCIM="+fileDCIM.toString());
                    if(fileDCIM.isDirectory()){
                        File[] innerDCIM = fileDCIM.listFiles();
                        for (File innerArchsDCIM : innerDCIM){
                            if(!innerArchsDCIM.isDirectory()){
                                files.add(innerArchsDCIM);
                                countDCIMFiles++;
                            }
                            if(countDCIMFiles>=15){break;}
                        }
                    }

                }

                int countPublicFiles=0;
                for(File filePIC : publicPICSFiles){
                    Log.d("files","PICS="+filePIC.toString());
                    if(filePIC.isDirectory()){
                        File[] innerPIC = filePIC.listFiles();
                        for(File innerArchsPIC : innerPIC){
                            if(!innerArchsPIC.isDirectory()){
                                files.add(innerArchsPIC);
                                countPublicFiles++;
                            }
                            if(countPublicFiles>=15){break;}
                        }
                    }
                }

                Log.d("files","# ALLFiles="+files.size());

                //images = new ArrayList<File>();
                imagesPath = new ArrayList<String>();
                //int kk = 0;
                int countFiles=0;
                for(File file : files){
                    try {
                        String extension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toURL().toString());
                        if(extension!=null){
                            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            //Log.d("files",mimeType);
                            //kk++;
                            //Log.d("files",mimeType);
                            if(mimeType.contains("image")){
                                imagesPath.add(file.getAbsolutePath());
                                //images.add(file);
                                countFiles++;
                                //Log.d("files","added!");
                            }
                            //if(countFiles==50){break;}
                            //if(kk==50){break;}
                        }
                    }catch(Exception e){}
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                //Log.d("files","# ALLFiles="+images.size());
                Log.d("files","# ALLFiles="+imagesPath.size());
                notifyDataSetChanged();
                mGalleryLoadedListener.onGalleryLoaded();
            }
        };
        task.execute();



        /*
        AsyncTask<File,Void,Void> task = new AsyncTask<File, Void, Void>() {
            @Override
            protected Void doInBackground(File... files) {
                getFilesFromDirectory(files[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d("files","# DCIMfiles="+files.size());
            }
        };
        task.execute(new File(publicDCIM));
        */
            /*
        Log.d("FILES","------ path="+path+" ------");
        File f = new File(path);
        if(f==null){return;}
        Log.d("FILES","------ fileFromPath="+f.toString()+" ------");
        files = f.listFiles();
        if(files==null){return;}
        Log.d("FILES","------ files[]="+files.length+" ------");
        */
        /*
        String path = Environment.getRootDirectory().toString();
        File f = new File(path);
        File file[] = f.listFiles();
        for (int i=0; i < file.length; i++)
        {
            CreateList createList = new CreateList();
            createList.setImage_Location(file[i].getName());
        }
        * */
    }

    @Override
    public int getCount() {
        if(imagesPath==null)return 0; else return imagesPath.size();
        //return 50;
    }

    @Override
    public Object getItem(int i) {
        return imagesPath.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(activity);
            int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, activity.getResources().getDisplayMetrics());
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) view;
        }

        imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_image_ph));
        //imageView.setImageBitmap(BitmapFactory.decodeFile(imagesPath.get(i)));
        Picasso.with(activity).load(Uri.fromFile(new File(imagesPath.get(i)))).into(imageView);
        imageView.setTag(imagesPath.get(i));
        return imageView;
    }

    public GalleryLoadedListener mGalleryLoadedListener;
    public interface GalleryLoadedListener{
        public void onGalleryLoaded();
    };
    public void setGalleryLoadedListener(GalleryLoadedListener l){
        mGalleryLoadedListener=l;
    }
}
