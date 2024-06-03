package com.example.labombav2.controllers.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TopicsFragment : Fragment(), OnTopicInsertedListener {
    private var binding: FragmentTopicsBinding? = null
    private var adapter: PageTopicsAdapter? = null
    private lateinit var tvNoTopics: TextView
    private lateinit var pageIndicator: LinearLayout
    private lateinit var page: ViewPager2
    private lateinit var fabAddTopic: FloatingActionButton
    private var listPages: MutableList<MutableList<TopicModel>> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopicsBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity
        binding?.let {
            tvNoTopics = it.tvNoTopics
            pageIndicator = it.pageIndicator
            page = it.page
            fabAddTopic = it.fabAddTopic
        }

        getListPages()

        activity?.updateView(this, getString(R.string.topics_name))

        fabAddTopic.setOnClickListener{showAddTopic()}
        return view
    }

    private fun getListPages() {
        FirebaseAuthManager.getUid { uid ->
            TopicDbManager.getListPages(uid) { pages ->
                listPages.addAll(pages)
                setupViewPager()
                if (pages.size <= 0) {
                    tvNoTopics.visibility = View.VISIBLE
                } else {
                    tvNoTopics.visibility = View.GONE
                }
            }
        }
    }

    private fun setupViewPager() {
        adapter = PageTopicsAdapter(listPages)
        page.adapter = adapter

        page.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                addPageIndicators(position, listPages.size)
            }
        })
    }

    //  Método para crear los indicadores de la página
    private fun addPageIndicators(currentPage: Int, totalPages: Int) {
        //Eliminar indicadores anteriores
        pageIndicator.removeAllViews()

        //Crear indicadores para cada página
        for (i in 0 until totalPages) {
            val textView = TextView(requireContext())
            textView.text = (i + 1).toString() //Mostrar número de página
            textView.setPadding(16, 0, 16, 0)
            textView.textSize = 20f
//          Manejar el color de los indicadores segun la página actual
            if (i == currentPage){
                textView.setTextColor(resources.getColor(R.color.onSurface, activity?.theme))
            } else {
                textView.setTextColor(resources.getColor(R.color.primary, activity?.theme))
            }
            textView.setOnClickListener {
                page.setCurrentItem(i, true)
            }
            //cambiar página al hacer click
            pageIndicator.addView(textView)
        }
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
}