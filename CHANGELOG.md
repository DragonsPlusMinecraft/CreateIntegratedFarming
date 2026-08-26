## Create: Integrated Farming 1.3.3

> **Compatibility requirement:** Nether Depths Upgrade 3.2 or later is required when Nether Depths Upgrade is installed.

### Add
* Added Sable support for Spouts targeting Chicken Roosts, Farmer's Delight Organic Compost, and My Nether's Delight Letios Compost across the main world and moving sub-levels.
* Added cross-level crop harvesting for both stationary and contraption-mounted Vacuum Harvesters, including rotated sub-levels, replanting, shared inventories, and bounded suction particles.
* Added fishing support for Fishing Nets mounted in Sable sub-levels. Nets can now fish while moving through main-world water or fluid in neighboring sub-levels, including Nether Depths Upgrade Lava Fishing Nets when installed.
* Added cross-level aquatic creature capture for Fishing Nets, preserving the existing creature rules, loot, experience nuggets, and game-rule behavior.
* Added optional Simulated Auger collection for entire connected, coplanar Fishing Net panels, with round-robin distribution between multiple valid Augers.

### Fix
* Updated Nether Depths Upgrade compatibility to 3.2 and removed its obsolete fishing-event integration, preventing startup crashes with current releases.
* Spouts now revalidate cross-level targets when processing completes, preventing fluid consumption when a target has moved away.
* Cross-level Vacuum Harvester scans now use exact source-space filtering, preventing rotated sub-level bounding boxes from harvesting blocks outside the configured range.
