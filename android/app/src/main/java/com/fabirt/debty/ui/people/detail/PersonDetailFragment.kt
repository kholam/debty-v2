package com.fabirt.debty.ui.people.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.fabirt.debty.NavGraphDirections
import com.fabirt.debty.databinding.FragmentPersonDetailBinding
import com.fabirt.debty.util.applyNavigationBarBottomInset
import com.fabirt.debty.util.applyNavigationBarBottomMargin
import com.fabirt.debty.util.applyStatusBarTopInset
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonDetailFragment : Fragment() {

    private var _binding: FragmentPersonDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PersonDetailFragmentArgs by navArgs()
    private val viewModel: PersonDetailViewModel by viewModels()
    private lateinit var adapter: MovementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MovementAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyWindowInsets()

        binding.rvMovements.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (itemCount == 1) binding.rvMovements.scrollToPosition(0)
            }
        })

        binding.btnNewMovement.setOnClickListener {
            navigateToNewMovement()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.requestPerson(args.personId).collect { person ->
                binding.tvName.text = person?.name ?: ""
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.requestMovements(args.personId).collect {
                adapter.submitList(it)
            }
        }
    }

    private fun navigateToNewMovement() {
        val action = NavGraphDirections.actionGlobalCreateMovementFragment(args.personId.toString())
        findNavController().navigate(action)
    }

    private fun applyWindowInsets() {
        binding.tvName.applyStatusBarTopInset()
        binding.btnNewMovement.applyNavigationBarBottomMargin(16)
        binding.rvMovements.applyNavigationBarBottomInset(80)
    }
}