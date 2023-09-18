package io.bluestaggo.voxelthing.world;

import io.bluestaggo.pds.*;
import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.Identifier;
import io.bluestaggo.voxelthing.renderer.world.WorldRenderer;
import io.bluestaggo.voxelthing.world.block.Block;
import io.bluestaggo.voxelthing.world.storage.ISaveHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientWorld extends World {
	public final Game game;

	public ClientWorld(Game game, ISaveHandler saveHandler) {
		this(game, saveHandler, null);
	}

	public ClientWorld(Game game, ISaveHandler saveHandler, WorldInfo worldInfo) {
		super(saveHandler, worldInfo);
		this.game = game;
		game.renderer.worldRenderer.setWorld(this);
		game.renderer.worldRenderer.loadRenderers();
	}

	private void debugChunk(int cx, int cy, int cz) {
		byte[] blocks = new byte[Chunk.VOLUME];
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = (byte) (i % (Block.WOOL.length + 1));
		}

		List<StructureItem> palette = Arrays.stream(Block.WOOL)
				.map(Block::getId)
				.map(Identifier::serialize)
				.collect(Collectors.toCollection(ArrayList::new));
		palette.add(0, Block.ID_AIR.serialize());

		CompoundItem item = new CompoundItem();
		item.map.put("blockPalette", new ListItem(palette));
		item.map.put("blockArrayType", new ByteItem(1));
		item.map.put("blocks", new ByteArrayItem(blocks));

		chunkStorage.deserializeChunkAt(cx, cy, cz, item);
		onChunkAdded(cx, cy, cz);
	}

	@Override
	public void onBlockUpdate(int x, int y, int z) {
		super.onBlockUpdate(x, y, z);
		WorldRenderer worldRenderer = game.renderer.worldRenderer;
		worldRenderer.markNeighbourUpdateAt(x, y, z);
	}

	@Override
	public void onChunkAdded(int x, int y, int z) {
		super.onChunkAdded(x, y, z);
		WorldRenderer worldRenderer = game.renderer.worldRenderer;
		worldRenderer.markNeighbourChunkUpdateAt(x, y, z);
	}
}
