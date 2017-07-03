package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 6/06/17.
 */

public class MuappDialog implements Parcelable {
    String key;
    Boolean active;
    String contentImageUrl;
    String contentText;
    String dialogInternalSection;
    String headerIconUrl;
    Boolean showAlways;
    Boolean showCancelButton;
    String showInSection;
    String title;
    String extraButtonTitle;
    String dialogExternalUrl;
    String os;
    String gender;
    DialogLocation location;

    public MuappDialog() {
    }

    protected MuappDialog(Parcel in) {
        key = in.readString();
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
        extraButtonTitle = in.readString();
        dialogExternalUrl = in.readString();
        os = in.readString();
        gender = in.readString();
        location = (DialogLocation) in.readValue(DialogLocation.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
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
        dest.writeString(extraButtonTitle);
        dest.writeString(dialogExternalUrl);
        dest.writeString(os);
        dest.writeString(gender);
        dest.writeValue(location);
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getContentImageUrl() {
        return contentImageUrl;
    }

    public void setContentImageUrl(String contentImageUrl) {
        this.contentImageUrl = contentImageUrl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getDialogInternalSection() {
        return dialogInternalSection;
    }

    public void setDialogInternalSection(String dialogInternalSection) {
        this.dialogInternalSection = dialogInternalSection;
    }

    public String getHeaderIconUrl() {
        return headerIconUrl;
    }

    public void setHeaderIconUrl(String headerIconUrl) {
        this.headerIconUrl = headerIconUrl;
    }

    public Boolean getShowAlways() {
        return showAlways;
    }

    public void setShowAlways(Boolean showAlways) {
        this.showAlways = showAlways;
    }

    public Boolean getShowCancelButton() {
        return showCancelButton;
    }

    public void setShowCancelButton(Boolean showCancelButton) {
        this.showCancelButton = showCancelButton;
    }

    public String getShowInSection() {
        return showInSection;
    }

    public void setShowInSection(String showInSection) {
        this.showInSection = showInSection;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtraButtonTitle() {
        return extraButtonTitle;
    }

    public void setExtraButtonTitle(String extraButtonTitle) {
        this.extraButtonTitle = extraButtonTitle;
    }

    public String getDialogExternalUrl() {
        return dialogExternalUrl;
    }

    public void setDialogExternalUrl(String dialogExternalUrl) {
        this.dialogExternalUrl = dialogExternalUrl;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public DialogLocation getLocation() {
        return location;
    }

    public void setLocation(DialogLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "MuappDialog{" +
                "key='" + key + '\'' +
                ", active=" + active +
                ", contentImageUrl='" + contentImageUrl + '\'' +
                ", contentText='" + contentText + '\'' +
                ", dialogInternalSection='" + dialogInternalSection + '\'' +
                ", headerIconUrl='" + headerIconUrl + '\'' +
                ", showAlways=" + showAlways +
                ", showCancelButton=" + showCancelButton +
                ", showInSection='" + showInSection + '\'' +
                ", title='" + title + '\'' +
                ", extraButtonTitle='" + extraButtonTitle + '\'' +
                ", dialogExternalUrl='" + dialogExternalUrl + '\'' +
                ", os='" + os + '\'' +
                ", gender='" + gender + '\'' +
                ", location=" + (location != null ? location.toString() : "") +
                '}';
    }

}