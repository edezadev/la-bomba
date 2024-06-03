package com.example.labombav2.controllers.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.labombav2.R
import com.example.labombav2.controllers.activities.SettingsActivity
import com.example.labombav2.controllers.adapters.PageTopicsAdapter
import com.example.labombav2.controllers.dialogs.AddTopicBottomSheet
import com.example.labombav2.databinding.FragmentTopicsBinding
import com.example.labombav2.models.TopicModel
import com.example.labombav2.config.auth.FirebaseAuthManager
import com.example.labombav2.utils.listeners.OnTopicInsertedListener
import com.example.labombav2.config.database.TopicDbManager
import com.example.labombav2.controllers.adapters.PageIndicatorAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration

class TopicsFragment : Fragment(), OnTopicInsertedListener {
    private var binding: FragmentTopicsBinding? = null
    private var pageTopicsAdapter: PageTopicsAdapter? = null
    private var pageIndicatorAdapter: PageIndicatorAdapter? = null
    private lateinit var tvNoTopics: TextView
    private lateinit var recyclerPageIndicator: RecyclerView
    private lateinit var vpPageTopics: ViewPager2
    private lateinit var fabAddTopic: FloatingActionButton
    private var listPages: MutableList<MutableList<TopicModel>> = mutableListOf()

    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopicsBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity
        binding?.let {
            tvNoTopics = it.tvNoTopics
            recyclerPageIndicator = it.recyclerPageIndicator
            vpPageTopics = it.vpPageTopics
            fabAddTopic = it.fabAddTopic
        }

        getListPages()

        activity?.updateView(this, getString(R.string.topics_name))

        fabAddTopic.setOnClickListener{showAddTopic()}
        return view
    }

    private fun getListPages() {
        FirebaseAuthManager.getUid { uid ->
            listenerRegistration = TopicDbManager.getListPagesListener(uid) { pages ->
                listPages.clear() //limpiar antes de agregar, sirve para cuando se agrega un nuevo tema
                if (pages.size <= 0) {
                    tvNoTopics.visibility = View.VISIBLE
                } else {
                    tvNoTopics.visibility = View.GONE
                    listPages.addAll(pages)
                    setupViewPager()
                }
            }
        }
    }

    private fun setupViewPager() {
        pageTopicsAdapter = PageTopicsAdapter(listPages)
        vpPageTopics.adapter = pageTopicsAdapter

        vpPageTopics.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                addPageIndicators(position, listPages.size)
            }
        })
    }

    //  Método para crear los indicadores de la página
    private fun addPageIndicators(currentPage: Int, totalPages: Int) {
        recyclerPageIndicator.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        pageIndicatorAdapter = PageIndicatorAdapter(currentPage, totalPages) { page ->
            vpPageTopics.setCurrentItem(page, true)
        }
        recyclerPageIndicator.adapter = pageIndicatorAdapter
    }

    private fun showAddTopic() {
        val bottomSheet = AddTopicBottomSheet()
        /* Antes de mostrar el bottomSheet, asignar el fragmento actual como listener, para
        * establecer la comunicación entre el bottomSheet con el fragmento al insertar datos, a
        * través de la interfaz */
        bottomSheet.setOnDataInsertedListener(this)
        bottomSheet.show(parentFragmentManager.beginTransaction(), AddTopicBottomSheet.TAG)
    }

    override fun onTopicInserted(newTopic: TopicModel) {
        FirebaseAuthManager.getUid { uid ->
            TopicDbManager.createTopic(uid, newTopic)
        }
    }

    override fun onStop() {
        super.onStop()
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
        }
    }
}