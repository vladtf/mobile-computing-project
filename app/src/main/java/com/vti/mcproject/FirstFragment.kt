package com.vti.mcproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.vti.mcproject.databinding.FragmentFirstBinding

/**
 * A Fragment for managing a simple todo list.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var todoAdapter: TodoAdapter
    private val todoList = mutableListOf<TodoItem>()
    private var nextId = 1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupInputHandling()
        updateEmptyState()
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(
            onToggleComplete = { todo ->
                toggleTodoCompletion(todo)
            },
            onDeleteClick = { todo ->
                deleteTodo(todo)
            }
        )
        binding.recyclerTodos.adapter = todoAdapter
    }

    private fun setupInputHandling() {
        binding.buttonAdd.setOnClickListener {
            addTodo()
        }

        binding.editTodo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addTodo()
                true
            } else {
                false
            }
        }
    }

    private fun addTodo() {
        val text = binding.editTodo.text?.toString()?.trim()

        if (text.isNullOrEmpty()) {
            Snackbar.make(binding.root, R.string.enter_todo_text, Snackbar.LENGTH_SHORT).show()
            return
        }

        val newTodo = TodoItem(
            id = nextId++,
            text = text,
            isCompleted = false
        )

        todoList.add(0, newTodo) // Add to the beginning
        todoAdapter.submitList(todoList.toList())
        binding.editTodo.text?.clear()

        updateEmptyState()
        Snackbar.make(binding.root, R.string.todo_added, Snackbar.LENGTH_SHORT).show()
    }

    private fun toggleTodoCompletion(todo: TodoItem) {
        val index = todoList.indexOfFirst { it.id == todo.id }
        if (index != -1) {
            todoList[index].isCompleted = !todoList[index].isCompleted
            todoAdapter.submitList(todoList.toList())

            val message = if (todoList[index].isCompleted) {
                R.string.todo_completed
            } else {
                R.string.todo_uncompleted
            }
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun deleteTodo(todo: TodoItem) {
        val index = todoList.indexOfFirst { it.id == todo.id }
        if (index != -1) {
            todoList.removeAt(index)
            todoAdapter.submitList(todoList.toList())
            updateEmptyState()
            Snackbar.make(binding.root, R.string.todo_deleted, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateEmptyState() {
        binding.emptyText.isVisible = todoList.isEmpty()
        binding.recyclerTodos.isVisible = todoList.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}