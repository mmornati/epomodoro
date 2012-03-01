package net.mornati.epomodoro.util;

import java.util.HashMap;
import java.util.Map;

import net.mornati.epomodoro.Activator;

import org.eclipse.swt.graphics.Image;

public class PluginImages {

	private static Map<String, Image> IMAGE_CACHE=new HashMap<String, Image>();

	public static final String ICONS_CLEAR="icons/clear.png";
	public static final String ICONS_CONNECTED="icons/connected.png";
	public static final String ICONS_DICONNECTED="icons/connected.png";
	public static final String ICONS_MESSAGE="icons/message.png";
	public static final String ICONS_POMODORO="icons/pomodoro.png";

	public static final String ICONS_PLAY="icons/play.png";
	public static final String ICONS_PLAY_DISABLED="icons/play_disabled.png";
	public static final String ICONS_PAUSE="icons/pause.png";
	public static final String ICONS_PAUSE_DISABLED="icons/pause_disabled.png";
	public static final String ICONS_RESET="icons/reset.png";
	public static final String ICONS_RESET_DISABLED="icons/reset_disabled.png";

	public static Image getImage(String name) {
		if (!IMAGE_CACHE.containsKey(name)) {
			Image image=Activator.getImageDescriptor(name).createImage();
			IMAGE_CACHE.put(name, image);
		}

		return IMAGE_CACHE.get(name);
	}

}
