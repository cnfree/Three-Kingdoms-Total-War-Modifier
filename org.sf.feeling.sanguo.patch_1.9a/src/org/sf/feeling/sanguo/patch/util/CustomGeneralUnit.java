package org.sf.feeling.sanguo.patch.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.swt.graphics.ImageData;
import org.sf.feeling.sanguo.patch.Patch;
import org.sf.feeling.sanguo.patch.model.Unit;
import org.sf.feeling.swt.win32.extension.graphics.TgaLoader;

public class CustomGeneralUnit {

	public String getFaction() {
		return generalFaction;
	}

	public void setFaction(String faction) {
		this.generalFaction = faction;
	}

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	private String displayName;

	public void setDisplayName(String displayName) {
		this.displayName = ChangeCode.Change(displayName, true);
	}

	private ImageData soldierImage;

	public void setGeneralSoldierImage(ImageData soldierImage) {
		this.soldierImage = soldierImage;
	}

	private Unit soldier;

	public void setGeneralSoldier(Unit soldier) {
		this.soldier = soldier;
	}

	private String soldierType;
	private String soldierDictionary;
	private String generalFaction;

	private String description = null;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void createCustomGeneral() {
		generalCustomSoldier();
	}

	private void generalCustomSoldier() {
		soldierType = "Custom " + name;
		soldierDictionary = soldierType.replaceAll("(\\s+)", "_");

		String soldierModel = soldier.getSoldier()[0];
		List officerTypes = soldier.getOfficers();
		try {
			UnitUtil.modifyBattleFile(generalFaction, soldierModel);
			if (officerTypes != null && officerTypes.size() > 0) {
				for (int i = 0; i < officerTypes.size(); i++) {
					UnitUtil.modifyBattleFile(generalFaction,
							(String) officerTypes.get(i));
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String horse = soldier.getMount();
		if (horse != null) {
			horse = (String) UnitUtil.getMountTypeToModelMap().get(horse);
			if (horse != null) {
				try {
					UnitUtil.modifyBattleFile(generalFaction, horse);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (!soldier.getAttributes().contains("general_unit"))
			soldier.getAttributes().add("general_unit");
		if (!soldier.getAttributes().contains("no_custom"))
			soldier.getAttributes().add("no_custom");
		UnitParser.createSoldier(soldier, this.soldierType, soldierDictionary,
				generalFaction);
		setSoldierDescription(soldierDictionary, displayName, description);
		setSoldierImage();
	}

	private void setSoldierImage() {
		String bigFilePath = Patch.GAME_ROOT + "\\alexander\\data\\ui\\unit_info\\"
				+ generalFaction + "\\" + soldierDictionary + "_info.tga";
		String smallFilePath = Patch.GAME_ROOT + "\\alexander\\data\\ui\\units\\"
				+ generalFaction + "\\#" + soldierDictionary + ".tga";
		if (soldierImage != null) {
			try {
				TgaLoader.saveImage(new FileOutputStream(bigFilePath),
						soldierImage.scaledTo(160, 210));
				TgaLoader.saveImage(new FileOutputStream(smallFilePath),
						soldierImage.scaledTo(48, 64));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setSoldierDescription(String soldierDictionary,
			String displayName, String description) {

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(
							FileConstants.exportUnitFile, true), "UTF-16LE")),
					false);
			out.println();

			String short_description = displayName + "麾下的精銳親兵。";
			String long_description = displayName + "麾下的精銳親兵。\\n\\n";
			long_description += (displayName + "的部下隨" + displayName + "征戰四方。\\n");

			if (description != null && description.length() > 0)
				long_description = ChangeCode.toShort(description);
			out.println("{" + soldierDictionary + "}" + displayName);
			out.println("{" + soldierDictionary + "_descr}" + long_description);
			out.println("{" + soldierDictionary + "_descr_short}"
					+ short_description);

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(
							FileConstants.unitEnumsFile, true), "GBK")), false);

			out.println();
			out.println(soldierDictionary);
			out.println(soldierDictionary + "_descr");
			out.println(soldierDictionary + "_descr_short");

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
