package stickdev.tun1x.model;

import stickdev.tun1x.util.TextUtil;

public enum RewardCycle {

    MONEY("&#FFD700Монеты", "&#FFD700монеты"),
    XP("&#00FF00Опыт", "&#00FF00опыт");

    private final String title;
    private final String label;

    RewardCycle(String title, String label) {
        this.title = title;
        this.label = label;
    }

    public String getDisplay() {
        return title;
    }

    public String getReward() {
        return TextUtil.color(label);
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
