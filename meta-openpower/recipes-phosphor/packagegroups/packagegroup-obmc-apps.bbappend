RDEPENDS_${PN}-extrasdevtools_append_df-openpower = " obmc-pydevtools"

# Add the deprecated /org/openbmc inventory namespace providing
# obmc-mgr-inventory application to the inventory packagegroup
# until all applications have been updated to use the new,
# officially spec'ed xyz.openbmc_project.Inventory.Manager
# provider (VIRTUAL-RUNTIME_obmc-inventory-manager).
RDEPENDS_${PN}-inventory_append_df-openpower = " obmc-mgr-inventory"

# Add checkstop monitor as part of host state management package
# This will kick start a gpio monitor that will catch the
# host checkstop conditions and takes necessary actions
RDEPENDS_${PN}-host-state-mgmt_append_df-openpower = " checkstop-monitor"

# Add openpower debug collector as a requirement for state-mgmt
# since it is used during checkstop handling.
RDEPENDS_${PN}-host-state-mgmt_append_df-openpower = " openpower-debug-collector"
