package ivl.milkorama;

import ivl.milkorama.config.MilkConfig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// FUNC: Handles right-click milking of any entity on the configured list.
public class MilkEventHandler {

	// FUNC: Called when a player right-clicks an entity, turning a milkable mob into a milk bucket.
	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		// RETYPEME: Only the server handles the milking; the client just gets its
		// inventory synced back once the swap happens.
		if (event.getWorld().isRemote) {
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		// RETYPEME: Mirror vanilla cows, which refuse to be milked by a creative
		// player, before we even look at what the click target is.
		if (player.capabilities.isCreativeMode) {
			return;
		}

		ItemStack held = player.getHeldItem(event.getHand());
		if (held.isEmpty() || held.getItem() != Items.BUCKET) {
			return;
		}

		if (!MilkConfig.isMilkable(event.getTarget())) {
			return;
		}

		// RETYPEME: Mimic the cow's own milking: one bucket in, one milk bucket
		// out, with a fresh splash of cow-milk sound for good measure.
		player.world.playSound(null, player.posX, player.posY, player.posZ,
				SoundEvents.ENTITY_COW_MILK, SoundCategory.PLAYERS, 1.0F, 1.0F);
		held.shrink(1);

		ItemStack milk = new ItemStack(Items.MILK_BUCKET);
		if (held.isEmpty()) {
			player.setHeldItem(event.getHand(), milk);
		} else if (!player.inventory.addItemStackToInventory(milk)) {
			player.dropItem(milk, false);
		}

		event.setCanceled(true);
		event.setCancellationResult(EnumActionResult.SUCCESS);
	}
}