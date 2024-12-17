package com.tvip.stockopnamegudang.pojos;

public class sparepart_pojos {

    /* renamed from: in */
    private int id;
    private String f874in;
    private String name;
    private String warehouse;
    private String order_id;
    private String order_name;
    private String out;
    private String stock_fisik;
    private String stok_akhir;
    private String stok_awal;
    private String tgl_closing;

    private String condition;

    private String stock_system;

    public sparepart_pojos(int id, String tgl_closing2, String name2, String warehouse, String order_id2, String order_name2, String stok_awal2, String in, String out2, String stok_akhir2, String stock_fisik2, String condition, String stock_system) {
        this.id = id;
        this.tgl_closing = tgl_closing2;
        this.name = name2;
        this.warehouse = warehouse;
        this.order_id = order_id2;
        this.order_name = order_name2;
        this.stok_awal = stok_awal2;
        this.f874in = in;
        this.out = out2;
        this.stok_akhir = stok_akhir2;
        this.stock_fisik = stock_fisik2;
        this.condition = condition;
        this.stock_system = stock_system;
    }

    public int getId() {
        return id;
    }

    public String getTgl_closing() {
        return this.tgl_closing;
    }

    public String getName() {
        return this.name;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public String getOrder_id() {
        return this.order_id;
    }

    public String getOrder_name() {
        return this.order_name;
    }

    public String getStok_awal() {
        return this.stok_awal;
    }

    public String getIn() {
        return this.f874in;
    }

    public String getOut() {
        return this.out;
    }

    public String getStok_akhir() {
        return this.stok_akhir;
    }

    public String getStock_fisik() {
        return this.stock_fisik;
    }

    public void setStock_fisik(String stock_fisik2) {
        this.stock_fisik = stock_fisik2;
    }

    public String toString() {
        return this.order_name;
    }

    public String getCondition() {
        return condition;
    }

    public String getStock_system() {
        return stock_system;
    }

    public void setStock_system(String stock_system) {
        this.stock_system = stock_system;
    }


}
