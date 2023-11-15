package com.example.teste100;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class recarga extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    private TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recarga);

        // Inicializar o objeto TextToSpeech
        textToSpeech = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Definir o idioma do TextToSpeech para o idioma padrão do dispositivo
            int result = textToSpeech.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Idioma não suportado", Toast.LENGTH_SHORT).show();
            } else {
                // Frase automática
                String automaticPhrase = "Fale 1 para fazer uma recarga na TIM;Fale 2 para fazer uma recarga na CLARO;Fale 3 para fazer uma recarga na VIVO";
                // Falar a frase automaticamente
                textToSpeech.speak(automaticPhrase, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        } else {
            Toast.makeText(this, "Falha ao inicializar o TextToSpeech", Toast.LENGTH_SHORT).show();
        }
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "\"Fale 1 para fazer uma recarga na TIM;Fale 2 para fazer uma recarga na CLARO;Fale 3 para fazer uma recarga na VIVO\"");

        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            // Processar os resultados do reconhecimento de voz, se necessário
        }
    }
}