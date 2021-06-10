package fr.threedijnns.engine;

import fr.threedijnns.api.lang.enums.Capability;
import fr.threedijnns.api.lang.enums.EngineOption;

public interface gxEngine {

	String 			getName();
	String 			getVersion();

	String 			getRendererDesc();
	String 			getHardwareDesc();
	String 			getVendorDesc();

	boolean 		isAMD();
	boolean 		isNVIDIA();
	
	///< API methods >
	void 			checkCapabilities();
	@Deprecated
	boolean 		hasCapability(Capability _capacity);

	///< Engine option methods >
	boolean 		isAvailable(EngineOption _option);
	boolean 		isEnable(EngineOption _option);
	void 			enable(EngineOption _option);
	void 			disable(EngineOption _option);

	void 			makeCurrent();
	void 			attach();
	void 			detach();

}
