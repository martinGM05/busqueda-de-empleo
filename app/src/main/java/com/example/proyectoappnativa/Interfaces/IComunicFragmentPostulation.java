package com.example.proyectoappnativa.Interfaces;

import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.Models.Postulation;

public interface IComunicFragmentPostulation {

    public void sendPostulation(Postulation postulation);

    public void sendPostulationOff(PostulationOff postulationOff);

}
