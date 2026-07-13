package com.edeza.labomba.controllers.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.edeza.labomba.R
import com.edeza.labomba.controllers.activities.SettingsActivity
import com.edeza.labomba.controllers.adapters.PageTopicsAdapter
import com.edeza.labomba.controllers.dialogs.AddTopicBottomSheet
import com.edeza.labomba.databinding.FragmentTopicsBinding
import com.edeza.labomba.models.TopicModel
import com.edeza.labomba.config.auth.FirebaseAuthManager
import com.edeza.labomba.utils.listeners.OnTopicInsertedListener
import com.edeza.labomba.config.database.TopicDbManager
import com.edeza.labomba.controllers.adapters.PageIndicatorAdapter
import com.edeza.labomba.utils.dismissLoading
import com.edeza.labomba.utils.listeners.OnCurrentPageListener
import com.edeza.labomba.utils.showLoading
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
    private var currentPage = 0
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

        recyclerPageIndicator.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        activity?.let{
            it.updateView(this, getString(R.string.topics_name))
            btnNext = it.findViewById(R.id.btnNext)
        }

        btnNext.setOnClickListener { activity?.addFragment(TimerFragment()) }
        fabAddTopic.setOnClickListener{showAddTopic()}
        return view
    }

    override fun onStart() {
        super.onStart()
        getListPages()
    }

    // Recupera los temas desde Firestore y procesa la paginación en memoria
    private fun getListPages() {
        showLoading()
        FirebaseAuthManager.getUid { uid ->
            listenerRegistration = TopicDbManager.getListPagesListener(uid) { pages ->
                dismissLoading()
                if (!isAdded) return@getListPagesListener

                listPages.clear()
                if (pages.isEmpty()) {
                    tvNoTopics.visibility = View.VISIBLE
                    setupViewPager() // Notificamos al ViewPager que la lista ahora está vacía
                } else {
                    tvNoTopics.visibility = View.GONE
                    listPages.addAll(pages)
                    setupViewPager()
                }
            }
        }
    }

    private fun setupViewPager() {
//        Por seguridad se verifica si el fragmento sigue vinculado a la Activity
        if (!isAdded || context == null) return

        pageTopicsAdapter = PageTopicsAdapter(listPages, childFragmentManager)
        vpPageTopics.adapter = pageTopicsAdapter

        /* Validación de seguridad: Si la página que recordamos ya no existe
        (ej. se borran temas y ahora hay menos páginas), volvemos a la última disponible.*/
        if (currentPage >= listPages.size) {
            currentPage = if (listPages.isNotEmpty()) listPages.size - 1 else 0
        }

        vpPageTopics.setCurrentItem(currentPage, false)
        updatePageIndicators(currentPage)

        vpPageTopics.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (isAdded) {
                    currentPage = position
//                  Permite cambiar visualmente el indicador de la página actual
                    updatePageIndicators(position)
                    changeListener?.onCurrentPageChange(position)
                }
            }
        })
    }

    private fun updatePageIndicators(currentPage: Int) {
        pageIndicatorAdapter = PageIndicatorAdapter(currentPage, listPages.size) { numPage ->
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
     * Registra un listener para detectar cambios en la navegación del ViewPager2.
     * Esta función actúa como un puente de comunicación (patrón Observer), permitiendo que
     * componentes externos —como el adapter o la activity— se suscriban a los eventos
     * de cambio de página. Esto es fundamental para sincronizar estados de la interfaz
     * o lógica de negocio que dependa de la posición actual del usuario dentro de la
     * lista paginada de temas.
     * @param listener Objeto que implementa [OnCurrentPageListener] para recibir las notificaciones.
     */
    fun setOnDataChangeListener(listener: OnCurrentPageListener) {
        this.changeListener = listener
    }

    override fun onStop() {
        super.onStop()
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        pageTopicsAdapter = null
        pageIndicatorAdapter = null
    }
}