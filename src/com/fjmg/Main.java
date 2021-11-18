package com.fjmg;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Stack;

public class Main
{

    public static void main(String[] args)
    {
        ArrayList<Thread> jugadores  = new ArrayList<>();
        NumeroSecreto nsInicial = new NumeroSecreto(0,100);
        Partida partidaInicial = new Partida(nsInicial);
        for (int i = 0; i < 10; i++) {
            jugadores.add(new Thread(new Jugador("Jugador"+i,partidaInicial)));
        }
        while (true)
        {
            jugadores.forEach(jugador -> jugador.run());
        }
    }
    static class NumeroSecreto
    {
        int numero;
        int minimo;
        int maximo;
        public  NumeroSecreto(int minimo , int maximo)
        {
            this.minimo = minimo;
            this.maximo = maximo;
        }
        void generarNumeroSecreto()
        {
            numero = new Random().nextInt(minimo,maximo);
        }
        boolean Adivinar(int intento)
        {
            return this.numero == intento;
        }
    }
    static class Jugador implements Runnable {
        String nombre;
        Partida partida;
        int numero;

        public Jugador(String nombre,Partida partida) {
            this.nombre = nombre;
            this.partida = partida;
        }

        void generarNumero(int minimo, int maximo )
        {
            numero = new Random().nextInt(minimo,maximo);
        }

        @Override
        public void run()
        {
             generarNumero(0,100);
            partida.AdivinarNumeroSecreto(numero,this);
            generarNumero(0,100);
        }

    }
    static class Partida
    {
        int id;
        Jugador ganador;
        static Stack<Partida> partidas;
        final NumeroSecreto numeroSecreto;
        ArrayList<Jugador> jugadores;
        public Partida(NumeroSecreto numeroSecreto)
        {
            this.numeroSecreto = numeroSecreto;
            if (partidas == null)
            {
                partidas = new Stack<>();
            }
            partidas.add(this);
            id = partidas.size();
            jugadores = new ArrayList<>();
        }
        synchronized void AdivinarNumeroSecreto(int intento,Jugador jugador)
        {
            synchronized(numeroSecreto)
            {
                if (!jugadores.contains(jugador))
                {
                    jugadores.add(jugador);
                }

                if (numeroSecreto.Adivinar(intento) && ganador == null)
                {
                    ganador = jugador;
                    cambiarPartida(jugador,new Partida(new NumeroSecreto(numeroSecreto.minimo,numeroSecreto.maximo)));
                    imprimirResultado();
                    return;
                }
                if (ganador != null)
                {
                    imprimirResultado();
                    cambiarPartida(jugador);
                }
            }

        }

        private void imprimirResultado() {
            System.out.println("------------------------------------------");
            System.out.println("Ronda:"+id);
            System.out.println("Ganador:"+ ganador.nombre);
            System.out.println("Lista de perdedores:");
            jugadores.forEach(jugadorListado -> {
                if (!Objects.equals(jugadorListado.nombre, ganador.nombre))
                {
                    System.out.println("->"+jugadorListado.nombre);
                }
            });
            System.out.println("------------------------------------------");
        }

        void cambiarPartida(Jugador jugador)
        {
               jugador.partida = partidas.peek();
        }
        void cambiarPartida(Jugador jugador,Partida nuevaPartida)
        {
            partidas.add(nuevaPartida);
            jugador.partida = nuevaPartida;
        }
    }

}
