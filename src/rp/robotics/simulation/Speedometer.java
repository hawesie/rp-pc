package rp.robotics.simulation;

import java.util.LinkedList;

public class Speedometer {

	private class Pair<K, V> {
		K item1;
		V item2;

		public Pair(K _item1, V _item2) {
			super();
			item1 = _item1;
			item2 = _item2;
		}

	}

	private final LinkedList<Pair<Integer, Long>> readings = new LinkedList<Pair<Integer, Long>>();
	private int m_window;

	public Speedometer(int _tachoCount, long _timeMS, int window) {
		readings.add(new Pair<Integer, Long>(_tachoCount, _timeMS));
		m_window = window;
	}

	public double update(int _tachoCount, long _timeMs) {
		readings.addLast(new Pair<Integer, Long>(_tachoCount, _timeMs));
		if (readings.size() > m_window) {
			readings.removeFirst();
		}
		return caculateSpeed();
	}

	private double caculateSpeed() {
		return (readings.getLast().item1 - readings.getLast().item1)
				/ (readings.getLast().item2 - readings.getLast().item2);
	}

}
