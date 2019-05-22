package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.State;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.StatusData;

import java.awt.Color;

public class GHMOPhaseCloakStats extends BaseShipSystemScript {
    public static final Color JITTER_COLOR = new Color(43, 209, 255, 255);
    public static final float JITTER_FADE_TIME = 0.5F;
    public static final float SHIP_ALPHA_MULT = 0.25F;
    public static final float VULNERABLE_FRACTION = 0.0F;
    public static final float INCOMING_DAMAGE_MULT = 0.25F;
    public static final float MAX_TIME_MULT = 3.0F;
    protected Object STATUSKEY1 = new Object();
    protected Object STATUSKEY2 = new Object();
    protected Object STATUSKEY3 = new Object();
    protected Object STATUSKEY4 = new Object();

    public GHMOPhaseCloakStats() {
    }

    public static float getMaxTimeMult(MutableShipStatsAPI stats) {
        return 1.0F + 2.0F * stats.getDynamic().getValue("phase_time_mult");
    }

    protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
        float f = 0.0F;
        ShipSystemAPI cloak = playerShip.getPhaseCloak();
        if (cloak == null) {
            cloak = playerShip.getSystem();
        }

        if (cloak != null) {
            if (effectLevel > f) {
                Global.getCombatEngine().maintainStatusForPlayerShip(this.STATUSKEY2, cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "time flow altered", false);
            }

        }
    }

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
            id = id + "_" + ship.getId();
            if (player) {
                this.maintainStatus(ship, state, effectLevel);
            }

            if (!Global.getCombatEngine().isPaused()) {
                if (state != State.COOLDOWN && state != State.IDLE) {
                    float jitterLevel = 0.0F;
                    float jitterRangeBonus = 0.0F;
                    float levelForAlpha = effectLevel;
                    ShipSystemAPI cloak = ship.getPhaseCloak();
                    if (cloak == null) {
                        cloak = ship.getSystem();
                    }

                    if (state != State.IN && state != State.ACTIVE) {
                        if (state == State.OUT) {
                            if (effectLevel > 0.5F) {
                                ship.setPhased(true);
                                float flux = ship.getCurrFlux() - ship.getHardFluxLevel();
                                ship.getFluxTracker().setHardFlux(0f);
                                ship.getFluxTracker().setCurrFlux(flux);
                            } else {
                                ship.setPhased(false);
                            }

                            levelForAlpha = effectLevel;
                        }
                    } else {
                        ship.setPhased(true);
                        levelForAlpha = effectLevel;
                    }

                    ship.setExtraAlphaMult(1.0F - 0.75F * levelForAlpha);
                    ship.setApplyExtraAlphaToEngines(true);
                    float shipTimeMult = 1.0F + (getMaxTimeMult(stats) - 1.0F) * levelForAlpha;
                    stats.getTimeMult().modifyMult(id, shipTimeMult);
                    if (player) {
                        Global.getCombatEngine().getTimeMult().modifyMult(id, 1.0F / shipTimeMult);
                    } else {
                        Global.getCombatEngine().getTimeMult().unmodify(id);
                    }

                } else {
                    this.unapply(stats, id);
                }
            }
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            Global.getCombatEngine().getTimeMult().unmodify(id);
            stats.getTimeMult().unmodify(id);
            ship.setPhased(false);
            ship.setExtraAlphaMult(1.0F);
        }
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }
}
