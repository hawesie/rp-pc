package rp.robotics.simulation;

import java.util.LinkedList;

import rp.util.Pair;

public class Speedometer {

	private final LinkedList<Pair<Integer, Long>> readings = new LinkedList<Pair<Integer, Long>>();
	private int m_window;

	public Speedometer(int _tachoCount, long _timeMS, int window) {
		readings.add(Pair.makePair(_tachoCount, _timeMS));
		m_window = window;
	}

	public double update(int _tachoCount, long _timeMs) {
		readings.addLast(Pair.makePair(_tachoCount, _timeMs));
		if (readings.size() > m_window) {
			readings.removeFirst();
		}
		return caculateSpeed();
	}

	private double caculateSpeed() {
		// System.out
		// .println(readings.getLast().item1 - readings.getFirst().item1);
		// System.out
		// .println(readings.getLast().item2 - readings.getFirst().item2);
		return (Math.abs(readings.getLast().getItem1()
				- readings.getFirst().getItem1()) / (double) (readings
				.getLast().getItem2() - readings.getFirst().getItem2())) * 1000d;
	}

	public static void main(String[] args) {
		Speedometer speedo = new Speedometer(0, 0, 5);
		System.out.println(speedo.update(100, 1000));
		System.out.println(speedo.update(200, 2000));
		System.out.println(speedo.update(300, 3000));
		System.out.println(speedo.update(400, 4000));
		System.out.println(speedo.update(500, 5000));
		System.out.println(speedo.update(600, 6000));
		System.out.println(speedo.update(700, 7000));

	}
}
