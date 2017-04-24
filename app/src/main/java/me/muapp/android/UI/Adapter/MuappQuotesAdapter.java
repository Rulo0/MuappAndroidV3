package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.R;

/**
 * Created by rulo on 18/04/17.
 */

public class MuappQuotesAdapter extends PagerAdapter {
    private Context context;
    private List<MuappQuote> quotes;
    private final LayoutInflater mInflater;
    private String lang;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MuappQuote quote = quotes.get(position);
        View v = mInflater.inflate(R.layout.muapp_quote_item_layout, container, false);
        TextView txt_quote_container = (TextView) v.findViewById(R.id.txt_quote_container);
        txt_quote_container.setText(lang.equals("es") ? quotes.get(position).getCaptionSpa() : quotes.get(position).getCaptionEng())
        ;
        container.addView(v);
        return v;
    }

    public MuappQuotesAdapter(Context context) {
        this.context = context;
        this.quotes = new ArrayList<>();
        this.mInflater = LayoutInflater.from(context);
        this.lang = Locale.getDefault().getLanguage();
    }

    public void setQuotes(List<MuappQuote> quotes) {
        this.quotes = quotes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return quotes.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

}
