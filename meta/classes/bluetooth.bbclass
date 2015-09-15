# Avoid code duplication in bluetooth-dependent recipes.

# Define a variable that expands to the recipe (package) providing core
# bluetooth support on the platform:
# "" if bluetooth is not in DISTRO_FEATURES
# else "bluez5" if bluez5 is in DISTRO_FEATURES
# else "bluez4"

# Use this with:
#  inherit bluetooth
#  PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', '${BLUEZ}', '', d)}
#  PACKAGECONFIG[bluez4] = "--enable-bluez4,--disable-bluez4,bluez4"

BLUEZ ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5', 'bluez4', d), '', d)}"
