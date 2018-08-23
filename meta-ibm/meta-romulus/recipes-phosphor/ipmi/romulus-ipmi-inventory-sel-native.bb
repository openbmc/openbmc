SUMMARY = "Inventory to Sensor config for Romulus"
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

PROVIDES += "virtual/phosphor-ipmi-inventory-sel"

SRC_URI += "file://sel-config.yaml"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${sensor_datadir}
        install -d ${DEST}
        install sel-config.yaml ${DEST}/invsensor.yaml
}
