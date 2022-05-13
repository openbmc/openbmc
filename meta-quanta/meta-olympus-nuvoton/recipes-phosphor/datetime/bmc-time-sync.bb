SUMMARY = "BMC time sync service"
DESCRIPTION = "Provide a service for BMC sync host time via IPMB"
PR = "0.1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd
RDEPENDS:${PN} += "bash"

SRC_URI += "file://get_host_date.sh"

SYSTEMD_SERVICE:${PN} = "bmc-time-sync.service"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/get_host_date.sh ${D}${bindir}/
}
