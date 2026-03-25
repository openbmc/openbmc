LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN}:append = " \
    bash \
    btrfs-tools \
    "

SRC_URI += " \
    file://emmc-init \
    file://emmc-init.service \
    "

do_install:append() {
    install -D -m 0755 ${UNPACKDIR}/emmc-init ${D}${libexecdir}/${BPN}/emmc-init
    install -D -m 0644 ${UNPACKDIR}/emmc-init.service ${D}${systemd_system_unitdir}/emmc-init.service
}

FILES:${PN}:append = " ${systemd_system_unitdir}/emmc-init.service"

SYSTEMD_SERVICE:${PN} += "emmc-init.service"
