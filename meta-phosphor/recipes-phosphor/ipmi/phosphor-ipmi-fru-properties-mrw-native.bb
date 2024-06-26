SUMMARY = "FRU properties config for ipmi-fru-parser"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           "
PROVIDES += "virtual/phosphor-ipmi-fru-properties"
PR = "r1"

SRC_URI += "file://config.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit phosphor-ipmi-fru
inherit mrw-xml
inherit native

do_install() {
        DEST=${D}${properties_datadir}
        install -d ${DEST}
        ${bindir}/perl-native/perl \
            ${bindir}/gen_fru_properties.pl \
            -m ${mrw_datadir}/${MRW_XML} \
            -c config.yaml \
            -o ${DEST}/extra-properties.yaml
}
