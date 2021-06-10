package fr.threedijnns.api.utils;

public class MipMapUtils {

	public static final int nearestPowerOfTwo(int Value) {
		int Temp = Value;
		int PowerOfTwo = 0;

		while(Temp > 1) {
			Temp >>= 1;
			++PowerOfTwo;
		}

		int Retval = 1 << PowerOfTwo;

		return Retval == Value ? Retval : Retval << 1;
	}

	public static final int getMipLevelsCount(int _w, int _h) {
        int count = 0;

        while((_w > 1) || (_h > 1)) { if (_w > 1) _w /= 2; if (_h > 1) _h /= 2; ++count; }

        return count;
    }
    
}
