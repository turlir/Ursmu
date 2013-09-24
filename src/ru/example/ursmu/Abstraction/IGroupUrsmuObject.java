package ru.example.ursmu.Abstraction;

import android.os.Parcelable;

public interface IGroupUrsmuObject<T extends IUrsmuObject> extends Parcelable {

    public boolean first();

    public T next();

    public void setCounter(int value);

    public T getSample();
}
