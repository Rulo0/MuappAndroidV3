package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 6/06/17.
 */

public class MuappDialog implements Parcelable {
    Boolean active;
    String contentImageUrl;
    String contentText;
    String dialogInternalSection;
    String headerIconUrl;
    Boolean showAlways;
    Boolean showCancelButton;
    String showInSection;
    String title;


    public MuappDialog() {
    }

    public Boolean getActive() {
        return active;
    }

    public String getContentImageUrl() {
        return contentImageUrl;
    }

    public String getContentText() {
        return contentText;
    }

    public String getDialogInternalSection() {
        return dialogInternalSection;
    }

    public String getHeaderIconUrl() {
        return headerIconUrl;
    }

    public Boolean getShowAlways() {
        return showAlways;
    }

    public Boolean getShowCancelButton() {
        return showCancelButton;
    }

    public String getShowInSection() {
        return showInSection;
    }

    public String getTitle() {
        return title;
    }

    protected MuappDialog(Parcel in) {
        byte activeVal = in.readByte();
        active = activeVal == 0x02 ? null : activeVal != 0x00;
        contentImageUrl = in.readString();
        contentText = in.readString();
        dialogInternalSection = in.readString();
        headerIconUrl = in.readString();
        byte showAlwaysVal = in.readByte();
        showAlways = showAlwaysVal == 0x02 ? null : showAlwaysVal != 0x00;
        byte showCancelButtonVal = in.readByte();
        showCancelButton = showCancelButtonVal == 0x02 ? null : showCancelButtonVal != 0x00;
        showInSection = in.readString();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (active == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (active ? 0x01 : 0x00));
        }
        dest.writeString(contentImageUrl);
        dest.writeString(contentText);
        dest.writeString(dialogInternalSection);
        dest.writeString(headerIconUrl);
        if (showAlways == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (showAlways ? 0x01 : 0x00));
        }
        if (showCancelButton == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (showCancelButton ? 0x01 : 0x00));
        }
        dest.writeString(showInSection);
        dest.writeString(title);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MuappDialog> CREATOR = new Parcelable.Creator<MuappDialog>() {
        @Override
        public MuappDialog createFromParcel(Parcel in) {
            return new MuappDialog(in);
        }

        @Override
        public MuappDialog[] newArray(int size) {
            return new MuappDialog[size];
        }
    };
}
