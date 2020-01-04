package com.example.e01_memo.data.pojo;

import java.util.Date;

public class MemoItem {

    private int id;
    private String memo;
    private Date createDate;
    private boolean favorite;
    private boolean deleteFlag;

    public MemoItem() {}

    public MemoItem(int id, String memo, Date createDate, boolean favorite, boolean deleteFlag) {
        this.id = id;
        this.memo = memo;
        this.createDate = createDate;
        this.favorite = favorite;
        this.deleteFlag = deleteFlag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isDeleteFlag() { return deleteFlag; }

    public void setDeleteFlag(boolean deleteFlag) { this.deleteFlag = deleteFlag; }
}
