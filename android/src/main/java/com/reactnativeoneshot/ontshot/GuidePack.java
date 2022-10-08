package com.reactnativeoneshot.ontshot;

import java.util.ArrayList;
import java.util.List;

public class GuidePack implements IDataPack{

	@Override
	public List<Short> getShorts() {
		List<Short> ret = new ArrayList<Short>();
		ret.add((short) 4);
		ret.add((short) 3);
		ret.add((short) 2);
		ret.add((short) 1);
		return ret;
	}

}
