# Developer Integration

This document describes the Java-facing integration APIs provided by Create: Integrated Farming. Modpack and datapack authors should use [MODPACK_README.md](MODPACK_README.md).

The examples below target Minecraft 1.21.1, Create 6.0.10, and Create: Integrated Farming 1.3.x.

## Dependency

Create: Integrated Farming is published to the DragonsPlus Maven repository:

```groovy
repositories {
    maven { url = "https://maven.dragons.plus/releases" }
}

dependencies {
    compileOnly("plus.dragons.createintegratedfarming:create-integrated-farming-1.21.1:1.3.0")

    // Useful in a NeoForge ModDev development environment.
    localRuntime("plus.dragons.createintegratedfarming:create-integrated-farming-1.21.1:1.3.0")
}
```

If your integration is optional, isolate every direct reference to this API behind your normal mod-presence check so those classes are never loaded without Create: Integrated Farming.

## Harvester API Overview

The API has two harvesting paths:

1. `CustomHarvestBehaviour.harvest(...)` handles Create's moving Mechanical Harvester.
2. `CustomHarvestBehaviour.harvestInArea(...)` handles stationary and Contraption-mounted area harvesters, including the Vacuum Harvester.

`harvest(...)` remains the single abstract method, so existing lambda implementations stay source-compatible. `harvestInArea(...)` defaults to `false`; a custom crop must opt in explicitly because applying a generic fallback to a structured or multi-block crop can corrupt it.

Once a block has a registered `CustomHarvestBehaviour`, area harvesting does not fall back to the standard crop handler. Returning `false` means that nothing was harvested; it does not request the generic implementation.

Do not register ordinary `CropBlock` crops unless they require special drops or post-harvest states. The standard area handler already supports mature `CropBlock` crops, Cocoa, Sweet Berry Bushes, Nether Wart, and simple non-solid bushes with an integer `age` property.

To disable mechanical harvesting instead of customizing it, add the block to `#create:non_harvestable`.

## Registering a Crop Behaviour

Register behaviours during common setup. A direct registration takes priority over providers:

```java
@SubscribeEvent
public static void commonSetup(FMLCommonSetupEvent event) {
    CustomHarvestBehaviour.REGISTRY.register(
            MyBlocks.FRUITING_BUSH.get(),
            new FruitingBushHarvestBehaviour());
}
```

Use a provider when one implementation applies to a block class or several related blocks:

```java
CustomHarvestBehaviour.REGISTRY.registerProvider(block ->
        block instanceof MyFruitingBushBlock bush
                ? new FruitingBushHarvestBehaviour(bush)
                : null);
```

Provider results are cached by Create's `SimpleRegistry`. A provider should return `null` for unsupported blocks and should not depend on mutable state unless it also invalidates the registry cache.

## Implementing Both Harvesting Paths

The following example uses the crop's loot table, preserves the current block's properties, and resets its age after harvesting:

```java
public final class FruitingBushHarvestBehaviour implements CustomHarvestBehaviour {
    private static final int RIPE_AGE = 3;
    private static final int RESET_AGE = 1;

    @Override
    public void harvest(
            HarvesterMovementBehaviour behaviour,
            MovementContext context,
            BlockPos pos,
            BlockState state) {
        if (!isReady(state, CustomHarvestBehaviour.partial()))
            return;

        CustomHarvestBehaviour.harvestBlock(
                context.world,
                pos,
                state.setValue(MyFruitingBushBlock.AGE, RESET_AGE),
                null,
                CustomHarvestBehaviour.getHarvestTool(context),
                1.0F,
                stack -> behaviour.collectOrDropItem(context, stack));
    }

    @Override
    public boolean harvestInArea(
            AreaHarvestContext context,
            BlockPos pos,
            BlockState state) {
        if (!isReady(state, context.harvestPartiallyGrown()))
            return false;

        CustomHarvestBehaviour.harvestBlock(
                context.level(),
                pos,
                state.setValue(MyFruitingBushBlock.AGE, RESET_AGE),
                null,
                context.tool(),
                1.0F,
                context::collect);
        return true;
    }

    private static boolean isReady(BlockState state, boolean partial) {
        int age = state.getValue(MyFruitingBushBlock.AGE);
        return partial ? age > 0 : age == RIPE_AGE;
    }
}
```

