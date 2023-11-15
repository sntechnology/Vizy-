package com.example.teste100;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar TextToSpeech
        textToSpeech = new TextToSpeech(this, this);

        Button button = findViewById(R.id.bt1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código para iniciar a nova atividade aqui
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });
        Button secondButton = findViewById(R.id.bt2);
        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código para iniciar outra janela aqui
                Intent intent = new Intent(MainActivity.this, Cadastro.class);
                startActivity(intent);
            }
        });
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
                String automaticPhrase = "Bem vindo, Fale 'Entrar' caso já possua uma conta ou 'Criar' caso queira criar uma.";
                // Falar a frase automaticamente
                textToSpeech.speak(automaticPhrase, TextToSpeech.QUEUE_FLUSH, null, null);

                // Iniciar o reconhecimento de voz após um pequeno atraso (2 segundos)
                final int delayMillis = 2000;
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                startVoiceRecognition();
                            }
                        },
                        delayMillis);
            }
        } else {
            Toast.makeText(this, "Falha ao inicializar o TextToSpeech", Toast.LENGTH_SHORT).show();
        }
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale 'Entrar' caso já possua uma conta ou 'Criar' caso queira criar uma");

        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size() > 0) {
                String spokenText = result.get(0).trim();

                if (spokenText.equalsIgnoreCase("Entrar")) {
                    // Redirecionar para a tela de login
                    Toast.makeText(this, "Redirecionando para a tela de login", Toast.LENGTH_SHORT).show();
                    // Adicione aqui o código para iniciar a atividade da tela de login
                    Intent loginIntent = new Intent(MainActivity.this, Login.class);
                    startActivity(loginIntent);
                } else if (spokenText.equalsIgnoreCase("Criar")) {
                    // Redirecionar para a tela de cadastro
                    Toast.makeText(this, "Redirecionando para a tela de cadastro", Toast.LENGTH_SHORT).show();
                    // Adicione aqui o código para iniciar a atividade da tela de cadastro
                    Intent cadastroIntent = new Intent(MainActivity.this, Cadastro.class);
                    startActivity(cadastroIntent);
                } else {
                    Toast.makeText(this, "Comando de voz inválido", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libere os recursos do TextToSpeech
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
