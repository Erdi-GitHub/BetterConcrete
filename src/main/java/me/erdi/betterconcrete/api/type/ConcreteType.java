package me.erdi.betterconcrete.api.type;

import me.erdi.betterconcrete.BetterConcretePlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public enum ConcreteType {
    WHITE("WHITE", (byte) 0),
    ORANGE("ORANGE", (byte) 1),
    MAGENTA("MAGENTA", (byte) 2),
    LIGHT_BLUE("LIGHT_BLUE", (byte) 3),
    YELLOW("YELLOW", (byte) 4),
    LIME("LIME", (byte) 5),
    PINK("PINK", (byte) 6),
    GRAY("GRAY", (byte) 7),
    LIGHT_GRAY("LIGHT_GRAY", (byte) 8),
    CYAN("CYAN", (byte) 9),
    PURPLE("PURPLE", (byte) 10),
    BLUE("BLUE", (byte) 11),
    BROWN("BROWN", (byte) 12),
    GREEN("GREEN", (byte) 13),
    RED("RED", (byte) 14),
    BLACK("BLACK", (byte) 15);

    private byte id;

    private String concreteId;
    private String powderId;

    private static final Map<Byte, ConcreteType> idToType = new HashMap<>();
    private static final Map<String, ConcreteType> concreteIdToType = new HashMap<>();
    private static final Map<String, ConcreteType> powderIdToType = new HashMap<>();

    static {
        if(BetterConcretePlugin.isLegacy()) {
            for(ConcreteType type : values())
                idToType.put(type.getLegacyId(), type);
        } else {
            for(ConcreteType type : values()) {
                concreteIdToType.put(type.concreteId, type);
                powderIdToType.put(type.powderId, type);
            }
        }
    }

    ConcreteType(String modernId, byte id) {
        this.id = id;

        this.concreteId = modernId + "_CONCRETE";
        this.powderId = this.concreteId + "_POWDER";
    }

    public byte getLegacyId() {
        return id;
    }

    public String getConcreteId() {
        return concreteId;
    }

    public String getPowderId() {
        return powderId;
    }

    public Material getMaterial(boolean powder) {
        if(BetterConcretePlugin.isLegacy())
            return powder ? BetterConcretePlugin.LEGACY_CONCRETE_POWDER : BetterConcretePlugin.LEGACY_CONCRETE;

        return powder ?
                Material.getMaterial(getPowderId()) :
                Material.getMaterial(getConcreteId());
    }

    public static ConcreteType getByBlock(Block block) {
        Material material = block.getType();

        if(BetterConcretePlugin.isLegacy()) {
            if(material != BetterConcretePlugin.LEGACY_CONCRETE &&
                    material != BetterConcretePlugin.LEGACY_CONCRETE_POWDER)
                return null;

            return idToType.get(block.getData());
        }

        ConcreteType type = concreteIdToType.get(material.name());
        if(type == null)
            type = powderIdToType.get(material.name());

        return type;
    }

    public static ConcreteType getByItem(ItemStack stack) {
        Material material = stack.getType();

        if(BetterConcretePlugin.isLegacy()) {
            if(material != BetterConcretePlugin.LEGACY_CONCRETE &&
                material != BetterConcretePlugin.LEGACY_CONCRETE_POWDER)
                return null;

            return idToType.get(stack.getData().getData());
        }

        ConcreteType type = concreteIdToType.get(material.name());
        if(type == null)
            type = powderIdToType.get(material.name());

        return type;
    }

    public static ItemStack turnPowderToConcrete(ItemStack stack) {
        ConcreteType type = getByItem(stack);
        if(type == null)
            throw new IllegalArgumentException("Non-concrete itemstack supplied!");

        stack.setType(type.getMaterial(false));

        return stack;
    }

    public static Item turnPowderToConcrete(Item item) {
        item.setItemStack(turnPowderToConcrete(item.getItemStack()));
        return item;
    }

    public static Block turnPowderToConcrete(Block block) {
        ConcreteType type = getByBlock(block);
        if(type == null)
            throw new IllegalArgumentException("Non-concrete block supplied!");

        block.setType(type.getMaterial(false));

        return block;
    }
}
