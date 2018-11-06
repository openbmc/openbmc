SUMMARY = "Inventory config for openpower-vpd-parser"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${OPENPOWERBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit openpower-fru-vpd
inherit mrw-xml

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
            -m ${mrw_datadir}/${MRW_XML} \
            -c ${vpdlayout_datadir}/layout.yaml \
            -o ${DEST}/inventory
}
