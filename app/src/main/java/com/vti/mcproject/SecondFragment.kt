package com.vti.mcproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.vti.mcproject.blockchain.MultiversXSdkService
import com.vti.mcproject.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch

/**
 * Fragment for displaying MultiversX blockchain data
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var multiversXService: MultiversXSdkService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        multiversXService = MultiversXSdkService()

        setupRecyclerView()
        setupClickListeners()

        // Load initial data
        loadAccountData()
        loadTransactions()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.recyclerTransactions.adapter = transactionAdapter
    }

    private fun setupClickListeners() {
        binding.buttonRefresh.setOnClickListener {
            loadAccountData()
            loadTransactions()
        }
    }

    private fun loadAccountData() {
        binding.progressAccount.isVisible = true

        viewLifecycleOwner.lifecycleScope.launch {
            multiversXService.getAccountInfo(MultiversXSdkService.CONTRACT_ADDRESS).fold(
                onSuccess = { accountInfo ->
                    binding.progressAccount.isVisible = false
                    binding.textAccountAddress.text = accountInfo.address
                    binding.textBalance.text = "${accountInfo.balance} EGLD"
                    binding.textNonce.text = accountInfo.nonce.toString()
                },
                onFailure = { error ->
                    binding.progressAccount.isVisible = false
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_loading_account, error.message),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    private fun loadTransactions() {
        binding.progressTransactions.isVisible = true
        binding.recyclerTransactions.isVisible = false
        binding.textNoTransactions.isVisible = false

        viewLifecycleOwner.lifecycleScope.launch {
            multiversXService.getTransactions(MultiversXSdkService.CONTRACT_ADDRESS).fold(
                onSuccess = { transactions ->
                    binding.progressTransactions.isVisible = false

                    if (transactions.isEmpty()) {
                        binding.textNoTransactions.isVisible = true
                        binding.textNoTransactions.text = getString(R.string.no_transactions_found)
                        binding.recyclerTransactions.isVisible = false
                        binding.textTxCount.text = "(0)"
                    } else {
                        binding.textNoTransactions.isVisible = false
                        binding.recyclerTransactions.isVisible = true
                        transactionAdapter.submitList(transactions)
                        binding.textTxCount.text = "(${transactions.size})"
                    }
                },
                onFailure = { error ->
                    binding.progressTransactions.isVisible = false
                    binding.textNoTransactions.isVisible = true
                    binding.textNoTransactions.text = getString(R.string.unable_to_load_transactions)
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_generic, error.message ?: getString(R.string.unknown_error)),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}