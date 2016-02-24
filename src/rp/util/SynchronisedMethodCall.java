package rp.util;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map.Entry;

public class SynchronisedMethodCall {

	private final HashMap<MethodHandle, Object[]> m_calls = new HashMap<>();
	private final int m_count;

	public SynchronisedMethodCall(MethodHandle _method, int _count,
			Object... _params) {
		m_count = _count;
		add(_method, _params);
	}

	public synchronized void add(MethodHandle _method, Object... _params) {
		m_calls.put(_method, _params);
		 System.out.println("Inner method calls: " + m_calls.size());
	}

	/**
	 * Call the methods together if its received enough entries.
	 * 
	 * @return
	 */
	public synchronized boolean call() {

		if (m_calls.size() >= m_count) {

			for (Entry<MethodHandle, Object[]> entry : m_calls.entrySet()) {
				try {

					MethodHandle mh = entry.getKey();
					Object[] obs = entry.getValue();
					mh.invokeWithArguments(obs);

					if(obs.length > 1) {
						System.out.println(obs[1]);
					}
					
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			return true;
		} else {
			return false;
		}
	}
}
