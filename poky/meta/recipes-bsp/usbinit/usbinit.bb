SUMMARY = "Initscript for enabling USB gadget Ethernet"
DESCRIPTION = "This module allows ethernet emulation over USB, allowing for \
all sorts of nifty things like SSH and NFS in one go plus charging over the \
same wire, at higher speeds than most Wifi connections."
HOMEPAGE = "http://linux-sunxi.org/USB_Gadget/Ethernet"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${WORKDIR}/COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

PR = "r3"

SRC_URI = "file://usb-gether \
           file://COPYING.GPL"
S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/init.d
    install usb-gether ${D}${sysconfdir}/init.d
}

inherit update-rc.d allarch

INITSCRIPT_NAME = "usb-gether"
INITSCRIPT_PARAMS = "start 99 5 2 . stop 20 0 1 6 ."
