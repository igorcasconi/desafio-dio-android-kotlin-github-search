package br.com.igorbag.githubsearch.ui.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var repositoryList: (Repository) -> Unit = {}
    var btnShareLister: (Repository) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textRepoName.text = repositories[position].name

        holder.btnRepo.setOnClickListener {
          btnShareLister(repositories[position])
        }

        holder.viewRepo.setOnClickListener {
          repositoryList(repositories[position])
        }
    }

    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
      val textRepoName: TextView
      val btnRepo: ImageView
      val viewRepo: ConstraintLayout

      init {
        view.apply {
          textRepoName = findViewById(R.id.tv_preco)
          btnRepo = findViewById<ImageView>(R.id.iv_favorite)
          viewRepo = findViewById(R.id.cl_card_content)
        }
      }
    }
}


