## Tags

### Block

* `create:vertical_plants` includes `#create:vertical_plants/fragile`.
* `create:vertical_plants/fragile` is for vertical plants that should be skipped by Create's Saw-style tree cutting logic because breaking their root already breaks the whole plant.

## Data Maps

### Chicken Food

Chicken Roost feeding is controlled by synced data maps:

* `create_integrated_farming:chicken_food` on items
* `create_integrated_farming:chicken_food` on fluids

Item entries use:

* `progress`: ticks removed from the roost's production timer. This accepts Minecraft int provider formats.
* `cooldown`: ticks before the roost can be fed again. This accepts Minecraft int provider formats.
* `using_converts_to`: optional single item stack dropped after the food item is consumed.

Example:

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

* `progress`: ticks removed from the roost's production timer.
* `cooldown`: ticks before the roost can be fed again.
* `amount`: mB consumed by one Spout feeding operation.

Example:

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

The generated item data map includes `#minecraft:chicken_food` with `2400` progress and `400-800` cooldown. When Create Crafts & Additions is loaded, the generated fluid data map also includes `createadditions:seed_oil` with `100` mB consumption, `2400` progress, and `400-800` cooldown.

## Loot Tables

### Roost Production

Roost product generation uses loot tables. Datapacks can replace these tables to tune products or add extra drops.

* `create_integrated_farming:gameplay/roost/chicken`
* `create_integrated_farming:gameplay/roost/duck`
* `create_integrated_farming:gameplay/roost/goose`

The built-in tables produce one egg for the matching animal:

* Chicken Roost: `minecraft:egg`
* Duck Roost: `untitledduckmod:duck_egg`
* Goose Roost: `untitledduckmod:goose_egg`

Duck and Goose roost loot tables are loaded only when Untitled Duck Mod is loaded.

## Optional Compat

### Twilight's Flavor & Delight

When Twilight's Flavor & Delight is loaded:

* Create Harvesters can collect Lily Pads produced by Rich Soil Lily Pad farming.
* With Create's `harvesterReplants` enabled, Huge Lily Pads and Huge Water Lilies are preserved as farm cores.
* With `harvesterReplants` disabled, Huge Lily Pads and Huge Water Lilies are harvested as drops.

## Integration Source Sets

Optional integration source sets can be disabled with Gradle properties:

* `-Penable_twilightdelight_integration=false`

Integration source set dependencies:

* `twilightdelight` requires `farmersdelight`.
