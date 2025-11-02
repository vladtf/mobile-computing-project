package com.vti.mcproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vti.mcproject.blockchain.MultiversXSdkService
import com.vti.mcproject.databinding.FragmentCreateElectionBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Fragment for creating a new election on the blockchain
 */
class CreateElectionFragment : Fragment() {

    private var _binding: FragmentCreateElectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var multiversXService: MultiversXSdkService

    private val startCalendar = Calendar.getInstance()
    private val endCalendar = Calendar.getInstance()
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateElectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        multiversXService = MultiversXSdkService()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Start date picker
        binding.buttonStartDate.setOnClickListener {
            showDatePicker(startCalendar) { calendar ->
                startCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                startCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                startCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                updateStartDateTime()
            }
        }

        // Start time picker
        binding.buttonStartTime.setOnClickListener {
            showTimePicker(startCalendar) { hour, minute ->
                startCalendar.set(Calendar.HOUR_OF_DAY, hour)
                startCalendar.set(Calendar.MINUTE, minute)
                updateStartDateTime()
            }
        }

        // End date picker
        binding.buttonEndDate.setOnClickListener {
            showDatePicker(endCalendar) { calendar ->
                endCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                endCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                endCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                updateEndDateTime()
            }
        }

        // End time picker
        binding.buttonEndTime.setOnClickListener {
            showTimePicker(endCalendar) { hour, minute ->
                endCalendar.set(Calendar.HOUR_OF_DAY, hour)
                endCalendar.set(Calendar.MINUTE, minute)
                updateEndDateTime()
            }
        }

        // Create election button
        binding.buttonCreateElection.setOnClickListener {
            createElection()
        }
    }

    private fun showDatePicker(calendar: Calendar, onDateSelected: (Calendar) -> Unit) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                onDateSelected(selectedCalendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Set minimum date to today
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePicker(calendar: Calendar, onTimeSelected: (Int, Int) -> Unit) {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                onTimeSelected(hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        ).show()
    }

    private fun updateStartDateTime() {
        binding.textStartDateTimeValue.text = dateTimeFormat.format(startCalendar.time)
    }

    private fun updateEndDateTime() {
        binding.textEndDateTimeValue.text = dateTimeFormat.format(endCalendar.time)
    }

    private fun createElection() {
        // Validate inputs
        val name = binding.editElectionName.text?.toString()?.trim()
        val description = binding.editElectionDescription.text?.toString()?.trim()

        if (name.isNullOrBlank()) {
            binding.layoutElectionName.error = getString(R.string.error_empty_name)
            return
        } else {
            binding.layoutElectionName.error = null
        }

        if (description.isNullOrBlank()) {
            binding.layoutElectionDescription.error = getString(R.string.error_empty_description)
            return
        } else {
            binding.layoutElectionDescription.error = null
        }

        // Check if dates are selected
        if (binding.textStartDateTimeValue.text == getString(R.string.no_date_selected)) {
            Snackbar.make(binding.root, R.string.error_no_start_date, Snackbar.LENGTH_LONG).show()
            return
        }

        if (binding.textEndDateTimeValue.text == getString(R.string.no_date_selected)) {
            Snackbar.make(binding.root, R.string.error_no_end_date, Snackbar.LENGTH_LONG).show()
            return
        }

        // Validate dates
        if (startCalendar.timeInMillis >= endCalendar.timeInMillis) {
            Snackbar.make(binding.root, R.string.error_invalid_dates, Snackbar.LENGTH_LONG).show()
            return
        }

        if (startCalendar.timeInMillis < System.currentTimeMillis()) {
            Snackbar.make(binding.root, R.string.error_past_start_date, Snackbar.LENGTH_LONG).show()
            return
        }

        // Get election type
        val electionType = when (binding.radioGroupElectionType.checkedRadioButtonId) {
            R.id.radio_single_choice -> MultiversXSdkService.ElectionType.SINGLE_CHOICE
            R.id.radio_multiple_choice -> MultiversXSdkService.ElectionType.MULTIPLE_CHOICE
            R.id.radio_ranked_choice -> MultiversXSdkService.ElectionType.RANKED_CHOICE
            else -> MultiversXSdkService.ElectionType.SINGLE_CHOICE
        }

        // Convert timestamps to seconds
        val startTimestamp = startCalendar.timeInMillis / 1000
        val endTimestamp = endCalendar.timeInMillis / 1000

        // Show progress
        binding.progressCreate.isVisible = true
        binding.buttonCreateElection.isEnabled = false
        binding.buttonCreateElection.text = getString(R.string.creating_election)

        // Create election
        viewLifecycleOwner.lifecycleScope.launch {
            // Note: This is a placeholder - requires wallet PEM file
            // In production, you would need to:
            // 1. Store wallet securely (e.g., encrypted SharedPreferences or KeyStore)
            // 2. Prompt user for wallet password
            // 3. Pass wallet PEM to registerElection
            
            val walletPem = "" // Placeholder - needs actual implementation
            
            multiversXService.registerElection(
                name = name,
                description = description,
                electionType = electionType,
                startTime = startTimestamp,
                endTime = endTimestamp,
                walletPem = walletPem
            ).fold(
                onSuccess = { txHash ->
                    binding.progressCreate.isVisible = false
                    binding.buttonCreateElection.isEnabled = true
                    binding.buttonCreateElection.text = getString(R.string.create_election)
                    
                    Snackbar.make(
                        binding.root,
                        getString(R.string.election_created_success),
                        Snackbar.LENGTH_LONG
                    ).show()
                    
                    // Navigate back after successful creation
                    findNavController().navigateUp()
                },
                onFailure = { error ->
                    binding.progressCreate.isVisible = false
                    binding.buttonCreateElection.isEnabled = true
                    binding.buttonCreateElection.text = getString(R.string.create_election)
                    
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_creating_election, error.message ?: getString(R.string.unknown_error)),
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
