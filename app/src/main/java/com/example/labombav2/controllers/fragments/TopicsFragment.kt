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
import com.example.labombav2.utils.listeners.OnCurrentPageListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration

class TopicsFragment : Fragment(), OnTopicInsertedListener {
    private var binding: FragmentTopicsBinding? = null
    private var pageTopicsAdapter: PageTopicsAdapter? = null
    private var pageIndicatorAdapter: PageIndicatorAdapter? = null
    private lateinit var btnNext: MaterialButton
    private lateinit var tvNoTopics: TextView
    private lateinit var recyclerPageIndicator: RecyclerView
    private lateinit var vpPageTopics: ViewPager2
    private lateinit var fabAddTopic: FloatingActionButton
    private var listPages: MutableList<MutableList<TopicModel>> = mutableListOf()

    private lateinit var listenerRegistration: ListenerRegistration
    private var changeListener: OnCurrentPageListener? = null

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

        activity?.let{
            it.updateView(this, getString(R.string.topics_name))
            btnNext = it.findViewById(R.id.btnNext)
        }

        btnNext.setOnClickListener { activity?.addFragment(TimerFragment()) }
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
                    addPageIndicators(0,listPages.size)
                }
            }
        }
    }

    private fun setupViewPager() {
        pageTopicsAdapter = PageTopicsAdapter(listPages, parentFragmentManager)
        vpPageTopics.adapter = pageTopicsAdapter

        vpPageTopics.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//              Permite cambiar visualmente el indicador de la página actual
                addPageIndicators(position, listPages.size)
//              Aqui se deberia cambiar el valor de currentpage para que sea escuchado en el adapter
                changeListener?.onCurrentPageChange(position)
            }
        })
    }

    //  Métodoo para crear los indicadores de la página
    private fun addPageIndicators(currentPage: Int, totalPages: Int) {
        recyclerPageIndicator.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        pageIndicatorAdapter = PageIndicatorAdapter(currentPage, totalPages) { numPage ->
//          Cambiar la página de acuerdo a la posición del indicador que se hizo click
            vpPageTopics.setCurrentItem(numPage, true)
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
    /**
     * Crear interfaz para listener
     * Aqui crear metodo de configruacion para listener, variable para instanciar al metodo de la interfaz enviando el nuevo dato
     * que en este caso seria el nuevo currentPage
     * En el adapter extender de la interfaz, lo que implementará el metodo de ésta y ahi manejar el valor de currentPage, ademas crear un
     * objeto de este fragment y llamar al metodo de configuracion */
    fun setOnDataChangeListener(listener: OnCurrentPageListener) {
        this.changeListener = listener
    }

    override fun onStop() {
        super.onStop()
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
        }
    }
}