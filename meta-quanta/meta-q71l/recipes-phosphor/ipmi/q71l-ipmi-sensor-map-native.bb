SUMMARY = "Quanta Q71l IPMI to DBus Sensor mapping."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${QUANTABASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-host

PROVIDES += "virtual/phosphor-ipmi-sensor-inventory"

SRC_URI += "file://config.yaml"

S = "${WORKDIR}"

do_install() {
	DEST=${D}${sensor_datadir}
        install -d ${DEST}
	install config.yaml ${DEST}/sensor.yaml
}
