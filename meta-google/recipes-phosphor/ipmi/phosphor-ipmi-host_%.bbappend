# Neither of these are used in a gbmc configuration
RDEPENDS:${PN}:remove:gbmc = "clear-once"
RDEPENDS:${PN}:remove:gbmc = "phosphor-software-manager-updater"

EXTRA_OEMESON:append:gbmc = " -Dget-dbus-active-software=disabled"
