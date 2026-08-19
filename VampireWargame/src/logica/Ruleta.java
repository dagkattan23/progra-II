package logica;

import modelo.TipoPieza;
import java.util.Random;

public class Ruleta {

    private final Random random = new Random();

    // el zombie no participa de la ruleta
    private static final TipoPieza[] OPCIONES = {
            TipoPieza.HOMBRE_LOBO,
            TipoPieza.VAMPIRO,
            TipoPieza.NECROMANTE
    };

    public TipoPieza girar() {
        int indice = random.nextInt(OPCIONES.length);
        return OPCIONES[indice];
    }
}
