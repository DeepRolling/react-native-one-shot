package com.reactnativeoneshot.ontshot;

import java.util.ArrayList;
import java.util.List;

public class DataPack implements IDataPack {
	private static final int EXTRA_LEN = 20;

	private byte mData1;
	private byte mData2;
	private byte mCrc;
	private byte mSeq;

	DataPack(byte data1, byte data2, byte seq)
	{
		this.mData1 = data1;
		this.mData2 = data2;
		this.mSeq = seq;
	}

	@Override
	public List<Short> getShorts(){
		List<Short> ret = new ArrayList<Short>();
		PackManager packM = new PackManager();
		mCrc = packM.calCRC8(new byte[]{mData1, mData2, mSeq}, 3, 0);
		ret.add((short) (packM.combine2Bytes((byte) 0x0, mData1) + EXTRA_LEN));
		ret.add((short) (packM.combine2Bytes((byte) 0x1, mCrc) + EXTRA_LEN));
		ret.add((short) (packM.combine2Bytes((byte) 0x0, mData2) + EXTRA_LEN));
		ret.add((short) (packM.combine2Bytes((byte) 0x2, mSeq) + EXTRA_LEN));
		return ret;
	}
}
