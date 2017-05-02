package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.muapp.android.Classes.Internal.MuappQualifications.Qualification;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.R;

/**
 * Created by rulo on 28/03/17.
 */

public class UserQualificationsAdapter extends RecyclerView.Adapter<UserQualificationsAdapter.QualificationViewHolder> {
    private final LayoutInflater mInflater;
    private List<Qualification> qualifications;
    private Context mContext;
    User user;

    public UserQualificationsAdapter(Context context, List<Qualification> qualifications) {
        this.mInflater = LayoutInflater.from(context);
        this.qualifications = qualifications;
        this.mContext = context;
    }

    @Override
    public QualificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.user_qualification_item, parent, false);
        return new QualificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QualificationViewHolder holder, int position) {
        holder.bind(qualifications.get(position));
    }

    @Override
    public int getItemCount() {
        return qualifications.size();
    }

    public class QualificationViewHolder extends RecyclerView.ViewHolder {
        LinearLayout stars_container;
        TextView txt_qualificator_name;

        public QualificationViewHolder(View itemView) {
            super(itemView);
            stars_container = (LinearLayout) itemView.findViewById(R.id.stars_container);
            txt_qualificator_name = (TextView) itemView.findViewById(R.id.txt_qualificator_name);
        }

        public void bind(final Qualification qualification) {
            stars_container.removeAllViews();
            for (int i = 0; i < 5; i++) {
                ImageView star = new ImageView(mContext);
                star.setImageResource(i <= qualification.getStars() ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
                stars_container.addView(star);
            }
            txt_qualificator_name.setText(qualification.getUserName());
        }
    }
}
