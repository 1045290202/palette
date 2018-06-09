package com.sjk.palette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private List<Option> optionList;

    public MenuAdapter(List<Option> optionList) {
        this.optionList = optionList;
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.optionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.getMainActivity(), AboutActivity.class);
                        MainActivity.getMainActivity().startActivity(intent);
                        MainActivity.getMainActivity().dismissMenuDialog();
                        break;
                    case 1:
                        MainActivity.getMainActivity().finish();
                        break;
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, int position) {
        Option option = optionList.get(position);
        holder.optionImage.setImageResource(option.getOptionImageID());
        holder.optionName.setText(option.getOptionName());
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView optionName;
        ImageView optionImage;
        View optionView;

        public ViewHolder(View itemView) {
            super(itemView);
            optionView = itemView;
            optionImage = itemView.findViewById(R.id.option_image);
            optionName = itemView.findViewById(R.id.option_name);
        }
    }
}
