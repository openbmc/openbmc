# Zaius is not ready yet with GPIO bindings so disabling
# checkstop monitor for now.
RDEPENDS_${PN}-host-state-mgmt_remove = "checkstop-monitor"

# Disabling the supporting app also
RDEPENDS_${PN}-host-state-mgmt_remove = "openpower-debug-collector"
