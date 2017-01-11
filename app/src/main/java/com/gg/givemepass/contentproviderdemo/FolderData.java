package com.gg.givemepass.contentproviderdemo;

/**
 * Created by rick.wu on 2017/1/10.
 */
public class FolderData {
    public String path;

    private String bucketName;

    public FolderData(){
        path = "";
        bucketName = "";
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        FolderData f = (FolderData)obj;
        return this.path.equals(f.getPath());
    }
}
