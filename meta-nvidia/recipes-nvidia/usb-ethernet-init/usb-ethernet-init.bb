SUMMARY = "Init USB Host Ethernet Gadget"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

SRC_URI = " \
           file://usb-ethernet-init.service \
           file://usb-ethernet-init.sh \
           file://01-bmc-usb0.network \
          "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += "usb-ethernet-init.service"

# Include the network configuration file in the package
FILES:${PN} += "${systemd_unitdir}/network/01-bmc-usb0.network"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/usb-ethernet-init.sh ${D}${sbindir}/
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/usb-ethernet-init.service ${D}${systemd_unitdir}/system

    # Install network configuration for systemd-networkd
    install -d ${D}${systemd_unitdir}/network/
    install -m 0644 ${UNPACKDIR}/01-bmc-usb0.network ${D}${systemd_unitdir}/network/
}
