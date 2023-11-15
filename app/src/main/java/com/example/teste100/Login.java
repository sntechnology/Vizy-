package com.example.teste100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Login extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 1;
    private EditText usernameEditText;
    private Handler handler;
    private TextToSpeech textToSpeech;
    private boolean isSpeechEnabled = true;

    private List<String> forbiddenWords = Arrays.asList("Agora", "favor", "informe", "a", "sua", "senha"); // Adicione mais palavras proibidas, se necessário

    private boolean isUsernameDetected = false;

    private boolean isPasswordDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        handler = new Handler();
        usernameEditText = findViewById(R.id.username);

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
                    speakAndStartTimer("Login, favor informe seu username", 7900);
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
                if (text.equals("Login, favor informe seu username")) {
                    startSpeechToText(); // Ativar o microfone para o username
                } else if (text.equals("Agora, favor informe a sua senha")) {
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

        // Verificar se o nome de usuário já foi falado
        if (usernameEditText.getText().toString().isEmpty()) {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale seu nome de usuário");
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

                if (!isUsernameDetected) {
                    // Preencher o campo de username
                    usernameEditText.setText(spokenText);
                    speakAndStartTimer("Agora, favor informe a sua senha", 8000);
                    isUsernameDetected = true;
                } else {
                    // Verificar se o campo de senha já está preenchido
                    EditText passwordEditText = findViewById(R.id.senha);
                    if (passwordEditText.getText().toString().isEmpty()) {
                        String password = convertToNumbers(spokenText); // Converter para números
                        passwordEditText.setText(password);
                        isPasswordDetected = true; // Atualizar a flag de detecção de senha

                        // Verificar se ambos os campos foram preenchidos
                        if (isUsernameDetected && isPasswordDetected) {
                            // Chamar a próxima atividade
                            Intent nextIntent = new Intent(Login.this, Menu.class);
                            startActivity(nextIntent);
                        }
                    }
                }
            }
        }
    }


    private String convertToNumbers(String text) {
        String[] words = text.toLowerCase().split(" ");
        StringBuilder password = new StringBuilder();

        Map<String, String> numberMapping = new HashMap<>();
        numberMapping.put("zero", "0");
        numberMapping.put("um", "1");
        numberMapping.put("dois", "2");
        numberMapping.put("três", "3");
        numberMapping.put("quatro", "4");
        numberMapping.put("quinta", "5");
        numberMapping.put("seis", "6");
        numberMapping.put("sete", "7");
        numberMapping.put("oito", "8");
        numberMapping.put("nove", "9");

        // Adicione mais mapeamentos de palavras para números, se necessário

        for (String word : words) {
            if (numberMapping.containsKey(word)) {
                password.append(numberMapping.get(word));
            } else {
                password.append(word);
            }
        }

        return password.toString();
    }
}