package com.example.lucas.controlcar.relatorio;

/**
 * Created by lucas on 26/08/17.
 */

public class Relatorio {

    private Long id;
    private String data;
    private Double mediaKm;
    private Double mediaRpm;
    private String latitude;
    private String longitude;
    private Double kmdia;
    private Double kmmax;
    private Double kmmin;

    public static final String ID = "_id";
    public static final String DATA = "r_data";
    public static final String MEDIAKM = "r_mediakm";
    public static final String MEDIARPM = "r_mediarpm";
    public static final String LATITUDE = "r_latitude";
    public static final String LONGITUDE = "r_longitude";
    public static final String KMDIA = "r_kmdia";
    public static final String KMMAX = "r_kmmax";
    public static final String KMMIN = "r_kmmin";

    public static final String TABELA = "relatorios";
    public static final String[] COLUNAS = {ID, DATA, MEDIAKM, MEDIARPM, LATITUDE, LONGITUDE, KMDIA, KMMAX, KMMIN};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Double getMediaKm() {
        return mediaKm;
    }

    public void setMediaKm(Double mediaKm) {
        this.mediaKm = mediaKm;
    }

    public Double getMediaRpm() {
        return mediaRpm;
    }

    public void setMediaRpm(Double mediaRpm) {
        this.mediaRpm = mediaRpm;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Double getKmdia() {
        return kmdia;
    }

    public void setKmdia(Double kmdia) {
        this.kmdia = kmdia;
    }

    public Double getKmmax() {
        return kmmax;
    }

    public void setKmmax(Double kmmax) {
        this.kmmax = kmmax;
    }

    public Double getKmmin() {
        return kmmin;
    }

    public void setKmmin(Double kmmin) {
        this.kmmin = kmmin;
    }
}
