package com.geonwoo.assignment.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.geonwoo.assignment.databinding.FragmentTodoListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoListFragment : Fragment() {

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoListViewModel by viewModels()

    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(
            onItemClick = { todo ->
                val action = TodoListFragmentDirections.actionListToDetail(todo.id)
                findNavController().navigate(action)
            },
            onCheckChanged = { todo ->
                viewModel.toggleTodoComplete(todo)
            },
            onDeleteClick = { todo ->
                showDeleteConfirmDialog(todo)
            }
        )

        binding.recyclerView.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAddTodoDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.todos.observe(viewLifecycleOwner) { todos ->
            todoAdapter.submitList(todos)
            binding.emptyView.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun showAddTodoDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.geonwoo.assignment.R.layout.dialog_add_todo, null)
        
        val titleInput = dialogView.findViewById<TextInputEditText>(com.geonwoo.assignment.R.id.editTitle)
        val descInput = dialogView.findViewById<TextInputEditText>(com.geonwoo.assignment.R.id.editDescription)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("새 할 일 추가")
            .setView(dialogView)
            .setPositiveButton("추가") { _, _ ->
                val title = titleInput.text?.toString() ?: ""
                val description = descInput.text?.toString() ?: ""
                viewModel.addTodo(title, description)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showDeleteConfirmDialog(todo: com.geonwoo.assignment.domain.model.Todo) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("'${todo.title}'을(를) 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                viewModel.deleteTodo(todo)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
