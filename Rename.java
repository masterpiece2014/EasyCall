import java.io.*;

public class Rename {
	public static void main(String[] args) throws InterruptedException {

		String name = new String();
		System.out.println("\tenter folder name:\n\t");
//		name = System.in;
		BufferedReader input =
			    new BufferedReader(new InputStreamReader(System.in));
		try {
			name = input.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ./new/wallpaper_
		final String regName = "." + File.separator
				+ name + File.separator + "wallpaper_";
		String fn;
		File temp;
		File folder = new File(name);
		int count = 0;
		for (File fl : folder.listFiles()) {
			if (!fl.isDirectory()) {
				fn = fl.getName();
				temp = new File(regName + String.valueOf(count)
						+ fn.substring(fn.lastIndexOf('.')));
				fl.renameTo(temp);
				count++;
			}
		}
	}
}