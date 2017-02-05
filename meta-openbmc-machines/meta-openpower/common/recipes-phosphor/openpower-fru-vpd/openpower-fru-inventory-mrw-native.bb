SUMMARY = "Inventory config for openpower-vpd-parser"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-openpower-fru

DEPENDS += "mrw-native mrw-perl-tools-native"

SRC_URI += "file://layout.yaml"

PROVIDES += "virtual/phosphor-openpower-fru-inventory"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${inventory_datadir_native}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_openpower_fru.pl \
            -m ${datadir}/obmc-mrw/${MACHINE}.xml \
            -c layout.yaml \
            -o ${DEST}/inventory
}
