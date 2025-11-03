LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = " \
        http://www.litech.org/tayga/tayga-${PV}.tar.bz2 \
        file://tayga.conf \
        file://tayga.service \
        file://0001-include-sys-uio.patch;striplevel=0 \
        "
SRC_URI[sha256sum] = "2b1f7927a9d2dcff9095aff3c271924b052ccfd2faca9588b277431a44f0009c"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "tayga.service"

EXTRA_OEMAKE += "CFLAGS='${CFLAGS}'"

do_install:append() {
  install -m 0644 ${WORKDIR}/tayga.conf ${D}${sysconfdir}/tayga.conf
  install -d ${D}${systemd_unitdir}/system/
  install -m 0644 ${WORKDIR}/tayga.service ${D}${systemd_unitdir}/system/
}

inherit autotools systemd

