package fr.neige_i.fdj_entretien

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.neige_i.fdj_entretien.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}