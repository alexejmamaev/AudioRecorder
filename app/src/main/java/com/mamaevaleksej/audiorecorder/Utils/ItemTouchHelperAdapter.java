package com.mamaevaleksej.audiorecorder.Utils;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
