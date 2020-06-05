SUMMARY = "ALSA topology configuration files"
HOMEPAGE = "https://alsa-project.org"
BUGTRACKER = "https://alsa-project.org/wiki/Bug_Tracking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20d74d74db9741697903372ad001d3b4"

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2"
SRC_URI[sha256sum] = "b472d6b567c78173bd69543d9cffc9e379c80eb763c3afc8d5b24d5610d19425"

inherit allarch

do_install() {
        install -d ${D}/usr/share/alsa
        cp -r ${S}/topology ${D}/usr/share/alsa
}

PACKAGES = "${PN}"

FILES_${PN} = "*"
