LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS:${PN} += " bash libgpiod-tools"

SRC_URI += " \
    file://bletchley-common-functions \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/bletchley-common-functions ${D}${libexecdir}
}
