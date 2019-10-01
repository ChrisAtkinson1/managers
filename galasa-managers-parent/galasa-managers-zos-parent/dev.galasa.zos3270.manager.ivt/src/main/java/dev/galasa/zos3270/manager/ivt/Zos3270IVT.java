package dev.galasa.zos3270.manager.ivt;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.logging.Log;

import dev.galasa.Test;
import dev.galasa.common.zos.IZosImage;
import dev.galasa.common.zos.ZosImage;
import dev.galasa.common.zos3270.ITerminal;
import dev.galasa.common.zos3270.Zos3270Terminal;
import dev.galasa.core.manager.Logger;

@Test
public class Zos3270IVT {
	
    @Logger
    public Log logger;

	@ZosImage
	public IZosImage image;

	@Zos3270Terminal
	public ITerminal terminal;

	@Test
	public void checkInjection() {
		assertThat(logger).as("Logger Field").isNotNull();
		assertThat(image).as("zOS Image Field").isNotNull();
		assertThat(terminal).as("zOS 3270 Terminal Field").isNotNull();
	}

	@Test
	public void testWithSimframe() throws Exception {
		//*** Make sure the screen is ready to go and is at the logon screen
		terminal
		.waitForKeyboard()
		.waitForTextInField("SIMFRAME LOGON SCREEN");

		//*** Logon 
		terminal
		.positionCursorToFieldContaining("Userid")
		.tab()
		.type("BOO")
		.positionCursorToFieldContaining("Password")
		.tab()
		.type("EEK")
		.enter();

		//*** Select the BANKTEST application
		terminal
		.waitForKeyboard()
		.waitForTextInField("SIMFRAME MAIN MENU")
		.positionCursorToFieldContaining("===>")
		.tab()
		.type("BANKTEST")
		.enter();

		//*** Clear the CICS Logo
		terminal
		.waitForKeyboard()
		.waitForTextInField("***  WELCOME TO CICS  ***")
		.clear();

		//*** Enter the BANK transaction
		terminal
		.waitForKeyboard()
		.tab()
		.type("BANK")
		.enter();

		//*** check we are at the main menu and then return to the logon screen
		terminal
		.waitForKeyboard()
		.waitForTextInField("SIMBANK MAIN MENU")
		.pf3()
		.waitForKeyboard()
		.pf3()
		.waitForTextInField("SIMFRAME LOGON SCREEN");
		
		logger.info("SIMFRAME 3270 screen check is complete");
	}
}