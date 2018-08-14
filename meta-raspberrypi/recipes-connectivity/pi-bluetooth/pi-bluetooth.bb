SUMMARY = "Script to properly configure BT-HCI on Raspberry Pi"
HOMEPAGE = "https://github.com/RPi-Distro/pi-bluetooth"
SECTION = "kernel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "\
    file://debian/copyright;md5=6af8de3c8ee71f8e91e9b22f84ff2022 \
"

SRC_URI = "git://github.com/RPi-Distro/pi-bluetooth"
SRCREV = "2a7477966bb3c69838b224f3ea92cb49a88124d5"
UPSTREAM_VERSION_UNKNOWN = "1"

S = "${WORKDIR}/git"

inherit allarch

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/usr/bin/btuart ${D}${bindir}
}

FILES_${PN} = "${bindir}"
