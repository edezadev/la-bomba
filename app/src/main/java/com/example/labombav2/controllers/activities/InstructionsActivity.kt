package com.example.labombav2.controllers.activities

import android.os.Bundle
import android.text.Html
import android.view.KeyEvent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.labombav2.R
import com.example.labombav2.controllers.adapters.InstructionsAdapter
import com.example.labombav2.controllers.fragments.SliderFragment
import com.example.labombav2.databinding.ActivityInstructionsBinding
import com.example.labombav2.utils.BaseActivity
import com.example.labombav2.utils.Constants

class InstructionsActivity : BaseActivity() {
    private var binding: ActivityInstructionsBinding? = null

    private lateinit var viewPager: ViewPager2
    private lateinit var layoutDots: LinearLayout
    private lateinit var adapter: InstructionsAdapter
    private val dots: MutableList<TextView> = mutableListOf()
    private val titles = mutableListOf<String>()
    private val images = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstructionsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.let {
            viewPager = it.viewPager
            layoutDots = it.layoutDots
        }

        infoOfSlides()
        addDots(0) //Empieza indicando el slide 0
        setupViewPager()
    }

    private fun infoOfSlides() {
        titles.addAll(listOf(
            getString(R.string.instruction_penalty),
            getString(R.string.instruction_players),
            getString(R.string.instruction_topics),
            getString(R.string.instruction_time),
            getString(R.string.instruction_game)
        ))

        images.addAll(listOf(
            R.drawable.ic_penalty,
            R.drawable.ic_add_players,
            R.drawable.ic_selection_topics,
            R.drawable.ic_time,
            R.drawable.ic_pass_bomb
        ))
    }

    private fun addDots(currentSlide: Int) {
        layoutDots.removeAllViews() //Eliminar todos los views, para así reiniciar todoo el proceso

        for (i in 0..4) {
            dots.add(TextView(this))//se crean los puntos como TextView
            //crear los puntos con un codigo HTML
            dots[i].text = Html.fromHtml(Constants.COD_HTML_DOTS, Html.FROM_HTML_MODE_COMPACT)
            dots[i].textSize = resources.getInteger(R.integer.size_dots).toFloat()

            if (i == currentSlide) {
                dots[i].setTextColor(getColor(R.color.primary))
            } else {
                dots[i].setTextColor(getColor(R.color.onSurface))
            }

            layoutDots.addView(dots[i]) //agregar el textView en el linearLayout
        }
    }

    private fun setupViewPager() {
        adapter = InstructionsAdapter(fragmentActivity = this)
        for (i in titles.indices) {
            /*Primero se agrga el SliderFrgament (es la vista de cada slide) al adapter
            * el método newInstance crea cada fragment(slide), con los datos de los array*/
            adapter.addFragment(setupFragment(titles[i], images[i]))
        }
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            /* Sobreescribir este método para poder saber en que página se ecnuentra el viewPeger e ir
             * cambiando el estado de los indicadores */
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                addDots(position)
            }
        })
    }

    /*Crear una instancia diferente de SliderFragment(Vista de los slides), configurando cada
    * instancia o fragment de acuerdo a cada slide necesario*/
    private fun setupFragment(title: String, image: Int): SliderFragment {
        val bundle = Bundle()
        bundle.putString(Constants.TITLE, title)
        bundle.putInt(Constants.IMAGE, image)

//      Se envía los datos a SliderFragment
        val fragment = SliderFragment()
        fragment.arguments = bundle
        return fragment
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}