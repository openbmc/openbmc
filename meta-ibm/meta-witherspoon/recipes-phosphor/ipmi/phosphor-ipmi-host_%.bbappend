FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append = " \
	file://occ_sensors.hardcoded.yaml \
	file://hwmon_sensors.hardcoded.yaml \
	"

# System-specific sensors
SRC_URI_append_witherspoon = " \
	file://witherspoon_hwmon_sensors.hardcoded.yaml \
	"
