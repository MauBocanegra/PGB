package propulsar.pgb.domainlayer.objects;

/**
 * Created by maubocanegra on 13/02/17.
 */

public class Benefs {

    public Benefs(){}

    public int id;
    public String titulo;
    public String desc;
    public String fecha;
    public String imageUrl;
    public int surveyIdAssociated;

    public Benefs(int id, String titulo, String desc, String fecha){
        this.id=id; this.titulo=titulo; this.desc=desc; this.fecha=fecha;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getSurveyIdAssociated() {
        return surveyIdAssociated;
    }

    public void setSurveyIdAssociated(int surveyIdAssociated) {
        this.surveyIdAssociated = surveyIdAssociated;
    }
}
