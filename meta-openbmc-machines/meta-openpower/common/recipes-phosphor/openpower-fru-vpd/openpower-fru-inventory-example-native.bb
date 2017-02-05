SUMMARY = "Inventory config for openpower-vpd-parser"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-openpower-fru

DEPENDS += "mrw-native mrw-perl-tools-native"

SRC_URI += "file://inventory"

PROVIDES += "virtual/phosphor-openpower-fru-inventory"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${inventory_datadir_native}
        install -d ${DEST}
        install inventory ${DEST}
}