`CustomHarvestBehaviour.harvestBlock(...)` evaluates the original block's loot table and then applies the supplied result state. Use `Blocks.AIR.defaultBlockState()` as the result when the crop should be removed completely.

For a crop with explicit right-click-style drops, call `behaviour.collectOrDropItem(...)` in the moving path and `context.collect(...)` in the area path, then update the world state yourself.

## `AreaHarvestContext`

`AreaHarvestContext` supplies everything an area-compatible crop behaviour needs:

- `level()`: the world being modified.
- `replant()`: whether the caller requested replanting.
- `harvestPartiallyGrown()`: whether partially grown crops may be handled.
- `tool()` or `tool(fallback)`: a defensive copy of the harvesting tool.
- `collect(stack)`: sends output to the caller's inventory or overflow policy.
- `extractSeed(predicate, amount)`: removes planting material from the caller when replanting requires it.

Never spawn output `ItemEntity` instances directly from `harvestInArea(...)`. Passing every result to `collect(...)` lets the area harvester preserve overflow without loss or duplication.

When replanting a destructive crop, first consume a matching seed from the generated drops or through `extractSeed(...)`. If no seed is available, leave the crop removed. Crops that reset in place, such as berry bushes, normally do not consume a seed.

## Calling the Dispatcher from an Area Harvester

Area-harvester implementations can create one context for their current operation and pass candidate blocks through the canonical dispatcher:

```java
AreaHarvestContext harvestContext = new AreaHarvestContext(
        level,
        replant,
        harvestPartiallyGrown,
        tool,
        this::storeOrQueueDrop,
        (predicate, amount) -> extractFromStorage(predicate, amount));

boolean harvested = StandardAreaHarvests.harvest(
        harvestContext,
        targetPos,
        level.getBlockState(targetPos));
```

The drop collector owns all insertion and overflow handling. The seed extractor must remove and return up to the requested amount, returning `ItemStack.EMPTY` when nothing matches.

Only call the dispatcher on the logical server thread and on loaded positions. The dispatcher checks `#create:non_harvestable`, delegates registered custom behaviours first, and otherwise applies the safe standard-crop rules.

## Behaviour Requirements

A custom implementation should:

- Return `true` from `harvestInArea(...)` only after it has actually harvested or reset the target.
- Perform no mutation and return `false` for immature, malformed, or unsupported states.
- Preserve relevant properties such as facing, axis, waterlogging, trellis connections, and unrelated block-state values.
- Validate every required segment before modifying multi-block crops.
- Avoid player-only effects such as advancements and mined-block statistics during mechanical harvesting.
- Use the real loot table when the upstream crop is broken, or reproduce the upstream interaction exactly for right-click harvests.
- Test both the Mechanical Harvester path and the area-harvester path, including repeated calls and full inventories.

Reference implementations are available in:

- [Farmer's Delight tomato harvesting](src/integration/farmersdelight/java/plus/dragons/createintegratedfarming/integration/farmersdelight/farming/harvest/TomatoHarvestBehaviour.java)
- [Windswept wild berry harvesting](src/integration/windswept/java/plus/dragons/createintegratedfarming/integration/windswept/farming/harvest/WildBerryHarvestBehaviour.java)
- [Nether's Exoticism pitaya harvesting](src/integration/nethersexoticism/java/plus/dragons/createintegratedfarming/integration/nethersexoticism/farming/harvest/PitayaHarvestBehaviour.java)

## Saw Tag Constants

`SawableBlockTags.VERTICAL_PLANTS` and `SawableBlockTags.FRAGILE_VERTICAL_PLANTS` expose the `#create:vertical_plants` and `#create:vertical_plants/fragile` tags to Java integrations. Their datapack semantics are documented in [MODPACK_README.md](MODPACK_README.md).
