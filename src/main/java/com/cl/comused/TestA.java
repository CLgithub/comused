package com.cl.comused;

public class TestA {
    private String aa;
    private int bb;
    private String cc;

    public TestA(String aa, int bb, String cc) {
        this.aa = aa;
        this.bb = bb;
        this.cc = cc;
    }

    public TestA() {
    }

    public String getAa() {
        return aa;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }

    public int getBb() {
        return bb;
    }

    public void setBb(int bb) {
        this.bb = bb;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    @Override
    public String toString() {
        return "TestA{" +
                "aa='" + aa + '\'' +
                ", bb=" + bb +
                ", cc='" + cc + '\'' +
                '}';
    }
}
