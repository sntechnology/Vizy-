package com.example.teste100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class  Cadastro extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 1;
    private EditText nomeCompletoEditText;
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Handler handler;
    private TextToSpeech textToSpeech;
    private boolean isSpeechEnabled = true;

    private boolean isNomeCompletoDetected = false;
    private boolean isEmailDetected = false;
    private boolean isUsernameDetected = false;
    private boolean isPasswordDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        handler = new Handler();
        nomeCompletoEditText = findViewById(R.id.nome_completo);
        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.senha);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isSpeechEnabled = false;
                startSpeechToText();
            }
        }, 5000);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    speakAndStartTimer("Login, favor informe seu nome completo", 7900);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void speakAndStartTimer(String text, long delayMillis) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (text.equals("Cadastro, favor informe seu nome completo")) {
                    startSpeechToText(); // Ativar o microfone para o nome completo
                } else if (text.equals("Agora, favor informe seu email")) {
                    if (!isEmailDetected) {
                        startSpeechToText(); // Ativar o microfone para o email, apenas se ainda não tiver sido detectado
                    }
                } else if (text.equals("Agora, favor informe seu username")) {
                    if (!isUsernameDetected) {
                        startSpeechToText(); // Ativar o microfone para o username, apenas se ainda não tiver sido detectado
                    }
                } else if (text.equals("Agora, favor informe sua senha")) {
                    if (!isPasswordDetected) {
                        startSpeechToText(); // Ativar o microfone para a senha, apenas se ainda não tiver sido detectada
                    }
                } else if (isSpeechEnabled) {
                    startSpeechToText();
                }
            }
        }, delayMillis);
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // Verificar qual campo deve ser preenchido
        if (nomeCompletoEditText.getText().toString().isEmpty()) {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale seu nome completo");
        } else if (emailEditText.getText().toString().isEmpty()) {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale seu email");
        } else if (usernameEditText.getText().toString().isEmpty()) {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale seu username");
        } else {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale sua senha");
        }

        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);

                if (!isNomeCompletoDetected) {
                    nomeCompletoEditText.setText(spokenText);
                    speakAndStartTimer("Agora, favor informe seu email", 8000);
                    isNomeCompletoDetected = true;
                } else if (!isEmailDetected) {
                    emailEditText.setText(spokenText);
                    speakAndStartTimer("Agora, favor informe seu username", 8000);
                    isEmailDetected = true;
                } else if (!isUsernameDetected) {
                    usernameEditText.setText(spokenText);
                    speakAndStartTimer("Agora, favor informe sua senha", 8000);
                    isUsernameDetected = true;
                } else {
                    passwordEditText.setText(spokenText);
                    isPasswordDetected = true;

                    if (isNomeCompletoDetected && isEmailDetected && isUsernameDetected && isPasswordDetected) {
                        Intent nextIntent = new Intent(Cadastro.this, Menu.class);
                        startActivity(nextIntent);
                    }
                }
            }
        }
    }
}


