package stickdev.tun1x.model;

import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.util.TextUtil;

public enum RewardCycle {

    MONEY,
    XP;

    public String getDisplay() {
        return this == MONEY
                ? TextUtil.color(StickHWSun.inst().getSettings().getCycles().getMoneyDisplayColor())
                : TextUtil.color(StickHWSun.inst().getSettings().getCycles().getXpDisplayColor());
    }

    public String getReward() {
        return this == MONEY
                ? TextUtil.color(StickHWSun.inst().getSettings().getCycles().getMoneyRewardColor())
                : TextUtil.color(StickHWSun.inst().getSettings().getCycles().getXpRewardColor());
    }

    public RewardCycle shift() {
        if (this == RewardCycle.MONEY) return XP;
        return MONEY;
    }
}
