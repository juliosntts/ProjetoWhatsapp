package com.juliosantos.projetowhatsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.juliosantos.projetowhatsapp.databinding.ActivityRegisterBinding
import com.juliosantos.projetowhatsapp.utils.showMessage

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializateToolbar()
        inicializateClickEvents()
    }

    private fun inicializateClickEvents() {
        binding.btnRegister.setOnClickListener {
            if (validateFields()) {
                registerUser( name, email, password )
            }
        }
    }

    private fun validateFields(): Boolean {
        val fields = listOf(
            binding.textInputLayoutName to "Preencha o seu nome!",
            binding.textInputLayoutEmail to "Preencha o seu e-mail!",
            binding.textInputLayoutPassword to "Preencha a sua senha!"
        )

        for ((inputLayout, errorMessage) in fields) {
            val editText = inputLayout.editText
            val value = editText?.text.toString()

            if (value.isEmpty()) {
                inputLayout.error = errorMessage
                return false
            } else {
                inputLayout.error = null
            }
        }

        return true
    }

    private fun registerUser(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                showMessage("Sucesso ao fazer seu cadastro!")
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else {
                showMessage("Erro ao cadastrar usuário!")
            }
        }.addOnFailureListener { exception ->
            try {
                throw exception
            }
            catch (errorPasswordWeak: FirebaseAuthWeakPasswordException) {
                errorPasswordWeak.printStackTrace()
                showMessage("Senha fraca, digite outra com letras, números e caracteres especiais!")
            }
            catch (errorExistingUser: FirebaseAuthUserCollisionException) {
                errorExistingUser.printStackTrace()
                showMessage("E-mail já cadastrado!")
            }
            catch (errorCredencials: FirebaseAuthInvalidCredentialsException) {
                errorCredencials.printStackTrace()
                showMessage("E-mail inválido!")
            }
        }
    }

    private fun inicializateToolbar() {

        val toolbar = binding.tbMainn
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }

}