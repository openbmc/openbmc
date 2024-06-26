SUMMARY = "Generated callout information for phosphor-logging"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           "
PROVIDES += "virtual/phosphor-logging-callouts"
PR = "r1"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit phosphor-logging
inherit mrw-xml
inherit native

do_install() {
        DEST=${D}${callouts_datadir}
        install -d ${DEST}
        ${bindir}/perl-native/perl \
            ${bindir}/gen_callouts.pl \
            -m ${mrw_datadir}/${MRW_XML} \
            -o ${DEST}/callouts.yaml
}
