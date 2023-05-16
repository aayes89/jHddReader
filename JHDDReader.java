package jhddreader;

/**
 *
 * @author Allan Ayes Ramírez
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {

    static String OS;

    public static void main(String[] args) {
        String disco = "/dev/zero"; // Ruta del disco duro que deseas leer
        long sectorInicial = 0; // Número del sector inicial que deseas leer
        long numSectores = 1; // Número de sectores que deseas leer

        Scanner in = new Scanner(System.in);

        System.out.println("Properties");
        System.out.println("Detecting OS: ");
        System.getProperties().forEach((k, v) -> {
            //System.out.println(k+":"+v);
            if (v.toString().contains("Mac OS")) {
                OS = v.toString();
                System.out.print("OS detected: " + OS);
            }
            if (k.toString().equalsIgnoreCase("os.version")) {
                System.out.println(" Version: " + v);
            }
            if (k.toString().equalsIgnoreCase("os.arch")) {
                System.out.println("Architecture: " + v);
            }
        });

        boolean opc = true;
        System.out.println("jHDDReader 0.0.1\n");
        System.out.println("");
        do {
            System.out.println("\nMENU ");
            System.out.println("0. para salir del programa");
            System.out.println("1. leer cantidad de sectores de un disco");
            System.out.println("2. leer sectores de un disco");
            System.out.println("");
            int opcion = in.nextInt();
            switch (opcion) {
                case 0:
                    System.out.println("Gracias por usar jHDDReader\njHDDReader 0.0.1 hecho por Allan Ayes");
                    System.exit(opcion);
                    break;
                case 1:
                    System.out.println("Detectando discos en la PC");
                    detectarHDDenSistema(OS);
                    //in.next();
                    System.out.println("Seleccione el disco del que desea obtener la cantidad de sectores\n");
                    disco = in.next();
                    System.out.println("Directorio establecido: " + disco);
                    leerCantSectoresHDD(disco);
                    break;
                case 2:
                    System.out.println("Detectando discos en la PC");
                    detectarHDDenSistema(OS);
                    System.out.println("Seleccione el disco que desea leer por sectores");
                    disco = in.next();
                    System.out.println("Directorio estabecido: " + disco);
                    leerCantSectoresHDD(disco);
                    /*System.out.println("Indique a partir de que sector desea iniciar la lectura, si no sabe, escriba 0 para hacerlo desde el inicio o presione ENTER");

                    String readString = in.nextLine();
                    while (readString != null) {
                        if (in.hasNextLine()) {
                            readString = in.nextLine();
                            break;
                        } else {
                            readString = null;
                        }
                    }
                    if (!readString.isEmpty()) {
                        sectorInicial = Long.valueOf(readString);
                    }*/
                    System.out.println("Indique la cantidad de bytes que desea leer o escriba 0 para lectura completa: ");
                    numSectores = in.nextLong();
                    if (numSectores == 0) {
                        numSectores = leerCantSectoresHDD(disco);
                    }
                    leerSectoresHDD(sectorInicial, numSectores, disco);
                    break;
                default:
                    System.out.println("Esa no es una opción válida");
            }

        } while (opc);
    }

    static long leerCantSectoresHDD(String disco) {
        long totalBytes = 0;
        try {
            File file = new File(disco);
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            totalBytes = raf.length();
            System.out.println("Cantidad de bytes: " + totalBytes);

            // Resto del código para leer sectores...
        } catch (Exception e) {
            System.out.println("Err: " + e.getMessage());
        }
        return totalBytes;
    }

    static void leerSectoresHDD(long sectorInicial, long totalSectores, String discoUrl) {
        try {

            String[] params = new String[]{"csh","-c","cat " + discoUrl};
            Process pr = Runtime.getRuntime().exec(params);
            BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            char[] buffer = new char[(int) totalSectores];
            System.out.println("Read result: " + br.read(buffer, (int)sectorInicial, buffer.length));
            int count = 0;
            for (char c : buffer) {
                if (count == 15) {
                    System.out.println("");
                    count = 0;
                }
                System.out.printf("%02X ", (byte) c);
                //System.out.print("("+c+")");
                count++;
            }
            System.out.println("\nRead success!");
        } catch (Exception e) {
            System.out.println("Excp: " + e.getMessage());
        }
        /*try (RandomAccessFile raf = new RandomAccessFile(disco, "r")) {
            // Calcular la posición inicial del sector
            long posicionInicial = sectorInicial * 512; // Cada sector tiene 512 bytes

            // Mover el puntero al sector inicial
            raf.seek(posicionInicial);

            // Leer los sectores
            int valR = 0;
            System.out.println("Reading sectors of: " + disco);
            while((valR=raf.read())!=-1){
                System.out.print((char)valR);
            }
            
            byte[] buffer = new byte[(int) totalSectores * 512]; // Crear un buffer para almacenar los datos leídos
            raf.read(buffer);

            // Procesar los datos leídos
            // Aquí puedes realizar las operaciones que desees con los datos leídos del disco
            // Mostrar los datos leídos en hexadecimal
            for (byte b : buffer) {
                System.out.printf("%02X ", b);
            }
        } catch (Exception e) {
            System.out.println("Err: " + e.getMessage());
        }*/
    }

    static void detectarHDDenSistema(String OS) {
        if (OS.contains("Mac")) {
            System.out.println(runCommand("diskutil list"));

        } else if (OS.contains("Windows")) {
            System.out.println("cmd /c start ");

        } else if (OS.contains("Linux")) {
            System.out.println(runCommand("csh -c lsblk -l"));

        } else {
            File[] roots = File.listRoots();
            for (File root : roots) {
                System.out.println("Disco montado: " + root.getAbsolutePath());
            }
        }
    }

    static String runCommand(String command) {
        String result = " ";
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
            int val = 0;
            while ((val = bis.read()) != -1) {
                System.out.print((char) val);
            }
            byte[] data = bis.readAllBytes();
            result = Arrays.toString(data);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}

