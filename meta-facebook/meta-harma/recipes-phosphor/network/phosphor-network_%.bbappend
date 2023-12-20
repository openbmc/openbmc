FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:harma = " file://00-bmc-usb0.network"

FILES:${PN}:append:harma = " ${sysconfdir_native}/systemd/network/00-bmc-usb0.network"

do_install:append() {
    install -d ${D}${sysconfdir_native}/systemd/network/
    install -m 0644 ${WORKDIR}/00-bmc-usb0.network \
        ${D}${sysconfdir_native}/systemd/network
}
