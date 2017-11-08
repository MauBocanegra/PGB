package propulsar.pgb.domainlayer.objects;

/**
 * Created by maubocanegra on 09/02/17.
 */

public class Case {

    public Case(){}

    public Case (int id, String folio, String titulo, String categoria,
                 String descripcion, String fecha, int status, int creatorID){
        this.id=id;
        this.folio=folio;
        this.titulo=titulo;
        this.categoria=categoria;
        this.descripcion=descripcion;
        this.fecha=fecha;
        this.status=status;
        this.creatorID=creatorID;
    }

    public int id;
    public String folio;
    public String titulo;
    public String categoria;
    public String descripcion;
    public String fecha;
    public int status;
    public int creatorID;
    public int creatorType;
    public int votesUp;
    public int votesDown;
    public double votesUpPercent;
    public double votesDownPercent;
    public String imageUrl;
    public String creatorName;
    public String lasStatusColor;

    public String getLasStatusColor() {
        return lasStatusColor;
    }

    public void setLasStatusColor(String lasStatusColor) {
        this.lasStatusColor = lasStatusColor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(int creatorType) {
        this.creatorType = creatorType;
    }

    public int getVotesUp() {
        return votesUp;
    }

    public void setVotesUp(int votesUp) {
        this.votesUp = votesUp;
    }

    public int getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(int votesDown) {
        this.votesDown = votesDown;
    }

    public double getVotesUpPercent() {
        return votesUpPercent;
    }

    public void setVotesUpPercent(double votesUpPercent) {
        this.votesUpPercent = votesUpPercent;
    }

    public double getVotesDownPercent() {
        return votesDownPercent;
    }

    public void setVotesDownPercent(double votesDownPercent) {
        this.votesDownPercent = votesDownPercent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
