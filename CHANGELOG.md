## Create: Integrated Farming 1.3.0

### Add
* Added optional Environmental duck and Autumnity turkey Roosts with native food tags, eggs, sounds, capture rules, and Ponder scenes.
* Added goggle countdowns and blocked-output status information to every occupied poultry Roost.
* Added the rotational Vacuum Harvester with a split animated model, RPM-scaled pressure charging, directional exhaust and suction, configurable range and stress impact, an 18-slot output inventory, safe pending-drop storage, Contraption actor mode, and dedicated Ponder scenes.
* Added a public area-harvesting API plus mature-crop support for vanilla crops, Farmer's Delight tomatoes and mushroom colonies, Corn Delight high crops, and Delight o' Flight cloudshrooms.
* Added dedicated Mechanical Harvester and Vacuum Harvester integrations for Cultural Delights, Hearth and Harvest, Windswept, Festive Delight, and Nether's Exoticism crops.

### Fix
* Applied the configured chicken-food cooldown to both item and fluid feeding instead of reusing production progress.
* Fixed duck and goose roosts not consuming food accepted by hand feeding.
* Corrected Roost Ponder instructions to explain that poultry produces naturally and feeding accelerates the next output.
* Preserved roost production overflow by dropping remainders that do not fit after a partial inventory insertion.
* Prevented a client crash when the Sable integration receives a Create behaviour query without a block entity.
* Corrected Cultural Delights corn and fruiting avocado leaf drops and post-harvest states.
* Corrected Hearth and Harvest corn, grape trellises, berry bushes, and cotton drops and post-harvest states.
* Corrected Windswept wild berry and Festive Delight cinnamon harvesting.
* Corrected Nether's Exoticism jaboticaba, kiwano, Buddha's Hand, rambutan, and both pitaya harvest stages, including direction and waterlogging preservation.
* Stitched Environmental duck and Autumnity turkey textures into the block atlas so their Roosts render correctly.
