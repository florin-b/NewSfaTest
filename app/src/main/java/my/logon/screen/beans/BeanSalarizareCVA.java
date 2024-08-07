package my.logon.screen.beans;

import java.util.List;

public class BeanSalarizareCVA {

    private String codAgent;
    private String numeAgent;
    private List<SalarizareCVABazaCL> listSalarizareCVABaza;
    private double venitBaza;
    private double nruf;
    private double coef;
    private double venitNruf;
    private double venitTcf;
    private double venitStocNociv;
    private double corectIncas;
    private double venitFinal;
    private List<BeanVanzariVS> detaliiVS;

    public double getVenitBaza() {
        return venitBaza;
    }

    public void setVenitBaza(double venitBaza) {
        this.venitBaza = venitBaza;
    }

    public double getNruf() {
        return nruf;
    }

    public void setNruf(double nruf) {
        this.nruf = nruf;
    }

    public double getCoef() {
        return coef;
    }

    public void setCoef(double coef) {
        this.coef = coef;
    }

    public double getVenitNruf() {
        return venitNruf;
    }

    public void setVenitNruf(double venitNruf) {
        this.venitNruf = venitNruf;
    }

    public double getVenitTcf() {
        return venitTcf;
    }

    public void setVenitTcf(double venitTcf) {
        this.venitTcf = venitTcf;
    }

    public double getCorectIncas() {
        return corectIncas;
    }

    public void setCorectIncas(double corectIncas) {
        this.corectIncas = corectIncas;
    }

    public double getVenitFinal() {
        return venitFinal;
    }

    public void setVenitFinal(double venitFinal) {
        this.venitFinal = venitFinal;
    }

    public List<SalarizareCVABazaCL> getListSalarizareCVABaza() {
        return listSalarizareCVABaza;
    }

    public void setListSalarizareCVABaza(List<SalarizareCVABazaCL> listSalarizareCVABaza) {
        this.listSalarizareCVABaza = listSalarizareCVABaza;
    }

    public List<BeanVanzariVS> getDetaliiVS() {
        return detaliiVS;
    }

    public void setDetaliiVS(List<BeanVanzariVS> detaliiVS) {
        this.detaliiVS = detaliiVS;
    }

    public double getVenitStocNociv() {
        return venitStocNociv;
    }

    public void setVenitStocNociv(double venitStocNociv) {
        this.venitStocNociv = venitStocNociv;
    }

    public String getCodAgent() {
        return codAgent;
    }

    public void setCodAgent(String codAgent) {
        this.codAgent = codAgent;
    }

    public String getNumeAgent() {
        return numeAgent;
    }

    public void setNumeAgent(String numeAgent) {
        this.numeAgent = numeAgent;
    }
}
