
package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CandidatesResult implements Parcelable {

    @SerializedName("current_page")
    @Expose
    private Integer currentPage;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("next_page")
    @Expose
    private Integer nextPage;
    @SerializedName("elements")
    @Expose
    private Integer elements;
    @SerializedName("users")
    @Expose
    private List<Candidate> candidates = null;
    public final static Parcelable.Creator<CandidatesResult> CREATOR = new Creator<CandidatesResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CandidatesResult createFromParcel(Parcel in) {
            CandidatesResult instance = new CandidatesResult();
            instance.currentPage = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.nextPage = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.elements = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.candidates, (me.muapp.android.Classes.Internal.Candidate.class.getClassLoader()));
            return instance;
        }

        public CandidatesResult[] newArray(int size) {
            return (new CandidatesResult[size]);
        }

    };

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    public Integer getElements() {
        return elements;
    }

    public void setElements(Integer elements) {
        this.elements = elements;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(currentPage);
        dest.writeValue(totalPages);
        dest.writeValue(nextPage);
        dest.writeValue(elements);
        dest.writeList(candidates);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "CandidatesResult{" +
                "currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", nextPage=" + nextPage +
                ", elements=" + elements +
                ", candidates=" + candidates +
                '}';
    }
}
