package com.example.tictactoe.utils.ai;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.tictactoe.interfaces.DifficultySettingContract;

public class DifficultySettings implements DifficultySettingContract, Parcelable {

    private final String NAME;
    private final int RANDOM_MOVE_CHANCE;

    public static final DifficultySettings GODLIKE = new DifficultySettings("AI GODLIKE", 0);
    public static final DifficultySettings EASY = new DifficultySettings("AI EASY", 75);
    public static final DifficultySettings NORMAL = new DifficultySettings("AI NORMAL", 50);
    public static final DifficultySettings HARD = new DifficultySettings("AI HARD", 25);

    public DifficultySettings(String name, int errorChance) {
        this.NAME = name;
		this.RANDOM_MOVE_CHANCE = errorChance;
    }

	@Override
	public int errorChance() {
		return RANDOM_MOVE_CHANCE;
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
        dest.writeString(this.NAME);
        dest.writeInt(this.RANDOM_MOVE_CHANCE);
    }

    protected DifficultySettings(Parcel in) {
        this.NAME = in.readString();
        this.RANDOM_MOVE_CHANCE = in.readInt();
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
