SUMMARY = "Inventory config for openpower-vpd-parser"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit openpower-fru-vpd

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           virtual/openpower-fru-vpd-layout \
           "

PROVIDES += "virtual/openpower-fru-inventory"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${inventory_datadir_native}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_openpower_fru.pl \
            -m ${datadir}/obmc-mrw/${MACHINE}.xml \
            -c ${vpdlayout_datadir}/layout.yaml \
            -o ${DEST}/inventory
}
