package com.example.aula13.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aula13.R;
import com.example.aula13.api.ApiClient;
import com.example.aula13.api.AlunoService;
import com.example.aula13.model.Aluno;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class AlunoActivity extends AppCompatActivity {
    Button btnSalvar;
    AlunoService apiService;
    EditText txtRa, txtNome, txtCep, txtLogradouro, txtComplemento, txtBairro, txtCidade, txtUf;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        apiService = ApiClient.getAlunoService();
        txtRa = findViewById(R.id.txtRaAluno);
        txtNome = findViewById(R.id.txtNomeAluno);
        txtCep = findViewById(R.id.txtCepAluno);
        txtLogradouro = findViewById(R.id.txtLogradouroAluno);
        txtComplemento = findViewById(R.id.txtComplementoAluno);
        txtBairro = findViewById(R.id.txtBairroAluno);
        txtCidade = findViewById(R.id.txtCidadeAluno);
        txtUf = findViewById(R.id.txtUfAluno);

        id = getIntent().getIntExtra("id", 0);
        if (id > 0) {
            apiService.getAlunoPorId(id).enqueue(new Callback<Aluno>() {
                @Override
                public void onResponse(Call<Aluno> call, retrofit2.Response<Aluno> response) {
                    if (response.isSuccessful()) {
                        Aluno aluno = response.body();
                        txtRa.setText(String.valueOf(aluno.getRa()));
                        txtNome.setText(aluno.getNome());
                        txtCep.setText(aluno.getCep());
                        txtLogradouro.setText(aluno.getLogradouro());
                        txtComplemento.setText(aluno.getComplemento());
                        txtBairro.setText(aluno.getBairro());
                        txtCidade.setText(aluno.getCidade());
                        txtUf.setText(aluno.getUf());
                    }
                }

                @Override
                public void onFailure(Call<Aluno> call, Throwable t) {
                    Log.e("Obter aluno", "Erro ao obter aluno");
                }
            });
        }

        txtCep.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                buscarEnderecoPorCep(txtCep.getText().toString());
            }
        });

        btnSalvar.setOnClickListener(view -> {
            Aluno aluno = new Aluno();
            aluno.setRa(Integer.parseInt(txtRa.getText().toString()));
            aluno.setNome(txtNome.getText().toString());
            aluno.setCep(txtCep.getText().toString());
            aluno.setLogradouro(txtLogradouro.getText().toString());
            aluno.setComplemento(txtComplemento.getText().toString());
            aluno.setBairro(txtBairro.getText().toString());
            aluno.setCidade(txtCidade.getText().toString());
            aluno.setUf(txtUf.getText().toString());

            if (id == 0)
                inserirAluno(aluno);
            else {
                aluno.setRa(id);
                editarAluno(aluno);
            }
        });
    }

    private void inserirAluno(Aluno aluno) {
        Call<Aluno> call = apiService.postAluno(aluno);
        call.enqueue(new Callback<Aluno>() {
            @Override
            public void onResponse(Call<Aluno> call, retrofit2.Response<Aluno> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AlunoActivity.this, "Inserido com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("Inserir", "Erro ao criar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Aluno> call, Throwable t) {
                Log.e("Inserir", "Erro ao criar: " + t.getMessage());
            }
        });
    }

    private void editarAluno(Aluno aluno) {
        Call<Aluno> call = apiService.putAluno(id, aluno);
        call.enqueue(new Callback<Aluno>() {
            @Override
            public void onResponse(Call<Aluno> call, retrofit2.Response<Aluno> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AlunoActivity.this, "Editado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("Editar", "Erro ao editar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Aluno> call, Throwable t) {
                Log.e("Editar", "Erro ao editar: " + t.getMessage());
            }
        });
    }

    private void buscarEnderecoPorCep(String cep) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://viacep.com.br/ws/" + cep + "/json/")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("ViaCEP", "Erro ao buscar CEP", e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        JSONObject jsonObject = new JSONObject(json);
                        runOnUiThread(() -> {
                            try {
                                txtLogradouro.setText(jsonObject.getString("logradouro"));
                                txtComplemento.setText(jsonObject.getString("complemento"));
                                txtBairro.setText(jsonObject.getString("bairro"));
                                txtCidade.setText(jsonObject.getString("localidade"));
                                txtUf.setText(jsonObject.getString("uf"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
