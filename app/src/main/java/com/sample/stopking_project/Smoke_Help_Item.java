package com.sample.stopking_project;

public class Smoke_Help_Item {

    private String title;
    private String img_url;
    private String detail_link;
    private String summary;

    public Smoke_Help_Item(String title, String url, String link, String summary){
        this.title = title;
        this.img_url = url;
        this.detail_link = link;
        this.summary = summary;
    }
    public String getTitle() {
        return title;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getDetail_link() {
        return detail_link;
    }

    public String getSummary() {
        return summary;
    }
}
