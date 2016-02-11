package rp.util;

public class Pair<T1, T2> {
	private T1 item1;
	private T2 item2;

	private Pair(T1 _item1, T2 _item2) {
		super();
		item1 = _item1;
		item2 = _item2;
	}

	public T1 getItem1() {
		return item1;
	}

	public T2 getItem2() {
		return item2;
	}

	@Override
	public boolean equals(Object _obj) {

		if (_obj instanceof Pair<?, ?>) {
			Pair<?, ?> that = (Pair<?, ?>) _obj;
			return getItem1().equals(that.getItem1())
					&& getItem2().equals(that.getItem2());
		} else {
			return false;
		}
	}

	public static <T1, T2> Pair<T1, T2> makePair(T1 _item1, T2 _item2) {
		return new Pair<>(_item1, _item2);
	}
}
