SUMMARY = "Quanta Q71l IPMI to DBus Sensor mapping."
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

PROVIDES += "virtual/phosphor-ipmi-sensor-inventory"

SRC_URI += "file://config.yaml"

S = "${WORKDIR}"

do_install() {
	DEST=${D}${sensor_datadir}
        install -d ${DEST}
	install config.yaml ${DEST}/sensor.yaml
}
