package jhddreader;

/**
 *
 * @author Allan Ayes Ramírez
 */
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class JHDDReader {

    public static void main(String[] args) {
        String disco = "/dev/zero"; // Ruta del disco duro que deseas leer
        long sectorInicial = 0; // Número del sector inicial que deseas leer
        long numSectores = 1; // Número de sectores que deseas leer

        Scanner in = new Scanner(System.in);
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
                    detectarHDDenSistema();
                    //in.next();
                    System.out.println("Seleccione el disco del que desea obtener la cantidad de sectores\n");
                    disco = in.next();
                    leerCantSectoresHDD(disco);
                    break;
                case 2:
                    System.out.println("Detectando discos en la PC");
                    detectarHDDenSistema();
                    System.out.println("Seleccione el disco que desea leer por sectores");
                    disco = in.next();
                    System.out.println("Indique a partir de que sector desea iniciar la lectura, si no sabe, escriba 0 para hacerlo desde el inicio");
                    sectorInicial = in.nextLong();
                    System.out.println("Indique la cantidad de sectores que desea leer: ");
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

        long totalSectores = 0;
        try {
            File file = new File(disco);
            long totalBytes = file.getTotalSpace();
            totalSectores = totalBytes / 512; // Tamaño de cada sector: 512 bytes

            System.out.println("Cantidad total de sectores: " + totalSectores);

            // Resto del código para leer sectores...
        } catch (Exception e) {
            System.out.println("Err: " + e.getMessage());
        }
        return totalSectores;
    }

    static void leerSectoresHDD(long sectorInicial, long totalSectores, String disco) {
        try (RandomAccessFile raf = new RandomAccessFile(disco, "r")) {
            // Calcular la posición inicial del sector
            long posicionInicial = sectorInicial * 512; // Cada sector tiene 512 bytes

            // Mover el puntero al sector inicial
            raf.seek(posicionInicial);

            // Leer los sectores
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
        }
    }

    static void detectarHDDenSistema() {
        File[] roots = File.listRoots();
        for (File root : roots) {
            System.out.println("Disco montado: " + root.getAbsolutePath());
        }
    }
}
