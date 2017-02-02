RDEPENDS_${PN}-extrasdev += "obmc-pydevtools"

# Add the deprecated /org/openbmc inventory namespace providing
# obmc-mgr-inventory application to the inventory packagegroup
# until all applications have been updated to use the new,
# officially spec'ed xyz.openbmc_project.Inventory.Manager
# provider (VIRTUAL-RUNTIME_obmc-inventory-manager).
RDEPENDS_${PN}-inventory += "obmc-mgr-inventory"

# Add the deprecated /org/openbmc led namespace providing
# obmc-control-led application to the leds packagegroup
# until all applications have been updated to use the new,
# officially spec'ed xyz.openbmc_project.Led
# provider (VIRTUAL-RUNTIME_obmc-leds-manager).
RDEPENDS_${PN}-leds += "obmc-control-led"

# Add the deprecated /org/openbmc sensor namespace providing
# obmc-hwmon and obmc-mgr-sensor applications to the sensor
# packagegroup until all applications have been updated to use
# the new, officially spec'ed xyz.openbmc_project.SensorValue
# providers (VIRTUAL-RUNTIME_obmc-sensor-hwmon).
RDEPENDS_${PN}-sensors += "obmc-hwmon obmc-mgr-sensor"
