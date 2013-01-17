package de.peterkossek.spdchkmailparser;

import java.util.Date;

public class SpeedCheckData {
	
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
