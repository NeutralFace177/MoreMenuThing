package io.bluestaggo.voxelthing.world.generation;

public enum WorldType {
    Normal,
    Chaotic;

    public static WorldType intToWorldType(int i) {
        if (i == 1) {
            return Normal;
        } else {
            return Chaotic;
        }
    }

    public static int typeToInt(WorldType type) {
        if (type == WorldType.Normal) {
            return 1;
        } else {
            return 2;
        }
    }
}
