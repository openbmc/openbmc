SUMMARY = "Initscript for enabling USB gadget Ethernet"

LICENSE = "GPLv2"
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
