package com.proyecto.gyrocampro.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.proyecto.gyrocampro.R
import com.proyecto.gyrocampro.data.local.PhotoCaptureEntity
import com.proyecto.gyrocampro.databinding.ItemPhotoCaptureBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoCaptureAdapter(
    private val onDetails: (PhotoCaptureEntity) -> Unit,
    private val onEdit: (PhotoCaptureEntity) -> Unit,
    private val onDelete: (PhotoCaptureEntity) -> Unit
) : ListAdapter<PhotoCaptureEntity, PhotoCaptureAdapter.PhotoCaptureViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCaptureViewHolder {
        val binding = ItemPhotoCaptureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoCaptureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoCaptureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PhotoCaptureViewHolder(
        private val binding: ItemPhotoCaptureBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PhotoCaptureEntity) {
            binding.photoTitle.text = item.title
            binding.photoThumbnail.contentDescription = item.title
            val date = SimpleDateFormat(
                binding.root.context.getString(R.string.date_pattern),
                Locale.getDefault()
            ).format(Date(item.capturedAt))
            binding.photoDate.text = date
            binding.photoTelemetry.text = binding.root.context.getString(
                R.string.item_telemetry_format,
                item.tiltDegrees,
                item.forceG
            )
            val imageFile = File(item.imagePath)
            if (imageFile.exists()) {
                binding.photoThumbnail.load(imageFile) {
                    crossfade(true)
                }
            } else {
                binding.photoThumbnail.setImageResource(com.proyecto.gyrocampro.R.drawable.ic_gallery)
            }
            binding.buttonDetails.setOnClickListener { onDetails(item) }
            binding.buttonEdit.setOnClickListener { onEdit(item) }
            binding.buttonDelete.setOnClickListener { onDelete(item) }
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<PhotoCaptureEntity>() {
        override fun areItemsTheSame(oldItem: PhotoCaptureEntity, newItem: PhotoCaptureEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhotoCaptureEntity, newItem: PhotoCaptureEntity): Boolean {
            return oldItem == newItem
        }
    }
}
