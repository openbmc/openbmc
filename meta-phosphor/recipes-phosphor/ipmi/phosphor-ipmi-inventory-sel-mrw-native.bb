SUMMARY = "Inventory to Sensor config for phosphor-host-ipmi"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           "
PROVIDES += "virtual/phosphor-ipmi-inventory-sel"
PR = "r1"

SRC_URI += "file://config.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit phosphor-ipmi-host
inherit mrw-xml
inherit native

do_install() {
        DEST=${D}${sensor_datadir}
        install -d ${DEST}
        ${bindir}/perl-native/perl \
            ${bindir}/gen_ipmi_sel.pl \
            -i ${mrw_datadir}/${MRW_XML} \
            -m config.yaml \
            -o ${DEST}/invsensor.yaml
}
