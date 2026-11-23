package com.proyecto.gyrocampro.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.data.local.AppDatabase
import com.proyecto.gyrocampro.data.local.PhotoCaptureEntity
import com.proyecto.gyrocampro.databinding.DialogEditCaptureBinding
import com.proyecto.gyrocampro.databinding.FragmentGalleryCrudBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GalleryCrudFragment : Fragment() {
    private var _binding: FragmentGalleryCrudBinding? = null
    private val binding get() = _binding!!
    private val database by lazy { AppDatabase.getInstance(requireContext().applicationContext) }
    private lateinit var captureAdapter: PhotoCaptureAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryCrudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        captureAdapter = PhotoCaptureAdapter(
            onDetails = ::showDetails,
            onEdit = ::showEditDialog,
            onDelete = ::confirmDelete
        )
        binding.captureRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.captureRecycler.adapter = captureAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.photoCaptureDao().observeAll().collect { captures ->
                    captureAdapter.submitList(captures)
                    binding.galleryCount.text = getString(R.string.gallery_count, captures.size)
                    binding.galleryEmptyState.visibility = if (captures.isEmpty()) View.VISIBLE else View.GONE
                    binding.captureRecycler.visibility = if (captures.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun showDetails(capture: PhotoCaptureEntity) {
        val date = SimpleDateFormat(getString(R.string.date_pattern), Locale.getDefault())
            .format(Date(capture.capturedAt))
        val details = getString(
            R.string.capture_details_format,
            capture.title,
            capture.note.ifBlank { getString(R.string.no_note) },
            date,
            capture.tiltDegrees,
            capture.forceG,
            capture.gyroX,
            capture.gyroY,
            capture.gyroZ,
            capture.accelX,
            capture.accelY,
            capture.accelZ
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(capture.title)
            .setMessage(details)
            .setPositiveButton(R.string.cancel, null)
            .show()
    }

    private fun showEditDialog(capture: PhotoCaptureEntity) {
        val form = DialogEditCaptureBinding.inflate(layoutInflater)
        form.titleInput.setText(capture.title)
        form.noteInput.setText(capture.note)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_edit_title)
            .setView(form.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = form.titleInput.text?.toString()?.trim().orEmpty()
                val note = form.noteInput.text?.toString()?.trim().orEmpty()
                if (validateForm(form, title, note)) {
                    dialog.dismiss()
                    lifecycleScope.launch {
                        database.photoCaptureDao().update(capture.copy(title = title, note = note))
                        if (isAdded) Toast.makeText(requireContext(), R.string.capture_updated, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dialog.show()
    }

    private fun validateForm(form: DialogEditCaptureBinding, title: String, note: String): Boolean {
        form.titleInputLayout.error = null
        form.noteInputLayout.error = null
        if (title.isBlank()) {
            form.titleInputLayout.error = getString(R.string.field_title_required)
            return false
        }
        if (title.length > 40) {
            form.titleInputLayout.error = getString(R.string.field_title_too_long)
            return false
        }
        if (note.length > 280) {
            form.noteInputLayout.error = getString(R.string.field_note_too_long)
            return false
        }
        return true
    }

    private fun confirmDelete(capture: PhotoCaptureEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    database.photoCaptureDao().delete(capture)
                    File(capture.imagePath).delete()
                    if (isAdded) Toast.makeText(requireContext(), R.string.capture_deleted, Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        binding.captureRecycler.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
