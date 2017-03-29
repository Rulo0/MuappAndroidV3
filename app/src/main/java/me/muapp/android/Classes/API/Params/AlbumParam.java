package me.muapp.android.Classes.API.Params;

/**
 * Created by rulo on 29/03/17.
 */
public class AlbumParam {
    private String name;
    private String url;
    private String filePath;
    private byte[] fileBytes;

    public String getFileName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setData(String filePath) {
        this.filePath = filePath;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }
}


