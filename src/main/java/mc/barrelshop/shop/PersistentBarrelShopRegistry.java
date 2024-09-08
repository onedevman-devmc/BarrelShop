package mc.barrelshop.shop;

import mc.barrelshop.shop.events.*;
import mc.compendium.events.EventListener;
import mc.compendium.events.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class PersistentBarrelShopRegistry extends EventManager<PersistentBarrelShopRegistryEvent, EventListener> implements Listener {

    private static String getShopFileName(BarrelShop shop) {
        return getShopFileName(shop.getLocation());
    }

    private static String getShopFileName(Location location) {
        return getShopFileName(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private static String getShopFileName(int x, int y, int z) {
        return x + "." + y + "." + z + ".yml";
    }

    //

    private static String getChunkDirectoryName(NamespacedKey worldKey, int chunkX, int chunkZ) {
        return worldKey.getNamespace() + "." + worldKey.getKey() + "." + chunkX + "." + chunkZ;
    }

    //

    private static class ChunkShopMap {

        private final Map<String, BarrelShop> map = new HashMap<>();

        //

        public ChunkShopMap() {

        }

        //

        public Collection<BarrelShop> values() { return this.map.values(); }

        public boolean isEmpty() { return this.map.isEmpty(); }

        public BarrelShop get(Location location) {
            return this.map.get(getShopFileName(location));
        }

        public void add(BarrelShop shop) {
            this.map.put(getShopFileName(shop), shop);
        }

        public void putIfAbsent(BarrelShop shop) {
            this.map.putIfAbsent(getShopFileName(shop), shop);
        }

        public void remove(Location location) {
            this.map.remove(getShopFileName(location));
        }

    }

    //

    private static class ShopCache {

        private final Map<String, ChunkShopMap> map = new HashMap<>();

        //

        public ShopCache() {

        }

        //

        public Collection<ChunkShopMap> values() { return this.map.values(); }

        //

        public ChunkShopMap get(Location location, boolean createIfAbsent) {
            return this.get(Objects.requireNonNull(location.getWorld()).getKey(), location.getBlockX() >> 4, location.getBlockZ() >> 4, createIfAbsent);
        }

        public ChunkShopMap get(Chunk chunk, boolean createIfAbsent) {
            return this.get(chunk.getWorld().getKey(), chunk.getX(), chunk.getZ(), createIfAbsent);
        }

        public ChunkShopMap get(NamespacedKey worldKey, int chunkX, int chunkZ, boolean createIfAbsent) {
            String chunkDirectoryName = getChunkDirectoryName(worldKey, chunkX, chunkZ);
            if(createIfAbsent) this.map.putIfAbsent(chunkDirectoryName, new ChunkShopMap());
            return this.map.get(chunkDirectoryName);
        }

        //

        public void remove(Location location) {
            this.remove(Objects.requireNonNull(location.getWorld()).getKey(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
        }

        public void remove(Chunk chunk) {
            this.remove(chunk.getWorld().getKey(), chunk.getX(), chunk.getZ());
        }

        public void remove(NamespacedKey worldKey, int chunkX, int chunkZ) {
            this.map.remove(getChunkDirectoryName(worldKey, chunkX, chunkZ));
        }

    }

    //

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        this.loadChunk(event.getChunk());
    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) throws IOException {
        this.unloadChunk(event.getChunk());
    }

    //

    private final Plugin plugin;
    private File storageDirectory;

    private final ShopCache shopCache = new ShopCache();

    //

    public PersistentBarrelShopRegistry(Plugin plugin, File storageDirectory) {
        super(PersistentBarrelShopRegistryEvent.class);

        //

        this.plugin = plugin;
        this.storageDirectory = storageDirectory;

        //

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //

    public Plugin getPlugin() { return this.plugin; }

    public File getStorageDirectory() { return this.storageDirectory; }

    public void setStorageDirectory(File storageDirectory) {
        this.storageDirectory = Objects.requireNonNull(storageDirectory, "Storage directory can't be null.");
    }

    //

    private File getChunkDirectory(Location location) {
        return getChunkDirectory(Objects.requireNonNull(location.getWorld()).getKey(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    private File getChunkDirectory(Chunk chunk) {
        return getChunkDirectory(chunk.getWorld().getKey(), chunk.getX(), chunk.getZ());
    }

    private File getChunkDirectory(NamespacedKey worldKey, int chunkX, int chunkZ) {
        return Paths.get(this.storageDirectory.getPath(), getChunkDirectoryName(worldKey, chunkX, chunkZ)).toFile();
    }

    //

    private File getShopFile(BarrelShop shop) {
        return getShopFile(shop.getBarrel().getLocation());
    }

    private File getShopFile(Location location) {
        int x = location.getBlockX();
        int z = location.getBlockZ();

        return Paths.get(
            getChunkDirectory(Objects.requireNonNull(location.getWorld()).getKey(), x >> 4, z >> 4).getPath(),
            getShopFileName(x, location.getBlockY(), z)
        ).toFile();
    }

    //

    public BarrelShop get(Location location) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        return this.get(location, true);
    }

    public BarrelShop get(Location location, boolean loadIfAbsent) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        BarrelShop shop = null;

        ChunkShopMap chunkShopMap = this.shopCache.get(location, loadIfAbsent);
        if(chunkShopMap != null) shop = chunkShopMap.get(location);

        if(shop == null && loadIfAbsent) {
            shop = this.load(location);
            if(shop != null) Objects.requireNonNull(chunkShopMap).putIfAbsent(shop);
        }

        return shop;
    }

    //

    public boolean registered(BarrelShop shop) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        return shop != null && shop.equals(this.get(shop.getLocation()));
    }

    //

    public void register(BarrelShop shop) throws IOException, BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        if(this.registered(shop)) return;

        this.shopCache.get(shop.getLocation(), true).add(shop);
        this.save(shop);
    }

    //

    public boolean unregister(BarrelShop shop) throws IOException {
        this.unload(shop, false);

        //

        File shopFile = getShopFile(shop.getLocation());
        File chunkDirectory = shopFile.getParentFile();

        boolean result = !shopFile.exists() || shopFile.delete();

        try(Stream<Path> pathStream = Files.list(chunkDirectory.toPath())) {
            if(pathStream.findAny().isEmpty()) chunkDirectory.delete();
        }

        return result;
    }

    //

    public List<BarrelShop> getAllLoadedShops() {
        List<BarrelShop> shops = new ArrayList<>();

        for(ChunkShopMap chunkShopMap : this.shopCache.values())
            shops.addAll(chunkShopMap.values());

        return Collections.unmodifiableList(shops);
    }

    private boolean save(BarrelShop shop) throws IOException {
        File file = this.getShopFile(shop);

        if(!file.exists()) {
            if(!file.getParentFile().mkdirs()) return false;
        }

        YamlConfiguration shopSave = new YamlConfiguration();

        shop.serialize(shopSave);
        shopSave.save(file);

        return true;
    }

    public void saveAll() throws IOException {
        for(ChunkShopMap chunkShopMap : this.shopCache.values())
            for(BarrelShop shop : chunkShopMap.values()) this.save(shop);
    }

    //

    public BarrelShop load(Location location) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        return this.load(location, false);
    }

    public BarrelShop load(Location location, boolean force) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        File shopFile = getShopFile(location);
        return shopFile.exists() ? this.load(shopFile, force) : null;
    }

    private BarrelShop load(File shopFile, boolean force) throws BarrelShop.InvalidSignException, BarrelShop.InvalidOwnerException, BarrelShop.InvalidBarrelException {
        BarrelShop shop = BarrelShop.deserialize(YamlConfiguration.loadConfiguration(shopFile));
        Location location = shop.getLocation();

        boolean accept = this.handle(new LoadShopEvent(location));
        if(!accept) return null;

        ChunkShopMap chunkShopMap = this.shopCache.get(location, true);

        if(force) chunkShopMap.remove(location);
        chunkShopMap.putIfAbsent(shop);

        this.handle(new ShopLoadedEvent(shop));

        return shop;
    }

    public void unload(BarrelShop shop) throws IOException {
        this.unload(shop, true);
    }

    public void unload(BarrelShop shop, boolean save) throws IOException {
        boolean accept = this.handle(new UnloadShopEvent(shop));
        if(!accept) return;

        if(save) this.save(shop);

        //

        ChunkShopMap chunkShopMap = this.shopCache.get(shop.getLocation(), false);

        if(chunkShopMap != null) {
            Location location = shop.getLocation();
            chunkShopMap.remove(location);
            if(chunkShopMap.isEmpty()) this.shopCache.remove(location);
        }

        this.handle(new ShopUnloadedEvent(shop));
    }

    //

    public List<BarrelShop> loadChunk(Chunk chunk) {
        return this.loadChunk(chunk, false);
    }

    public List<BarrelShop> loadChunk(Chunk chunk, boolean force) {
        File chunkDirectory = this.getChunkDirectory(chunk);
        if(!chunkDirectory.isDirectory()) return null;

        ChunkShopMap chunkShopMap = this.shopCache.get(chunk, true);

        for(File shopFile : Objects.requireNonNull(chunkDirectory.listFiles())) {
            try { this.load(shopFile, force); }
            catch (BarrelShop.InvalidBarrelException | BarrelShop.InvalidSignException | BarrelShop.InvalidOwnerException e) {
                throw new RuntimeException(e);
            }
        }

        return chunkShopMap.values().stream().toList();
    }

    public void unloadChunk(Chunk chunk) throws IOException {
        this.unloadChunk(chunk, false);
    }

    public void unloadChunk(Chunk chunk, boolean save) throws IOException {
        File chunkDirectory = this.getChunkDirectory(chunk);
        if(!chunkDirectory.isDirectory()) return;

        for(BarrelShop shop : this.shopCache.get(chunk, true).values())
            this.unload(shop, save);

        this.shopCache.remove(chunk);
    }

}
