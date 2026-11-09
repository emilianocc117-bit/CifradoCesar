package tarea10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Tarea10 {

    // Alfabeto con la ñ incluida (27 caracteres)
    private static final String ALFABETO = "abcdefghijklmnñopqrstuvwxyz";
    private static final int TAM_ALFABETO = ALFABETO.length();

    // Frecuencias aproximadas de letras en español (para puntuar candidatos)
    // Se priorizan letras muy comunes y se penalizan combinaciones raras.
    private static final Map<Character, Integer> FRECUENCIAS_ES = new HashMap<>();
    static {
        // Letras más frecuentes en español con sus pesos
        String ordenFrecuencia = "eaosrnidlctumpbgvyqóíáéfhúzñjxkw";
        for (int i = 0; i < ordenFrecuencia.length(); i++) {
            FRECUENCIAS_ES.put(ordenFrecuencia.charAt(i), ordenFrecuencia.length() - i);
        }
    }

    /**
     * Descifra un texto aplicando un desplazamiento César inverso.
     * @param texto Texto cifrado
     * @param desplazamiento Número de posiciones a retroceder
     * @return Texto descifrado
     */
    public static String descifrar(String texto, int desplazamiento) {
        StringBuilder resultado = new StringBuilder();
        // Normalizamos el desplazamiento al rango [0, TAM_ALFABETO)
        int desp = ((desplazamiento % TAM_ALFABETO) + TAM_ALFABETO) % TAM_ALFABETO;

        for (char c : texto.toCharArray()) {
            char minuscula = Character.toLowerCase(c);
            int indice = ALFABETO.indexOf(minuscula);

            if (indice != -1) {
                // Es una letra del alfabeto: aplicar desplazamiento inverso
                int nuevoIndice = ((indice - desp) % TAM_ALFABETO + TAM_ALFABETO) % TAM_ALFABETO;
                char nuevaLetra = ALFABETO.charAt(nuevoIndice);
                // Preservar mayúscula/minúscula
                if (Character.isUpperCase(c)) {
                    resultado.append(Character.toUpperCase(nuevaLetra));
                } else {
                    resultado.append(nuevaLetra);
                }
            } else {
                // No es letra del alfabeto (espacio, signos, acentos, etc.)
                resultado.append(c);
            }
        }
        return resultado.toString();
    }

    /**
     * Puntúa un texto candidato según la frecuencia de letras en español.
     * Cuanto mayor la puntuación, más probable que sea el texto correcto.
     */
    public static int puntuar(String texto) {
        int puntuacion = 0;
        for (char c : texto.toLowerCase().toCharArray()) {
            Integer peso = FRECUENCIAS_ES.get(c);
            if (peso != null) {
                puntuacion += peso;
            }
            // Bonus por espacios (los textos reales tienen espacios)
            if (c == ' ') puntuacion += 2;
        }
        return puntuacion;
    }

    public static void main(String[] args) {
        // Ruta del archivo 
        String rutaArchivo = "mensajeCifrado.txt";

        String textoCifrado;
        try {
            Path path = Paths.get(rutaArchivo);
            textoCifrado = Files.readString(path);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        System.out.println("=== TEXTO CIFRADO ===");
        System.out.println(textoCifrado);
        System.out.println("\n=== RESULTADOS DE FUERZA BRUTA (27 desplazamientos) ===\n");

        int mejorDesplazamiento = -1;
        int mejorPuntuacion = Integer.MIN_VALUE;
        String mejorTexto = "";

        // Probamos los 27 desplazamientos posibles
        for (int k = 1; k < TAM_ALFABETO; k++) {
            String candidato = descifrar(textoCifrado, k);
            int puntuacion = puntuar(candidato);

            System.out.printf("Desplazamiento %2d -> %s%n", k, candidato);

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejorDesplazamiento = k;
                mejorTexto = candidato;
            }
        }

        System.out.println("\n=== MEJOR CANDIDATO ===");
        System.out.println("Desplazamiento: " + mejorDesplazamiento);
        System.out.println("Puntuación: " + mejorPuntuacion);
        System.out.println("Mensaje descifrado:");
        System.out.println(mejorTexto);
    }
}
