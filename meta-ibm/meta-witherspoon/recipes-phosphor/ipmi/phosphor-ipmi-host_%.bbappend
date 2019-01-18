FILESEXTRAPATHS_append := ":${THISDIR}/${PN}"
SRC_URI_append = " \
	file://occ_sensors.hardcoded.yaml \
	file://hwmon_sensors.hardcoded.yaml \
	file://channel.yaml \
	"

EXTRA_OECONF_append = " \
        CHANNEL_YAML_GEN=${WORKDIR}/channel.yaml \
        "
