LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
SRC_URI:append = " file://00-bmc-usb0.network"

FILES:${PN}:append = " ${sysconfdir_native}/systemd/network/00-bmc-usb0.network"

do_install() {
    install -d ${D}${sysconfdir_native}/systemd/network/
    install -m 0644 ${UNPACKDIR}/00-bmc-usb0.network \
        ${D}${sysconfdir_native}/systemd/network
}
