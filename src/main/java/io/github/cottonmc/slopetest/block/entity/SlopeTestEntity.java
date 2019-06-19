package io.github.cottonmc.slopetest.block.entity;

import io.github.cottonmc.slopetest.SlopeTest;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SlopeTestEntity extends BlockEntity implements BlockEntityClientSerializable, RenderAttachmentBlockEntity {
	private Block renderedBlock = Blocks.AIR;
	
	public SlopeTestEntity() {
		super(SlopeTest.SLOPE_ENTITY);
	}

	public Block getRenderedBlock() {
		return renderedBlock;
	}

	public void setRenderedBlock(Block block) {
		this.renderedBlock = block;
		markDirty();
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		renderedBlock = Registry.BLOCK.get(new Identifier(tag.getString("Block")));
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putString("Block", Registry.BLOCK.getId(renderedBlock).toString());
		return tag;
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		fromTag(tag);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		return toTag(tag);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (!this.world.isClient) {
			for (Object obj : PlayerStream.watching(this).toArray()) {
				ServerPlayerEntity player = (ServerPlayerEntity) obj;
				player.networkHandler.sendPacket(this.toUpdatePacket());
			}
		}
	}

    @Override
    public Object getRenderAttachmentData() {
        return renderedBlock;
    }
}
