package de.peterkossek.spdchkmailparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailParser {

	private static final String MATCHER_REGEX = "\\d\\d [A-Z][a-z][a-z] \\d\\d\\d\\d \\d\\d:\\d\\d:\\d\\d";
	private static final String DATE_REGEX = ".*"+MATCHER_REGEX+".*";
	private static Pattern pattern = Pattern.compile(MATCHER_REGEX);

	public static SpeedCheckData parse(File file) {
		SpeedCheckData data = null;
		BufferedReader br = null;
		//16 Jan 2013 13:58:34
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		
		String ticketNumber = null;
		double downSpeed = -1;
		double upSpeed = -1;
		Date date = new Date();
		
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("Ihre Bandbreite beim Download")) {
					int start = line.indexOf(":");
					int end = line.indexOf(" kBit/s");
					line = line.substring(start+2, end);
					line = line.replace(",",".");
					downSpeed = Double.parseDouble(line);
				}
				else if (line.startsWith("Ihre Bandbreite beim Upload")) {
					int start = line.indexOf(":");
					int end = line.indexOf(" kBit/s");
					line = line.substring(start+2, end);
					line = line.replace(",",".");
					upSpeed = Double.parseDouble(line);
				}
				else if (line.startsWith("Die Ticket-Nummer dieser Messung lautet")) {
					int start = line.indexOf(":");
					ticketNumber = line.substring(start+2);
				}
				else if (line.matches(DATE_REGEX)) {
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						int start = matcher.start();
						int end = matcher.end();
						line = line.substring(start, end);
						date = sdf.parse(line);
					}
				}
			}
			if (ticketNumber != null && downSpeed != -1 && upSpeed != -1 && date != null) {
				data = new SpeedCheckData(ticketNumber, date, downSpeed, upSpeed);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}
	
	public static SpeedCheckData[] parse(File[] files) {
		SpeedCheckData[] data = new SpeedCheckData[files.length];
		for (int i=0; i<files.length; i++) {
			data[i] = parse(files[i]);
		}
		return data;
	}

}
