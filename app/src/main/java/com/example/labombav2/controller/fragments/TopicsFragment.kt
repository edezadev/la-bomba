package com.example.labombav2.controller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labombav2.R
import com.example.labombav2.controller.activities.SettingsActivity
import com.example.labombav2.controller.adapters.TopicAdapter
import com.example.labombav2.controller.dialogs.AddTopicBottomSheet
import com.example.labombav2.databinding.FragmentTopicsBinding
import com.example.labombav2.model.TopicModel
import com.example.labombav2.utils.FirebaseAuthManager
import com.example.labombav2.utils.OnTopicInsertedListener
import com.example.labombav2.utils.TopicDbManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TopicsFragment : Fragment(), OnTopicInsertedListener {
    private var binding: FragmentTopicsBinding? = null
    private var adapter: TopicAdapter? = null
    private lateinit var tvNoTopics: TextView
    private lateinit var recyclerTopic: RecyclerView
    private lateinit var fabAddTopic: FloatingActionButton
    private var listTopics: MutableList<TopicModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopicsBinding.inflate(inflater, container, false)
        val view = binding?.root
        val activity = activity as? SettingsActivity
        binding?.let {
            tvNoTopics = it.tvNoTopics
            recyclerTopic = it.recyclerTopic
            fabAddTopic = it.fabAddTopic
        }

        setupRecyclerView()

        activity?.updateView(this, getString(R.string.topics_name))

        fabAddTopic.setOnClickListener{showAddTopic()}
        return view
    }

    private fun setupRecyclerView() {
        adapter = TopicAdapter(listTopics)
        recyclerTopic.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerTopic.adapter = adapter
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