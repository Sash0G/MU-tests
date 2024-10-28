import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mu_tests.MainActivity
import com.example.mu_tests.R
import com.example.mu_tests.SecondActivity
import com.example.mu_tests.ThirdActivity

class ButtonAdapter(
    private val activity: ThirdActivity,
    private val context: Context,
    private val buttonList: List<String>, // Example data for buttons
    private val onSelectionModeChanged: (Boolean) -> Unit // Callback to notify when selection mode changes
) : RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder>() {

    private var isSelectionMode = false
    private val selectedItems = mutableSetOf<Int>()

    inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.itemButton)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.button_item, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.button.text = buttonList[position]

        // Show/hide checkbox based on selection mode
        holder.checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        holder.checkBox.isChecked = selectedItems.contains(position)

        // Long press to enter selection mode
        holder.button.setOnLongClickListener {
            if (!isSelectionMode) {
                isSelectionMode = true
                onSelectionModeChanged(true)
                notifyDataSetChanged()
            }
            true
        }

        // Click checkbox to select/deselect item
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(position)
            } else {
                selectedItems.remove(position)
            }
        }
        holder.button.text = "Result: ${activity.testList[position].result}/80"
        // Click button to toggle selection mode if it’s active
        holder.button.setOnClickListener {
            val intent = Intent(context, SecondActivity::class.java)
            intent.putExtra("test", activity.testList[position])
            startActivity(context,intent,null)
        }

        holder.button.setOnClickListener {
            val intent = Intent(context, SecondActivity::class.java)
            intent.putExtra("test", activity.testList[0])
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = buttonList.size

    // Expose selected items for further actions
    fun getSelectedItems() = selectedItems.toList()

    // Exit selection mode and reset
    fun exitSelectionMode() {
        isSelectionMode = false
        selectedItems.clear()
        notifyDataSetChanged()
    }
}