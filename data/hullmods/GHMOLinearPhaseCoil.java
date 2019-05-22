package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

@SuppressWarnings("Unchecked")
public class GHMOLinearPhaseCoil extends BaseHullMod {
    private static final float FRIGATE_SCALAR = .95f;
    private static final float DESTROYER_SCALAR = .80f;
    private static final float CRUISER_SCALAR = .65f;
    private static final float CAPITAL_SCALAR = .50f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getPhaseCloakActivationCostBonus().modifyMult(id, getValueByHullSize(hullSize));
        stats.getPhaseCloakCooldownBonus().modifyMult(id, getValueByHullSize(hullSize));
        stats.getPhaseCloakUpkeepCostBonus().modifyMult(id, getValueByHullSize(hullSize));
        stats.getSensorProfile().modifyMult(id, 0);
    }

    private float getValueByHullSize(ShipAPI.HullSize hullSize) {
        if (hullSize == ShipAPI.HullSize.FRIGATE) {
            return FRIGATE_SCALAR;
        } else if (hullSize == ShipAPI.HullSize.DESTROYER) {
            return DESTROYER_SCALAR;
        } else if (hullSize == ShipAPI.HullSize.CRUISER) {
            return CRUISER_SCALAR;
        } else if (hullSize == ShipAPI.HullSize.CAPITAL_SHIP) {
            return CAPITAL_SCALAR;
        }

        return 1f;
    }
}
