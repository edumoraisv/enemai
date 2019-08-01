package com.enemai.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enemai.R;
import com.enemai.model.data;

import java.util.List;

public class listaAdapter extends RecyclerView.Adapter<listaAdapter.holder> {

    private Context mContext = null;
    private List<data> dados = null;

    public listaAdapter(Context mContext, List<data> dados) {
        this.mContext = mContext;
        this.dados = dados;
    }

    public void showAlerta(){
        if(mContext == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Aguarde...");
        builder.setMessage(R.string.appConteudo);
        builder.setIcon(R.drawable.ic_star);
        builder.setPositiveButton("CERTO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alerta = builder.create();
        alerta.show();
    }

    @Override
    public holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.row_temas,parent,false);
        final listaAdapter.holder viewholder = new listaAdapter.holder(view);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(holder holder, int position) {
        if(dados.get(position).getTitulo().length() > 50){
            holder.ttitulo.setText(dados.get(position).getTitulo().substring(0,50)+"...");
        }else {
            holder.ttitulo.setText(dados.get(position).getTitulo());
        }

        if(dados.get(position).getDescricao().length() > 50){
            holder.tdescricao.setText(dados.get(position).getPrecisao() + "% - "+dados.get(position).getDescricao().substring(0,50)+"...");
        }else{
            holder.tdescricao.setText(dados.get(position).getPrecisao() + "% - "+dados.get(position).getDescricao());
        }

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlerta();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dados.size();
    }

    class holder extends RecyclerView.ViewHolder{

        private TextView ttitulo;
        private TextView tdescricao;
        private RelativeLayout content;

        public holder(View itemView) {
            super(itemView);

            ttitulo = (TextView)itemView.findViewById(R.id.tema_titulo);
            tdescricao = (TextView)itemView.findViewById(R.id.tema_descricao);
            content = (RelativeLayout) itemView.findViewById(R.id.themeContent);

        }
    }
}
