SUMMARY = "Inventory to Sensor config for non-mrw machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-ipmi-host
inherit native

PROVIDES += "virtual/phosphor-ipmi-inventory-sel"

SRC_URI += "file://config.yaml"

S = "${WORKDIR}"

do_install() {
        # This recipe would provide the sample inventory to sensor config
        # mapping, for non-mrw machines.

        DEST=${D}${sensor_datadir}
        install -d ${DEST}
        install config.yaml ${DEST}/invsensor.yaml
}
