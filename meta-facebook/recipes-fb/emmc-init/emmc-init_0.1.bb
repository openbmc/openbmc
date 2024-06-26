LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

RDEPENDS:${PN}:append = " \
    bash \
    btrfs-tools \
    "

SRC_URI += " \
    file://emmc-init \
    file://emmc-init.service \
    "

do_install:append() {
    install -d ${D}${libexecdir}/emmc-init
    install -m 0755 ${UNPACKDIR}/emmc-init ${D}${libexecdir}/emmc-init
}

SYSTEMD_SERVICE:${PN} += "emmc-init.service"
