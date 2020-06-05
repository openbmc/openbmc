SUMMARY = "ALSA Use Case Manager configuration"
HOMEPAGE = "https://alsa-project.org"
BUGTRACKER = "https://alsa-project.org/wiki/Bug_Tracking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20d74d74db9741697903372ad001d3b4"

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2"
SRC_URI[sha256sum] = "7ebfd929bc85a51f16fa3c8c4db13faa2ea6ff2b2266fc36d6198bdafe73c40c"

inherit allarch

do_install() {
        install -d ${D}/usr/share/alsa
        cp -r ${S}/ucm ${D}/usr/share/alsa
        cp -r ${S}/ucm2 ${D}/usr/share/alsa
}

PACKAGES = "${PN}"

FILES_${PN} = "*"
