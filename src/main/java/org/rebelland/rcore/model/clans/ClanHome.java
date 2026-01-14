package org.rebelland.rcore.model.clans;

import java.util.Objects;

public class ClanHome {
    private final int id;
    private final int clanId;
    private final String name;
    private final String world;
    private final String server;
    private final double x, y, z;
    private final float yaw, pitch;
    private final long createdAt;

    public ClanHome(int id, int clanId, String name, String world, String server, double x, double y, double z, float yaw, float pitch, long createdAt) {
        this.id = id;
        this.clanId = clanId;
        this.name = name;
        this.world = world;
        this.server = server;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getClanId() { return clanId; }
    public String getName() { return name; }
    public String getWorld() { return world; }
    public String getServer() { return server; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public long getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanHome that = (ClanHome) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
