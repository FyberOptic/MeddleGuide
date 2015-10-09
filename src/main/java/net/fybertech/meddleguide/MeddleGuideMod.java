package net.fybertech.meddleguide;

import net.fybertech.meddle.MeddleMod;
import net.fybertech.meddleapi.MeddleAPI;


@MeddleMod(id="meddleguide", name="Meddle Guide", version="1.0", author="FyberOptic")
public class MeddleGuideMod
{	
	public void init()
	{
		MeddleAPI.registerCommandHandler(new GuideCommand());
	}
}
