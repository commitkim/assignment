package com.geonwoo.assignment.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.geonwoo.assignment.R
import com.geonwoo.assignment.databinding.FragmentTodoDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TodoDetailFragment : Fragment() {

    private var _binding: FragmentTodoDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setupViews()
        observeViewModel()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_delete -> {
                        showDeleteConfirmDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupViews() {
        binding.btnSave.setOnClickListener {
            val title = binding.editTitle.text?.toString() ?: ""
            val description = binding.editDescription.text?.toString() ?: ""
            viewModel.updateTodo(title, description)
        }

        binding.checkboxComplete.setOnClickListener {
            viewModel.toggleComplete()
        }
    }

    private fun observeViewModel() {
        viewModel.todo.observe(viewLifecycleOwner) { todo ->
            todo?.let {
                binding.editTitle.setText(it.title)
                binding.editDescription.setText(it.description)
                binding.checkboxComplete.isChecked = it.isCompleted
                binding.tvCreatedAt.text = formatDate(it.createdAt)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                viewModel.onNavigateBackHandled()
                findNavController().popBackStack()
            }
        }
    }

    private fun showDeleteConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("이 할 일을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                viewModel.deleteTodo()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREA)
        return "생성일: ${dateFormat.format(Date(timestamp))}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
