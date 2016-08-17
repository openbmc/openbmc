SUMMARY = "Virtual EtherDrive blade AoE target"
SECTION = "admin"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "${SOURCEFORGE_MIRROR}/aoetools/${BPN}-${PV}.tgz \
       file://cross.patch"

SRC_URI[md5sum] = "3c80e4a6bc7d66ae0c235b88cb44bd59"
SRC_URI[sha256sum] = "c8fe2fc4f2fba8e07e5cfdf17335982584eef2cd5c78bf8b1db93f2b56e7121d"

inherit autotools-brokensep

do_install() {
    install -D -m 0755 ${S}/vblade ${D}/${sbindir}/vblade
    install -D -m 0755 ${S}/vbladed ${D}/${sbindir}/vbladed
    install -D -m 0644 ${S}/vblade.8 ${D}/${mandir}/man8/vblade.8
}

