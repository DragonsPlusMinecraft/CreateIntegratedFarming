# Modpack and Datapack Integration

This document covers data-driven customization for Create: Integrated Farming. Java integration authors should use [DEV_README.md](DEV_README.md) instead.

## Crop Harvesting

### Preventing Mechanical Harvesting

Add a block to `#create:non_harvestable` when Create's Mechanical Harvester and the Vacuum Harvester should both skip it.

`data/create/tags/block/non_harvestable.json`:

```json
{
  "replace": false,
  "values": [
    "examplemod:delicate_crop"
  ]
}
```

The Vacuum Harvester automatically supports mature `CropBlock` crops, Cocoa, Sweet Berry Bushes, Nether Wart, and simple non-solid bushes with an integer `age` property. Structured or multi-block crops that need custom drops or post-harvest states require the Java API described in [DEV_README.md](DEV_README.md).

### Vacuum Harvester Range and Pressure Cycle

The server config key `farming.vacuumHarvesterRange` controls the horizontal radius of stationary and Contraption-mounted Vacuum Harvesters. It defaults to `10` and accepts values from `1` through `16`. The vertical range is always one block above and below the harvester.

`farming.vacuumHarvesterChargeTime` controls the base pressure-charging time and defaults to `600` ticks. A stationary Vacuum Harvester takes `max(20, ceil(configuredTicks * 64 / abs(RPM)))` ticks to charge. A Contraption-mounted Vacuum Harvester uses the configured time directly, continues charging while its Contraption is stationary, and pauses only while disabled by Contraption Controls.

At the end of each charge, the Vacuum Harvester scans and harvests every mature crop in its current configured area. Its stress impact is configured separately at `stressValues.v1.impact.vacuum_harvester` and defaults to `8 SU/RPM`.

## Vertical Plant Tags

`#create:vertical_plants` includes `#create:vertical_plants/fragile`.

Use `#create:vertical_plants/fragile` for vertical plants that should be skipped by Create's Saw-style tree-cutting logic because breaking their root already breaks the whole plant.

## Roost Feeding

### Chicken Food Data Maps

Chicken Roost feeding is controlled by synced data maps:

- `create_integrated_farming:chicken_food` on items
- `create_integrated_farming:chicken_food` on fluids

Place overrides in `data/create_integrated_farming/data_maps/item/chicken_food.json` or `data/create_integrated_farming/data_maps/fluid/chicken_food.json`, respectively.

Item entries use:

- `progress`: ticks removed from the Roost's production timer. This accepts Minecraft integer-provider formats.
- `cooldown`: ticks before the Roost can be fed again. This accepts Minecraft integer-provider formats.
- `using_converts_to`: optional single item stack dropped at the Roost after the food item is consumed.

Example item data map:

```json
{
  "values": {
    "examplemod:rich_chicken_feed": {
      "progress": 3600,
      "cooldown": {
        "type": "minecraft:uniform",
        "min_inclusive": 400,
        "max_inclusive": 800
      },
      "using_converts_to": {
        "id": "minecraft:bowl"
      }
    }
  }
}
```

Fluid entries use:

- `progress`: ticks removed from the Roost's production timer.
- `cooldown`: ticks before the Roost can be fed again.
- `amount`: millibuckets consumed by one Spout feeding operation.

Example fluid data map:

```json
{
  "values": {
    "examplemod:seed_slurry": {
      "progress": 2400,
      "cooldown": {
        "type": "minecraft:uniform",
        "min_inclusive": 400,
        "max_inclusive": 800
      },
      "amount": 100
    }
  }
}
```

The generated item data map includes `#minecraft:chicken_food` with `2400` progress and a `400`–`800` tick cooldown. The generated fluid data map applies the same progress and cooldown to every fluid in `#c:plantoil`, consuming `100` mB per feeding operation. This includes Create Crafts & Additions Seed Oil, Create: Diesel Generators Plant Oil, and compatible fluids added by other mods.

### Other Poultry Food Tags

Optional poultry Roosts use the food tag provided by their source mod. Adding an item to one of these tags makes it valid Roost food as well:

| Roost | Food tag |
| --- | --- |
| Untitled Duck duck | `#untitledduckmod:duck_breeding_food` |
| Untitled Duck goose | `#untitledduckmod:goose_breeding_food` |
| Environmental duck | `#environmental:duck_food` |
| Autumnity turkey | `#autumnity:turkey_food` |

Each accepted item removes `2400` ticks from the production timer and applies a random `400`–`800` tick feeding cooldown.

## Roost Production Loot Tables

Roost production uses block-context loot tables. Datapacks can replace these tables to tune products or add extra drops.

| Roost | Loot table | Default product | Required mod |
| --- | --- | --- | --- |
| Chicken | `create_integrated_farming:gameplay/roost/chicken` | `minecraft:egg` | None |
| Untitled Duck duck | `create_integrated_farming:gameplay/roost/duck` | `untitledduckmod:duck_egg` | Untitled Duck |
| Untitled Duck goose | `create_integrated_farming:gameplay/roost/goose` | `untitledduckmod:goose_egg` | Untitled Duck |
| Environmental duck | `create_integrated_farming:gameplay/roost/environmental_duck` | `environmental:duck_egg` | Environmental |
| Autumnity turkey | `create_integrated_farming:gameplay/roost/autumnity_turkey` | `autumnity:turkey_egg` | Autumnity |

Built-in optional loot tables use a root-level `neoforge:mod_loaded` condition. Datapack replacements that reference optional-mod items should retain the corresponding condition so they can still load when that mod is absent.
