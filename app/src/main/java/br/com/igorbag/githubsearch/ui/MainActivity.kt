package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import androidx.core.content.edit
import androidx.core.view.isVisible
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService
    private val PREFS_NAME = "br.com.igorbag.githubsearch.PREFERENCE_FILE_KEY"
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        setupView()
        setupRetrofit()
        getAllReposByUserName()
        showUserName()
        setupListeners()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        nomeUsuario = findViewById<EditText>(R.id.et_nome_usuario)
        btnConfirmar = findViewById<Button>(R.id.btn_confirmar)
        listaRepositories = findViewById<RecyclerView>(R.id.rv_lista_repositories)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
          getAllReposByUserName()
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
      sharedPreferences.edit {
        putString("USERNAME", nomeUsuario.text.toString())
        apply()
      }

    }

    private fun showUserName() {
      val userSavedInfo = sharedPreferences.getString("USERNAME", null)
      if (!userSavedInfo.isNullOrEmpty()) nomeUsuario.setText(userSavedInfo)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        val instance = Retrofit.Builder().baseUrl("https://api.github.com/").addConverterFactory(GsonConverterFactory.create()).build()
        githubApi = instance.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        try {
          if (nomeUsuario.text.isNotEmpty()) {
            saveUserLocal()
            val call = githubApi.getAllRepositoriesByUser(nomeUsuario.text.toString())
            call.enqueue(object: Callback<List<Repository>> {
              override fun onResponse(
                call: Call<List<Repository>?>,
                response: Response<List<Repository>?>
              ) {
                if(response.isSuccessful) {
                  response.body()?.let { setupAdapter(it) }
                } else {
                  Toast.makeText(this@MainActivity, "Ocorreu um erro", Toast.LENGTH_LONG).show()
                }
              }

              override fun onFailure(call: Call<List<Repository>?>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Ocorreu um erro", Toast.LENGTH_LONG).show()
              }
            })
          }
        } catch (e: Exception) {
          Toast.makeText(this@MainActivity, "Ocorreu um erro", Toast.LENGTH_LONG).show()
        }
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
      var adapterRepo = RepositoryAdapter(list)
      listaRepositories.apply {
        isVisible = true
        adapter = adapterRepo
      }

      adapterRepo.btnShareLister = { repo -> shareRepositoryLink(repo.htmlUrl) }
      adapterRepo.repositoryList = { repo -> openBrowser(repo.htmlUrl) }
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}
