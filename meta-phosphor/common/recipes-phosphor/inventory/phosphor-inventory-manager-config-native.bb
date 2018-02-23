SUMMARY = "Phosphor Inventory Manager Configuration"
DESCRIPTION = "Meta-recipe, pulling in native recipes that wish to add \
data to the native /usr/share/phosphor-inventory-manager filesystem."
HOMEPAGE = "http://github.com/openbmc/phosphor-inventory-manager"
PR = "r1"

inherit obmc-phosphor-license
inherit native

PHOSPHOR_INVENTORY_MANAGER_CONFIGS += "phosphor-inventory-manager-assettag-native"

# To add additional managed inventory items, create a recipe in your layer,
# and add it to PHOSPHOR_INVENTORY_MANAGER_CONFIGS with a bbappend to this recipe.

DEPENDS += "${PHOSPHOR_INVENTORY_MANAGER_CONFIGS}"
