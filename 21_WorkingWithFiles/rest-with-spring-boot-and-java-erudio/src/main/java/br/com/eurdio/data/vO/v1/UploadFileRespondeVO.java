package br.com.eurdio.data.vO.v1;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class UploadFileRespondeVO implements Serializable {


    @Serial
    private static final long serialVersionUID =  1L;

    private String fileName;

    private String fileDownloadUri;

    private String fileType;
    private long size;
    public UploadFileRespondeVO(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UploadFileRespondeVO that = (UploadFileRespondeVO) o;

        if (size != that.size) return false;
        if (!Objects.equals(fileName, that.fileName)) return false;
        if (!Objects.equals(fileDownloadUri, that.fileDownloadUri))
            return false;
        return Objects.equals(fileType, that.fileType);
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (fileDownloadUri != null ? fileDownloadUri.hashCode() : 0);
        result = 31 * result + (fileType != null ? fileType.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }
}
