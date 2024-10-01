
package spiderman.villager_inventory.world.inventory;

import spiderman.villager_inventory.init.VillagerInventoryModMenus;

import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import com.google.common.collect.ImmutableMap;
import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraft.world.inventory.InventoryMenu;
import com.mojang.datafixers.util.Pair;

public class VillagerInventoryMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
 	public static final Map<Item, Integer> FOOD_POINTS = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	public Entity boundEntity = null;
	public int update;

	public VillagerInventoryMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(VillagerInventoryModMenus.VILLAGER_INVENTORY.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(12);
		BlockPos pos = null;
		
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
		}
		
		if (pos != null) {
			if (extraData.readableBytes() > 1) {
				extraData.readByte();
				boundEntity = world.getEntity(extraData.readVarInt());
				if (boundEntity instanceof Villager villager) {
					this.internal = this.getWrapper(villager);
					this.bound = true;
				}
			}
		}
		
		this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 8, 62) {
			private final int slot = 0;

            public int getMaxStackSize() {
               return 1;
            }

            public boolean mayPlace(ItemStack stack) {
               return stack.canEquip(EquipmentSlot.FEET, boundEntity);
            }

            public boolean mayPickup(Player player) {
               ItemStack itemstack = this.getItem();
               return !itemstack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(player);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
               return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS);
            }
		}));
		this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 8, 44) {
			private final int slot = 1;

            public int getMaxStackSize() {
               return 1;
            }

            public boolean mayPlace(ItemStack stack) {
               return stack.canEquip(EquipmentSlot.LEGS, boundEntity);
            }

            public boolean mayPickup(Player player) {
               ItemStack itemstack = this.getItem();
               return !itemstack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(player);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
               return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS);
            }
		}));
		this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 8, 26) {
			private final int slot = 2;

            public int getMaxStackSize() {
               return 1;
            }

            public boolean mayPlace(ItemStack stack) {
               return stack.canEquip(EquipmentSlot.CHEST, boundEntity);
            }

            public boolean mayPickup(Player player) {
               ItemStack itemstack = this.getItem();
               return !itemstack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(player);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
               return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE);
            }
		}));
		this.customSlots.put(3, this.addSlot(new SlotItemHandler(internal, 3, 8, 8) {
			private final int slot = 3;

            public int getMaxStackSize() {
               return 1;
            }

            public boolean mayPlace(ItemStack stack) {
               return stack.canEquip(EquipmentSlot.HEAD, boundEntity);
            }

            public boolean mayPickup(Player player) {
               ItemStack itemstack = this.getItem();
               return !itemstack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(player);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
               return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET);
            }
		}));
		this.customSlots.put(4, this.addSlot(new SlotItemHandler(internal, 4, 89, 39) {
			private final int slot = 4;

			@Override
   			public ItemStack remove(int amount) {
   				ItemStack stack = super.remove(amount);
   				SaveSlot(this.slot, this.getItem());
   				return stack;
   			}

			@Override
   			public void onTake(Player player, ItemStack stack) {
   				super.onTake(player, stack);
   				SaveSlot(this.slot, this.getItem());
   			}

			@Override
   			public void set(ItemStack stack) {
      			super.set(stack);
      			SaveSlot(this.slot, this.getItem());
   			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ItemTags.create(new ResourceLocation("minecraft:villager_inventory")));
			}
		}));
		this.customSlots.put(5, this.addSlot(new SlotItemHandler(internal, 5, 107, 39) {
			private final int slot = 5;

			@Override
   			public ItemStack remove(int amount) {
   				ItemStack stack = super.remove(amount);
   				SaveSlot(this.slot, this.getItem());
   				return stack;
   			}

			@Override
   			public void onTake(Player player, ItemStack stack) {
   				super.onTake(player, stack);
   				SaveSlot(this.slot, this.getItem());
   			}

			@Override
   			public void set(ItemStack stack) {
      			super.set(stack);
      			SaveSlot(this.slot, this.getItem());
   			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ItemTags.create(new ResourceLocation("minecraft:villager_inventory")));
			}

		}));
		this.customSlots.put(6, this.addSlot(new SlotItemHandler(internal, 6, 125, 39) {
			private final int slot = 6;

			@Override
   			public ItemStack remove(int amount) {
   				ItemStack stack = super.remove(amount);
   				SaveSlot(this.slot, this.getItem());
   				return stack;
   			}

			@Override
   			public void onTake(Player player, ItemStack stack) {
   				super.onTake(player, stack);
   				SaveSlot(this.slot, this.getItem());
   			}

			@Override
   			public void set(ItemStack stack) {
      			super.set(stack);
      			SaveSlot(this.slot, this.getItem());
   			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ItemTags.create(new ResourceLocation("minecraft:villager_inventory")));
			}

		}));
		this.customSlots.put(7, this.addSlot(new SlotItemHandler(internal, 7, 143, 39) {
			private final int slot = 7;

			@Override
   			public ItemStack remove(int amount) {
   				ItemStack stack = super.remove(amount);
   				SaveSlot(this.slot, this.getItem());
   				return stack;
   			}

			@Override
   			public void onTake(Player player, ItemStack stack) {
   				super.onTake(player, stack);
   				SaveSlot(this.slot, this.getItem());
   			}

			@Override
   			public void set(ItemStack stack) {
      			super.set(stack);
      			SaveSlot(this.slot, this.getItem());
   			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ItemTags.create(new ResourceLocation("minecraft:villager_inventory")));
			}

		}));
		this.customSlots.put(8, this.addSlot(new SlotItemHandler(internal, 8, 89, 57) {
			private final int slot = 8;

			@Override
   			public ItemStack remove(int amount) {
   				ItemStack stack = super.remove(amount);
   				SaveSlot(this.slot, this.getItem());
   				return stack;
   			}

			@Override
   			public void onTake(Player player, ItemStack stack) {
   				super.onTake(player, stack);
   				SaveSlot(this.slot, this.getItem());
   			}

			@Override
   			public void set(ItemStack stack) {
      			super.set(stack);
      			SaveSlot(this.slot, this.getItem());
   			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ItemTags.create(new ResourceLocation("minecraft:villager_inventory")));
			}

		}));
		this.customSlots.put(9, this.addSlot(new SlotItemHandler(internal, 9, 107, 57) {
			private final int slot = 9;

			@Override
   			public ItemStack remove(int amount) {
   				ItemStack stack = super.remove(amount);
   				SaveSlot(this.slot, this.getItem());
   				return stack;
   			}

			@Override
   			public void onTake(Player player, ItemStack stack) {
   				super.onTake(player, stack);
   				SaveSlot(this.slot, this.getItem());
   			}

			@Override
   			public void set(ItemStack stack) {
      			super.set(stack);
      			SaveSlot(this.slot, this.getItem());
   			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ItemTags.create(new ResourceLocation("minecraft:villager_inventory")));
			}

		}));
		this.customSlots.put(10, this.addSlot(new SlotItemHandler(internal, 10, 125, 57) {
			private final int slot = 10;

			@Override
   			public ItemStack remove(int amount) {
   				ItemStack stack = super.remove(amount);
   				SaveSlot(this.slot, this.getItem());
   				return stack;
   			}

			@Override
   			public void onTake(Player player, ItemStack stack) {
   				super.onTake(player, stack);
   				SaveSlot(this.slot, this.getItem());
   			}

			@Override
   			public void set(ItemStack stack) {
      			super.set(stack);
      			SaveSlot(this.slot, this.getItem());
   			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ItemTags.create(new ResourceLocation("minecraft:villager_inventory")));
			}

		}));
		this.customSlots.put(11, this.addSlot(new SlotItemHandler(internal, 11, 143, 57) {
			private final int slot = 11;

			@Override
   			public ItemStack remove(int amount) {
   				ItemStack stack = super.remove(amount);
   				SaveSlot(this.slot, this.getItem());
   				return stack;
   			}

			@Override
   			public void onTake(Player player, ItemStack stack) {
   				super.onTake(player, stack);
   				SaveSlot(this.slot, this.getItem());
   			}

			@Override
   			public void set(ItemStack stack) {
      			super.set(stack);
      			SaveSlot(this.slot, this.getItem());
   			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ItemTags.create(new ResourceLocation("minecraft:villager_inventory")));
			}

		}));
		
		for (int si = 0; si < 3; ++si)
			for (int sj = 0; sj < 9; ++sj)
				this.addSlot(new Slot(inv, sj + (si + 1) * 9, 0 + 8 + sj * 18, 0 + 84 + si * 18));
		for (int si = 0; si < 9; ++si)
			this.addSlot(new Slot(inv, si, 0 + 8 + si * 18, 0 + 142));

		this.LoadInventory();
	}

	public CombinedInvWrapper getWrapper(Villager villager) {
		return new CombinedInvWrapper(new EntityArmorInvWrapper(villager), new ItemStackHandler(8));
	}

	public void SaveSlot(int slot, ItemStack stack) {
		if(this.boundEntity instanceof Villager villager)
			villager.getInventory().setItem(slot - 4, stack);
	}

	public void LoadInventory() {
		this.update = 10;
		if(this.boundEntity instanceof Villager villager && this.internal instanceof CombinedInvWrapper wrapper) {
			SimpleContainer inventory = villager.getInventory();
			for(int idx = 0; idx < inventory.getContainerSize(); idx++) {
				wrapper.setStackInSlot(idx + 4, inventory.getItem(idx));
			}
		}
	}
	
	public int countFoodPointsInInventory() {
		if(this.boundEntity instanceof Villager villager) {
      		SimpleContainer simplecontainer = villager.getInventory();
      		return FOOD_POINTS.entrySet().stream().mapToInt((p_186300_) -> {
         		return simplecontainer.countItem(p_186300_.getKey()) * p_186300_.getValue();
      		}).sum();
		}
		return 0;
   	}

	@Override
	public boolean stillValid(Player player) {
		if(!(this.boundEntity instanceof Villager))
			return false;
		if(this.update-- < 0)
			this.LoadInventory();
		return this.boundEntity.isAlive();
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 8) {
				if (!this.moveItemStackTo(itemstack1, 8, this.slots.size(), true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (!this.moveItemStackTo(itemstack1, 0, 8, false)) {
				if (index < 8 + 27) {
					if (!this.moveItemStackTo(itemstack1, 8 + 27, this.slots.size(), true))
						return ItemStack.EMPTY;
				} else {
					if (!this.moveItemStackTo(itemstack1, 8, 8 + 27, false))
						return ItemStack.EMPTY;
				}
				return ItemStack.EMPTY;
			}
			if (itemstack1.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, itemstack1);
		}
		return itemstack;
	}

	@Override
	protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
		boolean flag = false;
		int i = p_38905_;
		if (p_38907_) {
			i = p_38906_ - 1;
		}
		if (p_38904_.isStackable()) {
			while (!p_38904_.isEmpty()) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}
				Slot slot = this.slots.get(i);
				ItemStack itemstack = slot.getItem();
				if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
					int j = itemstack.getCount() + p_38904_.getCount();
					int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
					if (j <= maxSize) {
						p_38904_.setCount(0);
						itemstack.setCount(j);
						slot.set(itemstack);
						flag = true;
					} else if (itemstack.getCount() < maxSize) {
						p_38904_.shrink(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.set(itemstack);
						flag = true;
					}
				}
				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}
		if (!p_38904_.isEmpty()) {
			if (p_38907_) {
				i = p_38906_ - 1;
			} else {
				i = p_38905_;
			}
			while (true) {
				if (p_38907_) {
					if (i < p_38905_) {
						break;
					}
				} else if (i >= p_38906_) {
					break;
				}
				Slot slot1 = this.slots.get(i);
				ItemStack itemstack1 = slot1.getItem();
				if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
					if (p_38904_.getCount() > slot1.getMaxStackSize()) {
						slot1.setByPlayer(p_38904_.split(slot1.getMaxStackSize()));
					} else {
						slot1.setByPlayer(p_38904_.split(p_38904_.getCount()));
					}
					slot1.setChanged();
					flag = true;
					break;
				}
				if (p_38907_) {
					--i;
				} else {
					++i;
				}
			}
		}
		return flag;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
			if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
				for (int j = 0; j < internal.getSlots(); ++j) {
					playerIn.drop(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
				}
			} else {
				for (int i = 0; i < internal.getSlots(); ++i) {
					playerIn.getInventory().placeItemBackInInventory(internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
				}
			}
		}
	}

	public Map<Integer, Slot> get() {
		return customSlots;
	}
}