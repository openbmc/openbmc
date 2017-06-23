RDEPENDS_${PN}-extrasdev += "obmc-pydevtools"

# Add the deprecated /org/openbmc inventory namespace providing
# obmc-mgr-inventory application to the inventory packagegroup
# until all applications have been updated to use the new,
# officially spec'ed xyz.openbmc_project.Inventory.Manager
# provider (VIRTUAL-RUNTIME_obmc-inventory-manager).
RDEPENDS_${PN}-inventory += "obmc-mgr-inventory"

# Add the deprecated /org/openbmc sensor namespace providing
# the obmc-mgr-sensor application to the sensor
# packagegroup until all applications have been updated to use
# the new, officially spec'ed xyz.openbmc_project.SensorValue
# providers (VIRTUAL-RUNTIME_obmc-sensor-hwmon).
RDEPENDS_${PN}-sensors += "obmc-mgr-sensor"

# Add checkstop monitor as part of host state management package
# This will kick start a gpio monitor that will catch the
# host checkstop conditions and takes necessary actions
RDEPENDS_${PN}-host-state-mgmt += "checkstop-monitor"

# Add openpower debug collector as a requirement for state-mgmt
# since it is used during checkstop handling.
RDEPENDS_${PN}-host-state-mgmt += "openpower-debug-collector"
