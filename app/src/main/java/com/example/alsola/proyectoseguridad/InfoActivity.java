package com.example.alsola.proyectoseguridad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private List<Person> persons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        try {



        initializeData();
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        RVAdapter adapter = new RVAdapter(persons);
        rv.setAdapter(adapter);
    }catch (Exception e ){
            Log.e("EROOOR: " , e.getMessage());

    }
    }

    private void initializeData(){
        persons = new ArrayList<>();
        persons.add(new Person("Universidad Tecnológica de Panamá", "La Universidad Tecnológica de Panamá (UTP) es una institución estatal, cuyo Campus Central está ubicado en la ciudad de Panamá, República de Panamá.", R.drawable.utp1));
        persons.add(new Person("Jose Alsola", "Nombre Completo: Jose Andres Alsola Villamonte\n|", R.drawable.estatua_de_cera_de_luffy));
        persons.add(new Person("Buddy Cheung", "Buddy Cheung: ", R.drawable.buddy));
    }
}
