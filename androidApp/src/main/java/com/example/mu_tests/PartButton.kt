import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mu_tests.MainActivity
import com.example.mu_tests.R
import com.example.mu_tests.SecondActivity
import com.example.mu_tests.ThirdActivity
import com.google.gson.Gson
import java.io.File

class PartButton(
    private val activity: MainActivity,
    private val context: Context,
    private var buttonList: List<Int>,
    private val onSelectionModeChanged: (Boolean) -> Unit // Callback to notify when selection mode changes
) : RecyclerView.Adapter<PartButton.ButtonViewHolder>() {

    val selectedItems = mutableSetOf<Int>()

    inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.itemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.part_item, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        if (selectedItems.contains(position)) {
            holder.button.background =
                ContextCompat.getDrawable(context, R.drawable.rectangle_button_green)
            holder.button.setTextColor(Color.WHITE)
        }
        else {
            holder.button.background =
                ContextCompat.getDrawable(context, R.drawable.rectangle_button)
            holder.button.setTextColor(Color.BLACK)
        }
        holder.button.text = "Тема ${buttonList[position] + 1}"
        holder.button.setOnClickListener {
            if (!selectedItems.contains(position)) {
                holder.button.background =
                    ContextCompat.getDrawable(context, R.drawable.rectangle_button_green)
                holder.button.setTextColor(Color.WHITE)
                selectedItems.add(position)
            } else {
                holder.button.background =
                    ContextCompat.getDrawable(context, R.drawable.rectangle_button)
                holder.button.setTextColor(Color.BLACK)
                selectedItems.remove(position)
            }
        }
    }

    override fun getItemCount() = buttonList.size
    fun add(f : Boolean) {
        if(!f) for (i in 0..24) {
            selectedItems.add(i)
        }
        else for (i in 25..59) {
            selectedItems.add(i)
        }
        notifyDataSetChanged()
    }
    fun remove(f : Boolean) {
        if(!f) for (i in 0..25) {
            selectedItems.remove(i)
        }
        else for (i in 26..59) {
            selectedItems.remove(i)
        }
        notifyDataSetChanged()
    }
    // Expose selected items for further actions
    fun getSelectedItems() = selectedItems.toList()

}