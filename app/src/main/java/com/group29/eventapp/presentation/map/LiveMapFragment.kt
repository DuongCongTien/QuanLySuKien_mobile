package com.group29.eventapp.presentation.map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.group29.eventapp.R
import com.group29.eventapp.data.mock.MockData
import com.group29.eventapp.data.model.MapMarkerModel
import com.group29.eventapp.databinding.DialogMapMarkerDetailBinding
import com.group29.eventapp.databinding.FragmentLiveMapBinding

class LiveMapFragment : Fragment() {

    private var _binding: FragmentLiveMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLiveMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupMarkers(MockData.sampleMapMarkers)
    }

    private fun setupMarkers(markers: List<MapMarkerModel>) {
        binding.layoutFloorPlan.post {
            val width = binding.layoutFloorPlan.width
            val height = binding.layoutFloorPlan.height

            markers.forEach { marker ->
                val markerView = MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                    text = marker.title.split(" ").firstOrNull() ?: "Marker"
                    textSize = 10f
                    minHeight = 36
                    setPadding(12, 4, 12, 4)
                    cornerRadius = 18
                    strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.color_primary)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_surface))
                    setOnClickListener {
                        showMarkerBottomSheet(marker)
                    }
                }

                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    leftMargin = (marker.xPercent * width - 60).toInt().coerceAtLeast(16)
                    topMargin = (marker.yPercent * height - 20).toInt().coerceAtLeast(40)
                }

                binding.layoutFloorPlan.addView(markerView, params)
            }
        }

        // Add Marker list
        binding.layoutMarkerList.removeAllViews()
        markers.forEach { marker ->
            val card = MaterialCardView(requireContext()).apply {
                radius = 12f
                strokeWidth = 1
                setStrokeColor(ContextCompat.getColor(requireContext(), R.color.color_outline))
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
                this.layoutParams = layoutParams

                val innerLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(24, 20, 24, 20)
                }

                val title = TextView(requireContext()).apply {
                    text = marker.title
                    textSize = 15f
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.color_on_surface))
                    paint.isFakeBoldText = true
                }

                val activity = TextView(requireContext()).apply {
                    text = marker.currentActivity
                    textSize = 13f
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.color_secondary))
                    setPadding(0, 8, 0, 0)
                }

                innerLayout.addView(title)
                innerLayout.addView(activity)
                addView(innerLayout)

                setOnClickListener { showMarkerBottomSheet(marker) }
            }
            binding.layoutMarkerList.addView(card)
        }
    }

    private fun showMarkerBottomSheet(marker: MapMarkerModel) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogMapMarkerDetailBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvMarkerTitle.text = marker.title
        dialogBinding.chipCategory.text = marker.category
        dialogBinding.tvMarkerActivity.text = marker.currentActivity

        dialogBinding.btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
