package com.example.lab8_ibtissam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

/**
 * Travail Pratique : Programmation Asynchrone (Threads & AsyncTask)
 * Réalisé par : Ibtissam
 */
public class MainActivity extends AppCompatActivity {

    // 1) Références vers l'interface personnalisées pour Ibtissam
    private TextView txtStatusIbtissam;
    private ProgressBar progressBarIbtissam;
    private ImageView imgIbtissam;

    // 2) Handler lié au UI thread (Main thread) pour Ibtissam
    private Handler mainHandlerIbtissam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Gestion du design immersif pour le layout racine "mainIbtissam"
        View rootLayout = findViewById(R.id.mainIbtissam);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // A) Lier les vues XML au code Java
        txtStatusIbtissam = findViewById(R.id.txtStatusIbtissam);
        progressBarIbtissam = findViewById(R.id.progressBarIbtissam);
        imgIbtissam = findViewById(R.id.imgIbtissam);

        MaterialButton btnLoadIbtissam = findViewById(R.id.btnLoadIbtissam);
        MaterialButton btnCalcIbtissam = findViewById(R.id.btnCalcIbtissam);
        MaterialButton btnToastIbtissam = findViewById(R.id.btnToastIbtissam);

        // B) Créer le Handler qui poste sur le UI thread
        mainHandlerIbtissam = new Handler(Looper.getMainLooper());

        // C) Bouton Toast : vérifie que l'interface reste fluide pendant les traitements
        btnToastIbtissam.setOnClickListener(v ->
                Toast.makeText(getApplicationContext(), "Ibtissam : UI toujours fluide ! ✅", Toast.LENGTH_SHORT).show()
        );

        // D) Lancer un Thread pour charger une image sans bloquer
        btnLoadIbtissam.setOnClickListener(v -> chargerImageThreadIbtissam());

        // E) Lancer un calcul lourd avec AsyncTask
        btnCalcIbtissam.setOnClickListener(v -> new HeavyCalcTaskIbtissam().execute());
    }

    // ---------------------------------------------------------
    // PARTIE 1 : THREAD (Mise à jour via Handler)
    // ---------------------------------------------------------
    private void chargerImageThreadIbtissam() {

        // 1) Préparation de l'UI (UI thread)
        progressBarIbtissam.setVisibility(View.VISIBLE);
        progressBarIbtissam.setIndeterminate(true);
        txtStatusIbtissam.setText("Ibtissam : Chargement image (Thread)...");

        // 2) Créer un thread de fond (Worker Thread)
        new Thread(() -> {

            // 3) Simuler un travail long (ex: téléchargement réseau)
            try {
                Thread.sleep(2000); // 2 secondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 4) Charger l'image en arrière-plan
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

            // 5) Retour au UI thread via le Handler pour modifier les vues
            mainHandlerIbtissam.post(() -> {
                imgIbtissam.setImageBitmap(bitmap);
                progressBarIbtissam.setVisibility(View.INVISIBLE);
                txtStatusIbtissam.setText("Ibtissam : Image chargée avec succès ! ✅");
            });

        }).start(); // Démarrage du thread
    }

    // ---------------------------------------------------------
    // PARTIE 2 : ASYNCTASK (Gestion automatique des threads)
    // ---------------------------------------------------------
    private class HeavyCalcTaskIbtissam extends AsyncTask<Void, Integer, Long> {

        // Avant le traitement : UI thread
        @Override
        protected void onPreExecute() {
            progressBarIbtissam.setVisibility(View.VISIBLE);
            progressBarIbtissam.setIndeterminate(false);
            progressBarIbtissam.setProgress(0);
            txtStatusIbtissam.setText("Ibtissam : Calcul lourd en cours...");
        }

        // Traitement long : Worker thread
        @Override
        protected Long doInBackground(Void... voids) {
            long result = 0;

            for (int i = 1; i <= 100; i++) {
                // Simulation d'un calcul lourd
                for (int k = 0; k < 200000; k++) {
                    result += (i * k) % 7;
                }

                // Envoi de la progression au UI Thread
                publishProgress(i);

                try {
                    Thread.sleep(30); // Petite pause pour voir la barre progresser
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        // Mise à jour de la progression : UI thread
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBarIbtissam.setProgress(values[0]);
        }

        // Après le traitement : UI thread
        @Override
        protected void onPostExecute(Long result) {
            progressBarIbtissam.setVisibility(View.INVISIBLE);
            txtStatusIbtissam.setText("Ibtissam : Terminé ! Résultat = " + result);
        }
    }
}
