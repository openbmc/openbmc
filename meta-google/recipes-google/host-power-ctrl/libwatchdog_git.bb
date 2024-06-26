SUMMARY = "Watchdog Shell Library"
DESCRIPTION = "Watchdog Shell Library"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI += " \
  file://libwatchdog.sh \
"

RDEPENDS:${PN} = "bash"

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/libwatchdog.sh ${D}${libexecdir}/
}
