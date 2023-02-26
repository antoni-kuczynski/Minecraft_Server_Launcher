package Servers;

public class Runner extends Thread {
    private final String pathToServerJar;

    public Runner(String pathToServerJar) {
        this.pathToServerJar = pathToServerJar;
    }

    @Override
    public void run() {
        //TODO: command to run jar files
    }
}
