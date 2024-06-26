SUMMARY = "Inventory config for openpower-vpd-parser"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit openpower-fru-vpd
inherit mrw-xml
inherit native

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           virtual/openpower-fru-vpd-layout \
           "

PROVIDES += "virtual/openpower-fru-inventory"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
        DEST=${D}${inventory_datadir_native}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_openpower_fru.pl \
            -m ${mrw_datadir}/${MRW_XML} \
            -c ${vpdlayout_datadir}/layout.yaml \
            -o ${DEST}/inventory
}
