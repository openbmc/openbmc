SUMMARY = "Inventory to Sensor config for non-mrw machines"
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

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
