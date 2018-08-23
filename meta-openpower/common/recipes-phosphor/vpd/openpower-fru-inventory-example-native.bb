SUMMARY = "Inventory config for openpower-vpd-parser"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit openpower-fru-vpd

SRC_URI += "file://inventory"

PROVIDES += "virtual/openpower-fru-inventory"

S = "${WORKDIR}"

do_install() {
        # This recipe is supposed to create a systemd environment file
        # with values for FRU types and paths. This example recipe
        # uses a pre-defined file ($PN/inventory).

        DEST=${D}${inventory_datadir_native}
        install -d ${DEST}
        install inventory ${DEST}
}
