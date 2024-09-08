package mc.barrelshop.shop;

import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.*;

public class ContainerItemList implements List<ItemStack> {

    public static class InvalidContainerItemException extends RuntimeException {

        private static final String DEFAULT_MESSAGE = "The given ItemStack should represent a container block.";

        public InvalidContainerItemException() { super(DEFAULT_MESSAGE); }
        public InvalidContainerItemException(Throwable cause) { super(DEFAULT_MESSAGE, cause); }

    }

    //

    public static void checkContainerItemValidity(ItemStack itemStack) {
        if(
            itemStack != null && (
                !(itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta)
                || !(blockStateMeta.getBlockState() instanceof Container)
            )
        ) throw new InvalidContainerItemException();
    }

    public static Inventory getContainerItemInventory(ItemStack containerItem) {
        return (
            containerItem == null
            ? null
            : ((Container) ((BlockStateMeta) Objects.requireNonNull(containerItem.getItemMeta())).getBlockState()).getInventory()
        );
    }

    public static void setContainerItemInventoryContent(ItemStack containerItem, ItemStack[] content) {
        BlockStateMeta meta = (BlockStateMeta) Objects.requireNonNull(containerItem.getItemMeta());
        Container container = (Container) meta.getBlockState();

        container.getInventory().setContents(content);
        container.update(true);

        meta.setBlockState(container);
        containerItem.setItemMeta(meta);
    }

    //

    private final List<Inventory> inventories;
    private final List<ItemStack> containerItems = new ArrayList<>();

    //

    public ContainerItemList(List<Inventory> inventoryList) {
        this.inventories = inventoryList;
    }

    //

    public List<Inventory> getInventoryList() {
        return Collections.unmodifiableList(this.inventories);
    }

    //

    private ItemStack updateOutgoingContainerItemInventory(ItemStack containerItem, Inventory inventory) {
        if(containerItem == null) return null;
        setContainerItemInventoryContent(containerItem, inventory.getContents());
        return containerItem;
    }

    //

    @Override
    public synchronized int size() { return this.containerItems.size(); }

    @Override
    public synchronized boolean isEmpty() { return this.containerItems.isEmpty(); }

    @Override
    public synchronized boolean contains(Object o) { return this.containerItems.contains(o); }

    @Override
    public synchronized Iterator<ItemStack> iterator() {
        final Iterator<ItemStack> iterator = this.containerItems.iterator();

        return new Iterator<ItemStack>() {

            @Override
            public boolean hasNext() { return iterator.hasNext(); }

            @Override
            public ItemStack next() {
                ItemStack containerItem = iterator.next();
                return updateOutgoingContainerItemInventory(containerItem, inventories.get(containerItems.indexOf(containerItem)));
            }

        };
    }

    @Override
    public synchronized Object[] toArray() {
        return this.containerItems.stream().map(containerItem -> this.updateOutgoingContainerItemInventory(containerItem, this.inventories.get(this.containerItems.indexOf(containerItem)))).toArray();
    }

    @Override
    public synchronized <T> T[] toArray(T[] ts) {
        return this.containerItems.stream().map(containerItem -> this.updateOutgoingContainerItemInventory(containerItem, this.inventories.get(this.containerItems.indexOf(containerItem)))).toList().toArray(ts);
    }

    @Override
    public synchronized boolean add(ItemStack containerItem) {
        checkContainerItemValidity(containerItem);

        this.inventories.add(getContainerItemInventory(containerItem));
        return this.containerItems.add(containerItem);
    }

    @Override
    public synchronized boolean remove(Object o) {
        if(!this.containerItems.contains(o)) return false;

        this.inventories.remove(getContainerItemInventory((ItemStack) o));
        return this.containerItems.remove(o);
    }

    @Override
    public synchronized boolean containsAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean addAll(Collection<? extends ItemStack> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean addAll(int i, Collection<? extends ItemStack> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void clear() {
        this.inventories.clear();
        this.containerItems.clear();
    }

    @Override
    public synchronized ItemStack get(int i) {
        return this.updateOutgoingContainerItemInventory(this.containerItems.get(i), this.inventories.get(i));
    }

    @Override
    public synchronized ItemStack set(int i, ItemStack containerItem) {
        checkContainerItemValidity(containerItem);

        Inventory previousInventory = this.inventories.set(i, getContainerItemInventory(containerItem));
        return this.updateOutgoingContainerItemInventory(this.containerItems.set(i, containerItem), previousInventory);
    }

    @Override
    public synchronized void add(int i, ItemStack containerItem) {
        checkContainerItemValidity(containerItem);

        this.inventories.add(i, getContainerItemInventory(containerItem));
        this.containerItems.add(i, containerItem);
    }

    @Override
    public synchronized ItemStack remove(int i) {
        Inventory inventory = this.inventories.remove(i);
        return this.updateOutgoingContainerItemInventory(this.containerItems.remove(i), inventory);
    }

    @Override
    public synchronized int indexOf(Object o) { return this.containerItems.indexOf(o); }

    @Override
    public synchronized int lastIndexOf(Object o) { return this.containerItems.lastIndexOf(o); }

    @Override
    public synchronized ListIterator<ItemStack> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized ListIterator<ItemStack> listIterator(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<ItemStack> subList(int i, int i1) {
        throw new UnsupportedOperationException();
    }

}
