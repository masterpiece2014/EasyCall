import java.io.*;

public class Rename {
	public static void main(String[] args) throws InterruptedException {

		File temp;
		// System.out.println("enter folder name");
		File folder = new File("temp");
		int count = 0;
		for (File fl : folder.listFiles()) {
			if (!fl.isDirectory()) {
				final String fn = fl.getName();
					temp = new File("./temp/wallpaper_" + String.valueOf(count)
															+ fn.substring(fn.lastIndexOf('.')));
					fl.renameTo(temp);
					count++;
				}
		}
	}
}