package emulator;

import javax.sound.midi.*;

public class AudioController {
	private  Synthesizer synth;
	private MidiChannel[] midiChannels;
	
	public AudioController() {
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		midiChannels = synth.getChannels();
	}
	
	public void startSound() {
		midiChannels[0].noteOn(60, 200);
	}
	
	
	public void stopSound() {
		midiChannels[0].noteOff(60, 200);
	}
}
