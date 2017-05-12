package com.jiexingxing.dragchannelmanager;

/**
 * Created by 张晓宁 on 2017/5/12.
 */

public class ItemBean {

    public ItemBean(String text, boolean select) {
        this.text = text;
        this.select = select;
    }

    private  String  text;
  private  boolean  select;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
