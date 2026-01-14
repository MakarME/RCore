package org.rebelland.rcore.model.clans;

public class Clan {
    private int id;
    private String name;
    private String tag;
    private int level;
    private long createdAt;

    // Экономика и опыт
    private long balance;
    private int exp;
    private int expNeeded;

    // Настройки и лимиты
    private boolean pvpEnabled;
    private boolean requestsEnabled;
    private int csTtl;
    private int capacity;
    private int maxPotionStacks;
    private int maxHomes;
    private int storageSlots;

    public Clan(int id, String name, String tag, int level, long createdAt, long balance, int exp, int expNeeded, boolean pvpEnabled, boolean requestsEnabled, int csTtl, int capacity, int maxPotionStacks, int maxHomes, int storageSlots) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.level = level;
        this.createdAt = createdAt;
        this.balance = balance;
        this.exp = exp;
        this.expNeeded = expNeeded;
        this.pvpEnabled = pvpEnabled;
        this.requestsEnabled = requestsEnabled;
        this.csTtl = csTtl;
        this.capacity = capacity;
        this.maxPotionStacks = maxPotionStacks;
        this.maxHomes = maxHomes;
        this.storageSlots = storageSlots;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTag() { return tag; }
    public int getLevel() { return level; }
    public long getCreatedAt() { return createdAt; }
    public long getBalance() { return balance; }
    public int getExp() { return exp; }
    public int getExpNeeded() { return expNeeded; }
    public boolean isPvpEnabled() { return pvpEnabled; }
    public boolean isRequestsEnabled() { return requestsEnabled; }
    public int getCsTtl() { return csTtl; }
    public int getCapacity() { return capacity; }
    public int getMaxPotionStacks() { return maxPotionStacks; }
    public int getMaxHomes() { return maxHomes; }
    public int getStorageSlots() { return storageSlots; }

    // Setters
    public void setBalance(long balance) { this.balance = balance; }
    public void setExp(int exp) { this.exp = exp; }
    public void setLevel(int level) { this.level = level; }

    // Сеттеры для настроек (если нужно обновлять объект в памяти до сохранения)
    public void setPvpEnabled(boolean pvpEnabled) { this.pvpEnabled = pvpEnabled; }
    public void setRequestsEnabled(boolean requestsEnabled) { this.requestsEnabled = requestsEnabled; }
}
