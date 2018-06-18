package com.sjk.palette.aboutActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sjk.palette.R;
import com.sjk.palette.aboutActivity.Info;

import java.util.List;

public class InfoAdapter extends ArrayAdapter<Info> {
    private int resource;

    public InfoAdapter(Context context, int resource, List<Info> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Info info = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.infoTitle = view.findViewById(R.id.info_title);
            viewHolder.detailInfo = view.findViewById(R.id.detail_info);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.infoTitle.setText(info.getInfoTitle());
        viewHolder.detailInfo.setText(info.getDetailInfo());
        return view;
    }

    class ViewHolder {
        TextView infoTitle, detailInfo;
    }
}
