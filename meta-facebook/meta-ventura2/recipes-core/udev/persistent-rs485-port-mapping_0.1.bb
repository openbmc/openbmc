SUMMARY = "Persistent RS485 port mapping"
DESCRIPTION = "Configure persistent RS485 port mapping"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI += " \
    file://99-persistent-rs485-port-mapping.rules \
"

do_install() {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/99-persistent-rs485-port-mapping.rules ${D}${sysconfdir}/udev/rules.d
}
