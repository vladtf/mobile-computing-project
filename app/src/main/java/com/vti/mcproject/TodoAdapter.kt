package com.vti.mcproject

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vti.mcproject.databinding.ItemTodoBinding

/**
 * Adapter for displaying todo items in a RecyclerView.
 */
class TodoAdapter(
    private val onToggleComplete: (TodoItem) -> Unit,
    private val onDeleteClick: (TodoItem) -> Unit
) : ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding, onToggleComplete, onDeleteClick)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TodoViewHolder(
        private val binding: ItemTodoBinding,
        private val onToggleComplete: (TodoItem) -> Unit,
        private val onDeleteClick: (TodoItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: TodoItem) {
            binding.todoText.text = todo.text
            binding.todoCheckbox.isChecked = todo.isCompleted

            // Strike through completed items
            if (todo.isCompleted) {
                binding.todoText.paintFlags = binding.todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.todoText.paintFlags = binding.todoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            binding.todoCheckbox.setOnCheckedChangeListener { _, _ ->
                onToggleComplete(todo)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteClick(todo)
            }
        }
    }

    class TodoDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem == newItem
        }
    }
}
