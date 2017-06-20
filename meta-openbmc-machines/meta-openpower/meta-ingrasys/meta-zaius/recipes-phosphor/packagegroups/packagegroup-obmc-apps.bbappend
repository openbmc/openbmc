# Zaius is not ready yet with GPIO bindings so disabling
# checkstop monitor for now.
# TODO: Enable it when openbmc/openbmc#1905 is implemented
RDEPENDS_${PN}-host-state-mgmt_remove_zaius = "checkstop-monitor"

# Disabling the supporting app also
# TODO: Enable it when openbmc/openbmc#1905 is implemented
RDEPENDS_${PN}-host-state-mgmt_remove_zaius = "openpower-debug-collector"
