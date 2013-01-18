package de.peterkossek.spdchkmailparser;

import java.util.Comparator;
import java.util.Date;

public class SpeedCheckData {
	
	public static final Comparator<SpeedCheckData> COMPARATOR = new Comparator<SpeedCheckData>() {
		
		@Override
		public int compare(SpeedCheckData data1, SpeedCheckData data2) {
			return data1.getDate().compareTo(data2.getDate());
		}
	}; 
	
	private String	ticketNumber;
	private Date	date;
	private double	downSpeed;
	private double	upSpeed;
	
	public SpeedCheckData(String ticketNumber, Date date, double downSpeed,
			double upSpeed) {
		super();
		this.ticketNumber = ticketNumber;
		this.date = date;
		this.downSpeed = downSpeed;
		this.upSpeed = upSpeed;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public Date getDate() {
		return date;
	}

	public double getDownSpeed() {
		return downSpeed;
	}

	public double getUpSpeed() {
		return upSpeed;
	}
}
