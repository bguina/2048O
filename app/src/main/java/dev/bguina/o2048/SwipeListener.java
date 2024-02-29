package dev.bguina.o2048;

interface SwipeListener {
    // This specific order is important (clockwise rotation)
    int UP = 0;
    int RIGHT = 1;
    int DOWN = 2;
    int LEFT = 3;

    void onSwipe(int direction);
}
