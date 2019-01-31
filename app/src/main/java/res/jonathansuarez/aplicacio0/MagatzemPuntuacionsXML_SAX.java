package res.jonathansuarez.aplicacio0;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MagatzemPuntuacionsXML_SAX implements MagatzemPuntuacions {

    // nom del fitxer on es guardaran les dades: data/daatta/com.example.app1/files/
    private static String FITXER = "puntuacions.xml";
    private Context context;
    // per guardar la informacio llegida del fitxer XML
    private LlistaPuntuacions llista;
    // indica si la variable llista llegida ja ha sigut llegida des del fitxer
    private boolean carregaLlista;

    public MagatzemPuntuacionsXML_SAX(Context context) {
        this.context = context;
        llista = new LlistaPuntuacions();
        carregaLlista = false;
    }

    @Override
    public void guardarPuntuacio(int punts, String nom, long data) {
        try {
            // Comprova si la variable llista te les dades
            if (!carregaLlista) {
                // Llegeix dades del fitxer XML
                llista.llegirXML(context.openFileInput(FITXER));
            }
        } catch (FileNotFoundException e) {
            // Si es la primera vegada l'arxiu no existira i es llançara aquesta excepcio. Pero no pasa res.
            Log.e("Asteroides", e.getMessage(), e);
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        // afegeix la nova puntuacio a la llista
        llista.nou(punts,nom,data);
        try {
            // escriu de nou tota la informacio de la llista al fitxer
            llista.escriuXML(context.openFileOutput(FITXER, Context.MODE_PRIVATE));
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    @Override
    public Vector<String> llistaPuntuacions(int quantitat) {
        try {
            // Comprovem si la var llista te les dades
            if (!carregaLlista) {
                // Llegeix dades del fitxer XML
                llista.llegirXML(context.openFileInput(FITXER));
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        // retorna la llista en el format esperat Vector
        return llista.aVectorString();
    }

    private class LlistaPuntuacions {
        // Una altra classe interna
        private class Puntuacio {
            int punts;
            String nom;
            long data;
        }

        // Variable que realment conte les dades XML
        private List<Puntuacio> llistaPuntuacions;

        public LlistaPuntuacions() {
            llistaPuntuacions = new ArrayList<Puntuacio>();
        }

        // Afegeix una noca puntuacio a la llista
        public void nou(int punts, String nom, long data) {
            Puntuacio puntuacio = new Puntuacio();
            puntuacio.punts = punts;
            puntuacio.nom = nom;
            puntuacio.data = data;
            llistaPuntuacions.add(puntuacio);
        }

        // Extreu la informacio que interessa de la llista i construeix
        // un vector d'strings amb la informacio
        public Vector<String> aVectorString() {
            Vector<String> result = new Vector<String>();
            for (Puntuacio puntuacio:llistaPuntuacions) {
                result.add(puntuacio.nom + " " + puntuacio.punts);
            }
            return result;
        }

        public void llegirXML(InputStream entrada) throws Exception {
            SAXParserFactory fabrica = SAXParserFactory.newInstance();
            SAXParser parser = fabrica.newSAXParser();
            XMLReader lector = parser.getXMLReader();
            ManejadorXML manejadorXML = new ManejadorXML();
            lector.setContentHandler(manejadorXML);
            lector.parse(new InputSource(entrada));
            carregaLlista = true;
        }

        class ManejadorXML extends DefaultHandler {
            // El manejador s'encarrega de generar la llista de puntuacions.
            private StringBuilder cadena;
            private Puntuacio puntuacio;

            @Override
            public void startDocument() throws SAXException {
                llistaPuntuacions = new ArrayList<Puntuacio>();
                cadena = new StringBuilder();
            }

            @Override
            public void startElement(String uri, String nomLocal, String nomQualif, Attributes atr) throws SAXException {
                // Cada vegada que començam un nou element reiniciem la cadena
                cadena.setLength(0);
                // tractar l'etiqueta puntuacio. Les altres descartades
                if (nomLocal.equals("puntuacio")) {
                    // Comença un nou obj
                    puntuacio = new Puntuacio();
                    // llegir l'atrb data de l'etiqueta. Ho rebrem per argument
                    puntuacio.data = Long.parseLong(atr.getValue("data"));
                }
            }

            @Override
            // Es crida quan apareix un text dins una etiqueta
            public void characters(char[] ch, int inici, int lon) throws SAXException {
                // guardem el text dins un string i despres el tractem
                cadena.append(ch, inici, lon);
                // SAX no garanteix que ens passara tot el text en un sol event, si el text és molt
                // extens es realitzaran diferents cridades a aquest métode. Per aixo s'acumula amb append().
            }

            @Override
            // En funcio de l'etiqueta que estiguem acabant realitzarem una tasca diferent. Si es punts
            // o noms emprarem el valor de la variable cadena per actualitzar el valor corresponent de l'obj.
            // Si es tracta de puntuacio afegim l'obj a la llista
            public void endElement(String uri, String nomLocal, String nomQualif) throws SAXException {
                if (nomLocal.equals("punts")){
                    puntuacio.punts = Integer.parseInt(cadena.toString());
                } else if (nomLocal.equals("nom")) {
                    puntuacio.nom = cadena.toString();
                } else if (nomLocal.equals("puntuacio")) {
                    llistaPuntuacions.add(puntuacio);
                }
            }

            @Override
            public void endDocument() throws SAXException {
            }
        }

        public void escriuXML(OutputStream sortida) {
            XmlSerializer serialitzador = Xml.newSerializer();
            try {
                serialitzador.setOutput(sortida, "UTF-8");
                serialitzador.startDocument("UTF-8", true);
                serialitzador.startTag("", "llista_puntuacions");
                for (Puntuacio puntuacio:llistaPuntuacions) {
                    serialitzador.startTag("", "puntuacio");
                    serialitzador.attribute("", "data", String.valueOf(puntuacio.data));
                    serialitzador.startTag("", "nom");
                    serialitzador.text(puntuacio.nom);
                    serialitzador.endTag("", "nom");
                    serialitzador.startTag("", "punts");
                    serialitzador.text(String.valueOf(puntuacio.punts));
                    serialitzador.endTag("", "punts");
                    serialitzador.endTag("", "puntuacio");
                }
                serialitzador.endTag("", "llista_puntuacions");
                serialitzador.endDocument();
            } catch (Exception e) {
                Log.e("Asteroides", e.getMessage(), e);
            }
        }
    }
}
