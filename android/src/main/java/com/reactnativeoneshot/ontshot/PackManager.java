package com.reactnativeoneshot.ontshot;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class PackManager {


	private static final char dscrc_table[] = {
	        0x00,0x91,0xe3,0x72,0x07,0x96,0xe4,0x75,
		0x0e,0x9f,0xed,0x7c,0x09,0x98,0xea,0x7b,
		0x1c,0x8d,0xff,0x6e,0x1b,0x8a,0xf8,0x69,
		0x12,0x83,0xf1,0x60,0x15,0x84,0xf6,0x67,
		0x38,0xa9,0xdb,0x4a,0x3f,0xae,0xdc,0x4d,
		0x36,0xa7,0xd5,0x44,0x31,0xa0,0xd2,0x43,
		0x24,0xb5,0xc7,0x56,0x23,0xb2,0xc0,0x51,
		0x2a,0xbb,0xc9,0x58,0x2d,0xbc,0xce,0x5f,
		0x70,0xe1,0x93,0x02,0x77,0xe6,0x94,0x05,
		0x7e,0xef,0x9d,0x0c,0x79,0xe8,0x9a,0x0b,
		0x6c,0xfd,0x8f,0x1e,0x6b,0xfa,0x88,0x19,
		0x62,0xf3,0x81,0x10,0x65,0xf4,0x86,0x17,
		0x48,0xd9,0xab,0x3a,0x4f,0xde,0xac,0x3d,
		0x46,0xd7,0xa5,0x34,0x41,0xd0,0xa2,0x33,
		0x54,0xc5,0xb7,0x26,0x53,0xc2,0xb0,0x21,
		0x5a,0xcb,0xb9,0x28,0x5d,0xcc,0xbe,0x2f,
		0xe0,0x71,0x03,0x92,0xe7,0x76,0x04,0x95,
		0xee,0x7f,0x0d,0x9c,0xe9,0x78,0x0a,0x9b,
		0xfc,0x6d,0x1f,0x8e,0xfb,0x6a,0x18,0x89,
		0xf2,0x63,0x11,0x80,0xf5,0x64,0x16,0x87,
		0xd8,0x49,0x3b,0xaa,0xdf,0x4e,0x3c,0xad,
		0xd6,0x47,0x35,0xa4,0xd1,0x40,0x32,0xa3,
		0xc4,0x55,0x27,0xb6,0xc3,0x52,0x20,0xb1,
		0xca,0x5b,0x29,0xb8,0xcd,0x5c,0x2e,0xbf,
		0x90,0x01,0x73,0xe2,0x97,0x06,0x74,0xe5,
		0x9e,0x0f,0x7d,0xec,0x99,0x08,0x7a,0xeb,
		0x8c,0x1d,0x6f,0xfe,0x8b,0x1a,0x68,0xf9,
		0x82,0x13,0x61,0xf0,0x85,0x14,0x66,0xf7,
		0xa8,0x39,0x4b,0xda,0xaf,0x3e,0x4c,0xdd,
		0xa6,0x37,0x45,0xd4,0xa1,0x30,0x42,0xd3,
		0xb4,0x25,0x57,0xc6,0xb3,0x22,0x50,0xc1,
		0xba,0x2b,0x59,0xc8,0xbd,0x2c,0x5e,0xcf};


	PackManager(){
	}

	byte calCRC8(byte[] ptr, int len, int first)
	{
		int i = 0;
		int crc = first;
	    while(len-- != 0)
	    {
	    	int p = ptr[i++];
	    	p = p < 0 ? 256 - (-p) : p;
	    	int idx = (crc ^ p)&0xFF;
	        crc = dscrc_table[idx];
	    }
	    return (byte) crc;
	}

	byte[] hexStr2Str(String hexStr)
    {
		if(hexStr == null){
			return new byte[]{};
		}
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toUpperCase().toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++)
        {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return bytes;
    }
	 // ????Unicode?????????????????????????
    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    // ?????????????????????
    public boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAssic(String strName) {
    	if(strName == null){
    		return false;
    	}
    	return strName.getBytes().length == strName.length();
    }

    public byte[] getBytes(String str){
    	if(str == null){
    		return new byte[]{};
    	}
    	if(isChinese(str)){
			if(Charset.isSupported("UTF-8")){
				return str.getBytes(Charset.forName("UTF-8"));
			}
			return str.getBytes();
		}
		else{
			return str.getBytes();
		}
    }

    public short combine2Bytes(byte high, byte low){
    	return (short) ((high & 0xff) << 8 | (low & 0xff));
    }


	public List<DataPack> preparePack(String ssid, String pwd, String bssid, String userData)
	{
		byte totalLen, pwdLen, ssidLen;
		byte bssidCrc = 0 ,pwdCrc ,ssidCrc ,totalCrc;
		int userDataLen, bssidLen;
		List<DataPack> ret = new ArrayList<DataPack>();
		List<Byte> byteList = new ArrayList<Byte>();
		byte[] ssidBytes = getBytes(ssid);
		byte[] pwdBytes = getBytes(pwd);
		byte[] bssidBytes = hexStr2Str(bssid.replaceAll(":", ""));
		byte[] udBytes = getBytes(userData);
		pwdLen = (byte) pwdBytes.length;
		ssidLen = (byte) ssidBytes.length;
		userDataLen = udBytes.length;
		bssidLen = bssidBytes.length;
		if(ssidLen > 0 && pwdLen > 0){
			totalLen = (byte) (pwdLen + ssidLen + userDataLen + 5);
		}
		else if(ssidLen > 0 || pwdLen > 0){
			totalLen = (byte) (pwdLen + ssidLen + userDataLen + 4);
		}
		else
		{
			totalLen = (byte) (pwdLen + ssidLen + userDataLen + 2);
		}
		if(ssidLen > 0 || pwdLen > 0){
			bssidCrc = calCRC8(bssidBytes, bssidLen, 0);
		}
		ssidCrc = calCRC8(ssidBytes, ssidLen, 0);
		pwdCrc = calCRC8(pwdBytes, pwdLen, 0);

		totalCrc = 0;
		if(ssidLen == 0 && pwdLen == 0){
			totalCrc = calCRC8(new byte[]{totalLen, pwdLen, ssidLen}, 3, totalCrc);
		}
		else
		{
			totalCrc = calCRC8(new byte[]{totalLen, pwdLen, ssidLen, bssidCrc}, 4, totalCrc);
		}
		if(pwdLen > 0){
			totalCrc = calCRC8(pwdBytes, pwdLen, totalCrc);
			totalCrc = calCRC8(new byte[]{pwdCrc}, 1, totalCrc);
		}
		if(ssidLen > 0){
			totalCrc = calCRC8(ssidBytes, ssidLen, totalCrc);
			totalCrc = calCRC8(new byte[]{ssidCrc}, 1, totalCrc);
		}
		if(userDataLen > 0){
			totalCrc = calCRC8(udBytes, userDataLen, totalCrc);
		}
		//totalLen + pwdLen + ssidLen + bssidCrc + passWord + pwdCrc + ssid + ssidCrc + userData + totalCrc
		byteList.add(totalLen);
		byteList.add(pwdLen);
		byteList.add(ssidLen);
		if(ssidLen > 0 || pwdLen > 0){
			byteList.add(bssidCrc);
		}
		if(pwdLen > 0){
			for(byte b : pwdBytes){
				byteList.add(b);
			}
			byteList.add(pwdCrc);
		}
		if(ssidLen > 0){
			for(byte b : ssidBytes){
				byteList.add(b);
			}
			byteList.add(ssidCrc);
		}
		for(byte b : udBytes){
			byteList.add(b);
		}
		byteList.add(totalCrc);

		totalLen += 2;
		int totalPack = (totalLen + 1) / 2;
		for(int i = 0; i< totalPack; i++){
			byte data1 = byteList.get(2 * i);
			byte data2 = 0;
			if(2 * i + 1 < totalLen){
				data2 = byteList.get(2 * i + 1);
			}
			ret.add(new DataPack(data1, data2, (byte) i));
		}

		return ret;
	}

	public static void main(String args[]){
		PackManager packM = new PackManager();
		List<DataPack> dl = packM.preparePack("ww", "aa", "123456789012", "123458");
		PrintStream console = System.out;
		for(DataPack dp : dl){
			List<Short> sl = dp.getShorts();
			for(short s : sl){
				console.printf("0x%x, ", s);
			}
		}
	}
}
