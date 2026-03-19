package stickdev.tun1x.model;

import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.util.TextUtil;

public enum RewardCycle {

    MONEY,
    XP;

    public String getDisplay() {
        return getConfig(this == MONEY ? "money.display" : "xp.display");
    }

    public String getReward() {
        return TextUtil.color(getConfig(this == MONEY ? "money.reward" : "xp.reward"));
    }

    private String getConfig(String path) {
        if (this == MONEY) {
            return path.contains("display") 
                ? StickHWSun.inst().getSettings().getMoneyDisplayColor()
                : StickHWSun.inst().getSettings().getMoneyRewardColor();
        } else {
            return path.contains("display")
                ? StickHWSun.inst().getSettings().getXpDisplayColor()
                : StickHWSun.inst().getSettings().getXpRewardColor();
        }
    }

    public RewardCycle shift() {
        switch (this) {
            case MONEY:
                return XP;
            case XP:
                return MONEY;
            default:
                return MONEY;
        }
    }
}
