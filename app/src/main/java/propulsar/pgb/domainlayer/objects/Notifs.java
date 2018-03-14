package propulsar.pgb.domainlayer.objects;

/**
 * Created by maubocanegra on 09/02/17.
 */

public class Notifs {

    public int id;
    public String notif;
    public String fecha;

    public Notifs(){}

    public Notifs(int id, String notif, String fecha){
        this.id = id;
        this.notif = notif;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
