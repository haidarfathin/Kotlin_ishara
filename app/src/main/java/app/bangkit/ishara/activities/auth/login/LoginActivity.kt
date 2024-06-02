package app.bangkit.ishara.activities.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import app.bangkit.ishara.activities.main.MainActivity
import app.bangkit.ishara.R
import app.bangkit.ishara.activities.auth.register.RegisterActivity
import app.bangkit.ishara.activities.main.MainViewModel
import app.bangkit.ishara.data.preferences.UserPreference
import app.bangkit.ishara.data.preferences.dataStore
import app.bangkit.ishara.databinding.ActivityLoginBinding
import app.bangkit.ishara.utils.ViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(application.dataStore)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]

        loginViewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        loginViewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonLogin.setOnClickListener(this)
        binding.textViewButtonRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonLogin -> {
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()

                if (email.isEmpty()) {
                    binding.editTextEmail.error = "Email tidak boleh kosong"
                    return
                }

                if (password.isEmpty()) {
                    binding.editTextPassword.error = "Password tidak boleh kosong"
                    return
                }

                if (password.length < 8) {
                    binding.editTextPassword.error = "Password minimal 8 karakter"
                    return
                }

                loginViewModel.login(email, password)
            }

            R.id.textViewButtonRegister -> {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }
}