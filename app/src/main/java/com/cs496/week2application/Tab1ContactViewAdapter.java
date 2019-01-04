package com.cs496.week2application;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Parsania Hardik on 11-May-17.
 */
public class Tab1ContactViewAdapter extends BaseAdapter {

    private Context context;
    private Map<String, ContactModel> contactModelMap;
    private ArrayList<String> contactKeyList;

    public Tab1ContactViewAdapter(Context context, Map<String, ContactModel>contactMap) {
        this.context = context;
        this.contactModelMap = contactMap;
        Set<String> keySet = contactModelMap.keySet();
        this.contactKeyList = new ArrayList<String>();
        this.contactKeyList.addAll(keySet);
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return contactModelMap.size();
    }

    @Override
    public Object getItem(int position) {
        return contactModelMap.get(contactKeyList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.tvname = (TextView) convertView.findViewById(R.id.name);
            holder.tvnumber = (TextView) convertView.findViewById(R.id.number);
            holder.ivphoto = (ImageView) convertView.findViewById(R.id.contactPhoto);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        ContactModel contact = contactModelMap.get(contactKeyList.get(position));
        holder.tvname.setText(contact.getName());
        holder.tvnumber.setText(contact.getNumber());
        if (contact.getIcon() != null) holder.ivphoto.setImageBitmap(contact.getIcon());

        //Log.d("FINAL CONTACT>>>>>", contactModelMap.get(position).getName() + "  " + contactModelMap.get(position).getNumber());

        return convertView;
    }

    private class ViewHolder {

        protected TextView tvname, tvnumber;
        protected ImageView ivphoto;

    }
}
