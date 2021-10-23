package com.hnievat.controlluces;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    List<MyDatalist> MyDatalists;

    public MyAdapter(List<MyDatalist> MyDatalists) {
        this.MyDatalists = MyDatalists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_data,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MyDatalist md=MyDatalists.get(i);
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);

        //  viewHolder.txtid.setText(md.getId());
        viewHolder.txtname.setText(df.format(md.getDate()));
        viewHolder.txtemail.setText((md.getOn()) ? "Encendido" : "Apagado");
        //viewHolder.txtcity.setText((md.getManual()) ? "Manual" : "Auto");
    }

    @Override
    public int getItemCount() {
        return MyDatalists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtid;
        private final TextView txtname;
        private final TextView txtemail;
        private TextView txtcity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //txtid=(TextView)itemView.findViewById(R.id.txt_id);
            txtname= itemView.findViewById(R.id.txt_datetime);
            txtemail= itemView.findViewById(R.id.txt_onoff);
            //txtcity=(TextView)itemView.findViewById(R.id.txt_automanual);
        }
    }
}
