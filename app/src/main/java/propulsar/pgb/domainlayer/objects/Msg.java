package propulsar.pgb.domainlayer.objects;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by maubocanegra on 08/03/17.
 */

public class Msg {
    public int id;
    public String msg;
    public int senderId;
    public String senderName;
    public String timeStamp;
    public String url;
    public boolean isBot;
    public String avatarURL;
    public int type;
    public ArrayList<String> buttons;
    public View.OnClickListener onClickListener;

    public ArrayList<String> getButtons() {
        return buttons;
    }

    public void setButtons(ArrayList<String> buttons) {
        this.buttons = buttons;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public Msg(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String toString(){
        return "Sender="+senderId+" Msg="+msg+" timeStamp="+timeStamp+" type="+type;
    }
}
