## Create: Integrated Farming 1.4.1

### Add
* Added a unified Roosting JEI category for Chicken Roosts and supported duck, goose, and turkey Roosts. Each entry shows optional item and fluid feeding, the natural production time, and outputs that can be identified from the active loot table.
* Added Roosting JEI entries for Vanilla Backport warm and cold chickens, Confluence ducks, Untitled Duck Mod ducks and geese, Environmental ducks, and Autumnity turkeys when those mods are installed.
* Roosting JEI information is supplied by the server and refreshes after joining or reloading data packs, so server-defined food data maps, food tags, and production loot tables are reflected without reconnecting.

### Update
* Create Spout feeding now works with every supported occupied animal Roost, including duck, goose, and turkey variants, instead of only Chicken Roosts.
* The existing `create_integrated_farming:chicken_food` fluid data map remains the configuration point for Spout feeding. Existing data packs require no migration, and no separate JEI display format has been added.
* Roosting JEI displays conditional or complex loot tables conservatively and does not invent uncertain outputs or probabilities.
* Updated the Roost Spout Ponder wording to refer to birds instead of only chickens.

### Fix
* Spouts no longer begin feeding or consume fluid when a Roost is empty, on feeding cooldown, already has an output ready, or does not contain enough fluid for one configured feeding operation.
