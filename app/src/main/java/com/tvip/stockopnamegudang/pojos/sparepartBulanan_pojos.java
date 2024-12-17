package com.tvip.stockopnamegudang.pojos;

public class sparepartBulanan_pojos {
    private int id;
    private String order_id;
    private String order_name;
    private String warehouse;
    private String name;

    private String stok_awal;
    private String stok_akhir;
    private String stok_opname;

    private String condition;

    private String stok_opname_system;

    private String in;
    private String out;

    private String editting;

    private String tgl_opname;
    private String ref_id;


    public sparepartBulanan_pojos(int id, String order_id, String order_name, String warehouse, String name,
                                  String stok_awal, String stok_akhir, String stok_opname, String condition,
                                  String stok_opname_system, String in, String out, String editting, String tgl_opname,
                                  String ref_id) {
        this.id = id;
        this.order_id = order_id;
        this.order_name = order_name;
        this.warehouse = warehouse;
        this.name = name;

        this.stok_awal = stok_awal;
        this.stok_akhir = stok_akhir;
        this.stok_opname = stok_opname;
        this.condition = condition;
        this.stok_opname_system = stok_opname_system;

        this.in = in;
        this.out = out;
        this.editting = editting;
        this.tgl_opname = tgl_opname;
        this.ref_id = ref_id;
    }

    public int getId() {
        return id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_name() {
        return order_name;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public String getName() {
        return name;
    }

    public String getStok_akhir() {
        return stok_akhir;
    }


    public String getStok_awal() {
        return stok_awal;
    }

    public String getStok_opname() {
        return stok_opname;
    }

    public void setStok_opname(String stok_opname) {
        this.stok_opname = stok_opname;
    }

    public String getCondition() {
        return condition;
    }

    public String getStok_opname_system() {
        return stok_opname_system;
    }

    public void setStok_opname_system(String stok_opname_system) {
        this.stok_opname_system = stok_opname_system;
    }

    public String getIn() {
        return in;
    }

    public String getOut() {
        return out;
    }

    public String getEditting() {
        return editting;
    }

    public void setEditting(String editting) {
        this.editting = editting;
    }

    public String getTgl_opname() {
        return tgl_opname;
    }

    public String getRef_id() {
        return ref_id;
    }
}
