package com.example.aula13.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.aula13.R;
import com.example.aula13.activity.AlunoActivity;
import com.example.aula13.api.ApiClient;
import com.example.aula13.api.AlunoService;
import com.example.aula13.model.Aluno;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.AlunoHolder> {
    private final List<Aluno> alunos;
    Context context;

    public AlunoAdapter(List<Aluno> alunos, Context context) {
        this.alunos = alunos;
        this.context = context;
    }

    @Override
    public AlunoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlunoHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_aluno, parent, false));
    }

    @Override
    public void onBindViewHolder(AlunoHolder holder, int position) {
        holder.nome.setText(alunos.get(position).getRa() + " - " + alunos.get(position).getNome());
        holder.email.setText(alunos.get(position).getCep());
        holder.btnExcluir.setOnClickListener(view -> removerItem(position));
        holder.btnEditar.setOnClickListener(view -> editarItem(position));
    }

    @Override
    public int getItemCount() {
        return alunos != null ? alunos.size() : 0;
    }

    private void removerItem(int position) {
        int id = alunos.get(position).getRa();
        AlunoService apiService = ApiClient.getAlunoService();
        Call<Void> call = apiService.deleteAluno(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    alunos.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, alunos.size());
                    Toast.makeText(context, "Excluído com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Exclusao", "Erro ao excluir");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Exclusao", "Erro ao excluir");
            }
        });
    }

    private void editarItem(int position) {
        int id = alunos.get(position).getRa();
        Intent i = new Intent(context, AlunoActivity.class);
        i.putExtra("id", id);
        context.startActivity(i);
    }

    public class AlunoHolder extends RecyclerView.ViewHolder {

        public TextView nome;
        public TextView email;
        public ImageView btnExcluir;
        public ImageView btnEditar;

        public AlunoHolder(View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.txtNome);
            email = itemView.findViewById(R.id.txtEmail);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
