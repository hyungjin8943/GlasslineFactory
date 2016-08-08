package factory.gui;

public class WavePlayer {

    String fileName;
    AePlayWave wave;

    public WavePlayer(String filename) {
	this.fileName = filename;
    }

    public void play() {
	wave = new AePlayWave(fileName);
	wave.start();
    }

    public void stop() {
	wave.stop();
	wave = null;
    }

}

