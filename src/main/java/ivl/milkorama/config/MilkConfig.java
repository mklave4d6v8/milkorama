package ivl.milkorama.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Loads and caches list of milkable mobs from a JSON config file.
public class MilkConfig {
	private static final Logger LOG = LogManager.getLogger("milkorama");

	private static final String SUBDIR = "ivl";
	private static final String FILE_NAME = "milkorama.json";
	private static final String KEY_MILKABLE = "milkableEntities";
	private static final String PLAYER_TOKEN = "player";
	private static final String[] DEFAULTS = { "minecraft:sheep", "minecraft:llama", PLAYER_TOKEN };

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final Set<String> milkableIds = new HashSet<String>();
	private static boolean milkablePlayer = false;

	private MilkConfig() {
	}

	// Reads the config from the given directory, writing defaults if missing or broken.
	public static void load(File configDir) {
		File dir = new File(configDir, SUBDIR);
		if (!dir.exists() && !dir.mkdirs()) {
			// Error handling. If we can't create the mod's config folder, we fall back to defaults.
			LOG.warn("Could not create config dir {}, using default milkable list", dir);
			resetDefaults();
			return;
		}

		File file = new File(dir, FILE_NAME);
		if (!file.exists()) {
			LOG.info("No config at {}, writing defaults", file);
			writeDefaults(file);
		}

		// Try to parse the JSON file; any failure drops back to the default list.
		try {
			JsonObject root = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
			JsonElement milkableMember = root.get(KEY_MILKABLE);
			if (milkableMember == null || !milkableMember.isJsonArray()) {
				throw new JsonParseException("missing or invalid \"" + KEY_MILKABLE + "\" array");
			}
			parseMilkable(milkableMember.getAsJsonArray());
		} catch (IOException | JsonParseException e) {
			LOG.error("Could not read {}, writing defaults: {}", file, e.getMessage());
			writeDefaults(file);
			resetDefaults();
		}
	}

	// Writes or rewrites the config file with the default milkable list.
	private static void writeDefaults(File file) {
		try (FileWriter writer = new FileWriter(file)) {
			JsonObject root = new JsonObject();
			JsonArray list = new JsonArray();
			for (String id : DEFAULTS) {
				list.add(id);
			}
			root.add(KEY_MILKABLE, list);
			GSON.toJson(root, writer);
		} catch (IOException e) {
			// RETYPEME: A failed write is not fatal; we just run on the defaults this session.
			LOG.error("Could not write default config {}: {}", file, e.getMessage());
		}
	}

	// Rebuilds the in-memory list from the default entries.
	private static void resetDefaults() {
		milkableIds.clear();
		milkablePlayer = false;
		for (String id : DEFAULTS) {
			addMilkableString(id);
		}
	}

	// Feeds every entry from the config's milkable entities array into the lookup set.
	private static void parseMilkable(JsonArray list) {
		milkableIds.clear();
		milkablePlayer = false;
		for (JsonElement element : list) {
			if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
				LOG.warn("Skipping non-string milkable entry: {}", element);
				continue;
			}
			addMilkableString(element.getAsString());
		}
	}

	// Normalizes one config entry and adds it to the lookup set.
	private static void addMilkableString(String rawId) {
		String id = rawId.trim();
		if (id.isEmpty()) {
			LOG.warn("Skipping empty milkable entry");
			return;
		}
		if (id.equalsIgnoreCase(PLAYER_TOKEN)) {
			milkablePlayer = true;
			return;
		}
		// Registry names are lowercase; bare names without a namespace default to minecraft, unless it is "player".
		if (!id.contains(":") & !id.equalsIgnoreCase(PLAYER_TOKEN)) {
			id = "minecraft:" + id;
		}
		id = id.toLowerCase();
		try {
			milkableIds.add(new ResourceLocation(id).toString());
		} catch (IllegalArgumentException e) {
			LOG.warn("Skipping invalid milkable entry \"{}\": {}", rawId, e.getMessage());
		}
	}

	// Pokes lists to check if our entity is on the milkable list.
	public static boolean isMilkable(Entity target) {
		if (milkablePlayer && target instanceof EntityPlayer) {
			return true;
		}
		ResourceLocation key = EntityList.getKey(target);
		return key != null && milkableIds.contains(key.toString());
	}
}