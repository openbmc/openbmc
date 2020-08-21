SUMMARY = "ALSA topology configuration files"
HOMEPAGE = "https://alsa-project.org"
BUGTRACKER = "https://alsa-project.org/wiki/Bug_Tracking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20d74d74db9741697903372ad001d3b4"

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2"
SRC_URI[sha256sum] = "833f99b2cbda34e0cfef867ef1d2e6a74fe276bb7fc525a573be32077f629dff"

inherit allarch

do_install() {
        install -d "${D}${datadir}/alsa"
        cp -r "${S}/topology" "${D}${datadir}/alsa"
}

PACKAGES = "${PN}"

FILES_${PN} = "*"
