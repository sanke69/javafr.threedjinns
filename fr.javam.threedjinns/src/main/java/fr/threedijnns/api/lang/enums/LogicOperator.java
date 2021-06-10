package fr.threedijnns.api.lang.enums;

public enum LogicOperator {
    LOGIC_CLEAR				(),
    LOGIC_SET				(),
    LOGIC_COPY				(),
    LOGIC_COPY_INVERTED		(),
    LOGIC_NOOP				(),
    LOGIC_INVERT			(),
    LOGIC_AND				(),
    LOGIC_NAND				(),
    LOGIC_OR				(),
    LOGIC_NOR				(),
    LOGIC_XOR				(),
    LOGIC_EQUIV				(),
    LOGIC_AND_REVERSE		(),
    LOGIC_AND_INVERTED		(),
    LOGIC_OR_REVERSE		(),
    LOGIC_OR_INVERTED		();

	private LogicOperator() {
		;
	}

};