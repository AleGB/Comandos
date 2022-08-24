import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.nio.charset.Charset;
import java.text.Normalizer;

/*
 * Programa que ejecuta, mediante un proceso, el comando "ipconfig /all", obtiene la informacion que arroja dicho comando y posteriormente la guarda para poder ser consultada (se consulta el Nombre de host y la Direccion IP)
 */

public class Commands{
    
    /*
     *Para poder organizar y guardar la informacion se utilizan dos estructuras Hashtable, esto facilita el acceso a los datos de manera individual
     *La informacion se conforma de la siguiente manera: Cada interfaz de red contiene atributos y cada atributo un valor
    */
    public static Hashtable<String, Hashtable> networkInterfacesTable = new Hashtable<String, Hashtable>(); //En la hashtable networkInterfacesTable cada clave (interfaz de red) se relaciona con otra estructura de datos para guardar sus atributos y valores (dataTable)
    public static Hashtable<String, String> dataTable = new Hashtable<String, String>(); //En la hashtable dataTable se guardara cada atributo con sus respectivos valores

    /*
     * Metodo principal
     */
    public static void main(String[] args) {
        try {
            Process process = Runtime.getRuntime().exec("ipconfig /all"); //Proceso que ejecuta el comando "ipconfig /all"
            saveInfo(process); 
            searchData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Metodo que se encarga guardar la informacion obtenida del proceso 
     */
    public static void saveInfo(Process process) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8")); //lee la entrada de datos
        String info = "";
        String networkInterface = ""; //interfaz de red
        String dataArray[] = new String[2];
        String attribute = ""; //atributo de cada interfaz de red
        String value = ""; //valor del atributo de cada interfaz de red

        while ((info = r.readLine()) != null) { //Se ejecutara el ciclo mientras que el valor que se le asigna a info sea diferente de null
            
            if (info.length() != 0) {
                if (info.charAt(0) != ' ') {//si el primer caracter de la cadena es diferente de ' ' significa que corresponde al nombre de una interfaz de red
                    dataTable = new Hashtable<String, String>(); //Cada vez que se encuentre el titulo de una interfaz de red creara una nueva hashtable dataTable que este relacionada con la nueva clave
                    networkInterface = info.trim(); //trim() elimina espacios en blanco de ambos lados de la cadena, se asigna el nombre de la interfaz encontrada
                    
                    //para evitar problemas con los acaracteres que tienen acento es mejor removerlos de la cadena
                    networkInterface = Normalizer.normalize(networkInterface, Normalizer.Form.NFD); //Con el metodo normalize se puede descomponer la cadena, separando las tildes de las letras
                    networkInterface = networkInterface.replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""); //remueve las tildes con ayuda de una expresion regular 
                } else {
                    dataArray = info.split(" :"); //con el metodo split separamos la cadena para guardar por separado los atributos y los valores 
                    if (dataArray.length > 1) {
                        attribute = dataArray[0].replace(".", ""); //se da formato a la cadena, se remueven los puntos del atributo
                        attribute = attribute.trim(); //se eliminan los espacios en blanco antes y despues de la cadena del atributo
                        value = dataArray[1].trim(); //se eliminan los espacios en blanco antes y despues de la cadena del valor
                    } else {
                        attribute = "Valor";
                        value = dataArray[0].trim();
                    }

                    //De igual forma se remueven las tildes de los atributos y sus valores
                    attribute = Normalizer.normalize(attribute, Normalizer.Form.NFD);
                    attribute = attribute.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                    value = Normalizer.normalize(value, Normalizer.Form.NFD);
                    value = value.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

                    dataTable.put(attribute, value); //se agregan los atributos y valores a la hashtable dataTable
                    networkInterfacesTable.put(networkInterface, dataTable); //se agrega a networkInterfacesTable la interfaz de red y la hashtable que contiene sus atributos y valores
                }
            }
        }
    }

    /*
     * Metodo que se encarga de buscar Nombre del host y Direccion IP del sistema
     */
    public static void searchData() {
        String networkInterface;
        String attribute;

        networkInterface = "Configuracion IP de Windows";
        attribute = "Nombre de host";
        System.out.println("NI: " + networkInterface);
        System.out.println("    " + attribute + " : " + networkInterfacesTable.get(networkInterface).get(attribute));//Se obtiene el valor de la hashtable a traves del método get(), dando como parametro la clave que queremos recuperar:

        networkInterface = "Adaptador de LAN inalambrica Wi-Fi:";
        attribute = "Direccion IPv4";
        System.out.println("NI: " + networkInterface);
        System.out.println("    " + attribute + " : " + networkInterfacesTable.get(networkInterface).get(attribute));
    }

}
