SUMMARY = "Sensor config for phosphor-host-ipmi"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-ipmi-host
inherit native

PROVIDES += "virtual/phosphor-ipmi-sensor-inventory"

SRC_URI += "file://config.yaml"

S = "${WORKDIR}"

do_install() {
        # This recipe is supposed to create an output yaml file with
        # sensor data extracted  from the mrw.
        # provides a sample output file.

        DEST=${D}${sensor_datadir}
        install -d ${DEST}
        install config.yaml ${DEST}/sensor.yaml
}
