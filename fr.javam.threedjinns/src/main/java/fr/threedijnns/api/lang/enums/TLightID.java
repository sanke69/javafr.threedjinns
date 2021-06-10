package fr.threedijnns.api.lang.enums;

public enum TLightID {
    LID_GLOBAL(0),
    LID_0(0x4000), // Due to OpenGL specification
    LID_1(0x4001),
    LID_2(0x4002),
    LID_3(0x4003),
    LID_4(0x4004),
    LID_5(0x4005),
    LID_6(0x4006),
    LID_7(0x4007);
	
	int id;

	private TLightID(int _id) {
		id = _id;
	}
	
	public int GL() {
		return id;
	}

};