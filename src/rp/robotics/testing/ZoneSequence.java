package rp.robotics.testing;

import java.util.ArrayList;
import java.util.Iterator;

import lejos.robotics.navigation.Pose;

public class ZoneSequence  implements Iterable<TargetZone>  {
	private final Pose m_start;
	private final ArrayList<TargetZone> m_zones;

	public ZoneSequence(Pose _start, ArrayList<TargetZone> _zones) {
		m_start = _start;
		m_zones = _zones;
	}
	
	public Pose getStart() {
		return m_start;
	}

	public ArrayList<TargetZone> getZones() {
		return m_zones;
	}
	
	@Override
	public Iterator<TargetZone> iterator() {
		return m_zones.iterator();
	}
	
}
