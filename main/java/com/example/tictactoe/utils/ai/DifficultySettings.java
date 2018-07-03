package com.example.tictactoe.utils.ai;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.tictactoe.interfaces.DifficultySettingContract;

public class DifficultySettings implements DifficultySettingContract, Parcelable {

    private final int WIN_BIAS;
    private final int LOSE_BIAS;
    private final int DEPTH;
    private final String NAME;

    public static final DifficultySettings GODLIKE = new DifficultySettings(10, 20, 4, "AI GODLIKE");
    public static final DifficultySettings EASY = new DifficultySettings(1, 1, 1, "AI EASY");
    public static final DifficultySettings NORMAL = new DifficultySettings(1, 1, 2, "AI NORMAL");
    public static final DifficultySettings HARD = new DifficultySettings(1, 1, 3, "AI HARD");

    private DifficultySettings(int winBias, int loseBias, int depth, String name) {
        this.WIN_BIAS = winBias;
        this.LOSE_BIAS = loseBias;
        this.DEPTH = depth;
        this.NAME = name;
    }

    @Override
    public int winBias() {
        return WIN_BIAS;
    }

    @Override
    public int loseBias() {
        return LOSE_BIAS;
    }

    @Override
    public int depth() {
        return DEPTH;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.WIN_BIAS);
        dest.writeInt(this.LOSE_BIAS);
        dest.writeInt(this.DEPTH);
        dest.writeString(this.NAME);
    }

    protected DifficultySettings(Parcel in) {
        this.WIN_BIAS = in.readInt();
        this.LOSE_BIAS = in.readInt();
        this.DEPTH = in.readInt();
        this.NAME = in.readString();
    }

    public static final Parcelable.Creator<DifficultySettings> CREATOR = new Parcelable.Creator<DifficultySettings>() {
        @Override
        public DifficultySettings createFromParcel(Parcel source) {
            return new DifficultySettings(source);
        }

        @Override
        public DifficultySettings[] newArray(int size) {
            return new DifficultySettings[size];
        }
    };
}
