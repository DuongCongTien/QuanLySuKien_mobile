package com.group29.eventapp.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.group29.eventapp.R
import com.group29.eventapp.databinding.FragmentSplashBinding
import com.group29.eventapp.databinding.ItemOnboardingSlideBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    data class OnboardingSlide(
        val illustrationRes: Int,
        val title: String,
        val description: String
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val slides = listOf(
            OnboardingSlide(
                R.drawable.ic_onboarding_1,
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1)
            ),
            OnboardingSlide(
                R.drawable.ic_onboarding_2,
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2)
            ),
            OnboardingSlide(
                R.drawable.ic_onboarding_3,
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3)
            )
        )

        val adapter = OnboardingAdapter(slides)
        binding.viewPagerOnboarding.adapter = adapter

        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerOnboarding) { _, _ -> }.attach()

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == slides.size - 1) {
                    binding.btnContinue.text = getString(R.string.btn_start)
                } else {
                    binding.btnContinue.text = getString(R.string.btn_continue)
                }
            }
        })

        binding.btnSkip.setOnClickListener {
            navigateToLogin()
        }

        binding.btnContinue.setOnClickListener {
            val currentItem = binding.viewPagerOnboarding.currentItem
            if (currentItem < slides.size - 1) {
                binding.viewPagerOnboarding.currentItem = currentItem + 1
            } else {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_splash_to_login)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class OnboardingAdapter(
        private val items: List<OnboardingSlide>
    ) : RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder>() {

        class SlideViewHolder(val binding: ItemOnboardingSlideBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
            val binding = ItemOnboardingSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SlideViewHolder(binding)
        }

        override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
            val item = items[position]
            holder.binding.ivSlideIllustration.setImageResource(item.illustrationRes)
            holder.binding.tvSlideTitle.text = item.title
            holder.binding.tvSlideDesc.text = item.description
        }

        override fun getItemCount(): Int = items.size
    }
}
