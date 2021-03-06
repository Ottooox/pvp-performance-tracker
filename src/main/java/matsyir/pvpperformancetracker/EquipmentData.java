/*
 * Copyright (c) 2020, Mazhar <https://twitter.com/maz_rs>
 * Copyright (c) 2020, Matsyir <https://github.com/matsyir>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package matsyir.pvpperformancetracker;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

// Mostly to help fetch LMS gear stats, since LMS items are copies of real items, so their stats aren't
// cached like most items. Each LMS item will have the 'real' itemId so the stats can be looked up.
// A few non-LMS range weapons will be saved in order to help estimate ammo type/range strength based
// on current weapon itemId.
public enum EquipmentData
{
	// Non-LMS items:
	RUNE_CROSSBOW_PVP(9185, 9185),
	ARMADYL_CROSSBOW_PVP(11785, 11785),
	DRAGON_CROSSBOW(21902, 21902),
	DRAGON_HUNTER_CROSSBOW(21012, 21012),
	DARK_BOW_PVP(11235, 11235),
	HEAVY_BALLISTA_PVP(19481, 19481),
	LIGHT_BALLISTA(19478, 19478),
	MAGIC_SHORTBOW(861, 861),
	MAGIC_SHORTBOW_I(12788, 12788),
	TOXIC_BLOWPIPE(12926, 12926),
	CRAWS_BOW(22550, 22550),

	// LMS items:
	RUNE_CROSSBOW(23601, 9185),
	ARMADYL_CROSSBOW(23611, 11785),
	DARK_BOW(20408, 11235),
	HEAVY_BALLISTA(23630, 19481),

	STATIUS_WARHAMMER(23620, 22622),
	VESTAS_LONGSWORD(23615, 22613),
	ARMADYL_GODSWORD(20593, 11802),
	DRAGON_CLAWS(20784, 13652),
	GRANITE_MAUL(20557, 4153),
	AMULET_OF_FURY(23640, 6585),
	BANDOS_TASSETS(23646, 11834),
	BLESSED_SPIRIT_SHIELD(23642, 12831),
	DHAROKS_HELM(23639, 4716),
	DHAROKS_PLATELEGS(23633, 4722),
	GUTHANS_HELM(23638, 4724),
	KARILS_TOP(23632, 4736),
	TORAGS_HELM(23637, 4745),
	TORAGS_PLATELEGS(23634, 4751),
	VERACS_HELM(23636, 4753),
	VERACS_PLATESKIRT(23635, 4759),
	MORRIGANS_JAVELIN(23619, 22636),
	SPIRIT_SHIELD(23599, 12829),
	HELM_OF_NEITIZNOT(23591, 10828),
	AMULET_OF_GLORY(20586, 1704),
	ABYSSAL_WHIP(20405, 4151),
	DRAGON_DEFENDER(23597, 12954),
	BLACK_DHIDE_BODY(20423, 2503),
	RUNE_PLATELEGS(20422, 1079),
	ROCK_CLIMBING_BOOTS(20578, 2203),
	BARROWS_GLOVES(23593, 7462),
	DRAGON_DAGGER(20407, 1215),
	ELDER_MAUL(21205, 21003),
	INFERNAL_CAPE(23622, 21295),
	GHRAZI_RAPIER(23628, 22324),

	ZURIELS_STAFF(23617, 22647),
	STAFF_OF_THE_DEAD(23613, 11791),
	KODAI_WAND(23626, 21006),
	AHRIMS_STAFF(23653, 4710),
	MYSTIC_ROBE_TOP(20425, 4091),
	MYSTIC_ROBE_BOTTOM(20426, 4093),
	AHRIMS_ROBE_TOP(20598, 4712),
	AHRIMS_ROBE_SKIRT(20599, 4714),
	OCCULT_NECKLACE(23654, 12002),
	MAGES_BOOK(23652, 6889),
	ETERNAL_BOOTS(23644, 13235),
	IMBUED_ZAMORAK_CAPE(23605, 21795),
	IMBUED_GUTHIX_CAPE(23603, 21793),
	IMBUED_SARADOMIN_CAPE(23607, 21791);

	// rings are not detected by the client so we can't add their stats anyways
	//BERSERKER_RING(23595, 6737),
	//SEERS_RING_I(23624, 11770),

	private static final Map<Integer, EquipmentData> itemData = new HashMap<>();

	@Getter
	private final int lmsItemId;
	@Getter
	private final int realItemId;

	@Inject
	EquipmentData(int lmsItemId, int realItemId)
	{
		this.lmsItemId = lmsItemId;
		this.realItemId = realItemId;
	}

	// Get the saved EquipmentData for a given itemId (could be null)
	public static EquipmentData getEquipmentDataFor(int itemId)
	{
		return itemData.get(itemId);
	}

	// get currently selected weapon ammo, based on weapon used & configured bolt choice.
	public static RangeAmmoData getWeaponAmmo(EquipmentData weapon)
	{
		if (ArrayUtils.contains(RangeAmmoData.BoltAmmo.WEAPONS_USING, weapon))
		{
			return PvpPerformanceTrackerPlugin.CONFIG.boltChoice();
		}
		else if (ArrayUtils.contains(RangeAmmoData.StrongBoltAmmo.WEAPONS_USING, weapon))
		{
			return PvpPerformanceTrackerPlugin.CONFIG.strongBoltChoice();
		}
		else if (ArrayUtils.contains(RangeAmmoData.DartAmmo.WEAPONS_USING, weapon))
		{
			return PvpPerformanceTrackerPlugin.CONFIG.bpDartChoice();
		}
		else if (weapon == HEAVY_BALLISTA || weapon == HEAVY_BALLISTA_PVP || weapon == LIGHT_BALLISTA)
		{
			return RangeAmmoData.OtherAmmo.DRAGON_JAVELIN;
		}
		else if (weapon == DARK_BOW || weapon == DARK_BOW_PVP)
		{
			return RangeAmmoData.OtherAmmo.DRAGON_ARROW;
		}
		else if (weapon == MAGIC_SHORTBOW || weapon == MAGIC_SHORTBOW_I)
		{
			return RangeAmmoData.OtherAmmo.AMETHYST_ARROWS;
		}

		return null;
	}

	static
	{
		for (EquipmentData data : EquipmentData.values())
		{
			itemData.put(data.getLmsItemId(), data);
		}
	}
}
