import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sesac.developer_study_platform.data.Repository
import com.sesac.developer_study_platform.databinding.ItemRepositoryBinding

class RepositoryAdapter(private var repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    fun updateData(newRepositories: List<Repository>) {
        repositories = newRepositories
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemRepositoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repository: Repository) {
            binding.tvRepositoryName.text = repository.name
            binding.tvRepositoryLanguage.text = repository.language ?: "Unknown"
            binding.tvRepositoryStars.text = repository.starsCount.toString()
            binding.tvRepositoryFork.text = repository.forksCount.toString()
            binding.tvRepositoryIssue.text = repository.issuesCount.toString()
            binding.tvRepositoryCreatedAt.text = repository.createdAt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepositoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repositories[position]
        holder.bind(repository)
    }

    override fun getItemCount(): Int {
        return repositories.size
    }
}