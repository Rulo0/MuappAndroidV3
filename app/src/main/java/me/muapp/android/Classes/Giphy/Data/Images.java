
package me.muapp.android.Classes.Giphy.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images implements Parcelable {

    @SerializedName("original")
    @Expose
    private Original original;
    @SerializedName("preview_gif")
    @Expose
    private PreviewGif previewGif;
    public final static Parcelable.Creator<Images> CREATOR = new Creator<Images>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Images createFromParcel(Parcel in) {
            Images instance = new Images();
            instance.original = ((Original) in.readValue((Original.class.getClassLoader())));
            instance.previewGif = ((PreviewGif) in.readValue((PreviewGif.class.getClassLoader())));
            return instance;
        }

        public Images[] newArray(int size) {
            return (new Images[size]);
        }

    };

    public Original getOriginal() {
        return original;
    }

    public void setOriginal(Original original) {
        this.original = original;
    }

    public PreviewGif getPreviewGif() {
        return previewGif;
    }

    public void setPreviewGif(PreviewGif previewGif) {
        this.previewGif = previewGif;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(original);
        dest.writeValue(previewGif);
    }

    public int describeContents() {
        return 0;
    }

}
