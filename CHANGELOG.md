## Create: Integrated Farming 1.4.2

### Fix

* Fixed Vacuum Harvesters removing water and harvesting Farmer's Delight rice before its panicles mature. Harvesting now preserves the submerged rice plant.
* Fixed premature harvesting and incomplete handling of Supplementaries flax, Haunted Harvest corn, Autumnity foul berries, Atmospheric aloe, and Jaden's Nether Expansion warped wart. Multi-part crops are handled as one plant, with planting material consumed when replanting destroyed crops.
* Fixed Neapolitan mint producing extra sprouts while retaining its original density. Harvesters now pick its leaves without uprooting it.
* Fixed My Nether's Delight powdery crops being uprooted instead of having their ripe peppers harvested.
* Fixed Spouts failing to catalyze My Nether's Delight Leteos Compost. In ultra-warm dimensions, each 250 mB of lava advances one stage; the tenth application completes fresh compost. Insufficient fluid, invalid catalysts, and replaced targets no longer consume fluid. Custom catalysts can use `#mynethersdelight:leteos_booster`.
* Fixed Confluence duck roost model-loading errors when Confluence is not installed.

### Compatibility

* Added optional harvesting integrations for Neapolitan 6.x (6.0.1+), Atmospheric 7.x (7.0.1+), Supplementaries 1.21.1-3.9.9+, Haunted Harvest 1.21-3.5.0+, and Jaden's Nether Expansion 2.x (2.4.1+).
