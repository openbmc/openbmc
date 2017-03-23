RDEPENDS_${PN}-extrasdev += "obmc-pydevtools"

# Add the iicmaster debug tool to p9 systems. iicmaster is for fsi-based i2c
# engines. This is a temporary addition until proper kernel i2c driver is in
# place. Use p9-vcs-workaround to restrict iicmaster installtion to p9 systems
# that use openfsi
RDEPENDS_${PN}-extrasdev += "${@mf_enabled(d, 'p9-vcs-workaround', 'iicmaster')}"

# Add the deprecated /org/openbmc inventory namespace providing
# obmc-mgr-inventory application to the inventory packagegroup
# until all applications have been updated to use the new,
# officially spec'ed xyz.openbmc_project.Inventory.Manager
# provider (VIRTUAL-RUNTIME_obmc-inventory-manager).
RDEPENDS_${PN}-inventory += "obmc-mgr-inventory"

# Add the deprecated /org/openbmc sensor namespace providing
# obmc-hwmon and obmc-mgr-sensor applications to the sensor
# packagegroup until all applications have been updated to use
# the new, officially spec'ed xyz.openbmc_project.SensorValue
# providers (VIRTUAL-RUNTIME_obmc-sensor-hwmon).
RDEPENDS_${PN}-sensors += "obmc-hwmon obmc-mgr-sensor"
