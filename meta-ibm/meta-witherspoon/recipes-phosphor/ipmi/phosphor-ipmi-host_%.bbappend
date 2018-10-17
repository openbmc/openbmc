FILESEXTRAPATHS_append_witherspoon := ":${THISDIR}/${PN}"
SRC_URI_append_witherspoon = " \
	file://occ_sensors.hardcoded.yaml \
	file://hwmon_sensors.hardcoded.yaml \
	file://channel.yaml \
	"

EXTRA_OECONF_append_witherspoon = " \
        CHANNEL_YAML_GEN=${WORKDIR}/channel.yaml \
        "
