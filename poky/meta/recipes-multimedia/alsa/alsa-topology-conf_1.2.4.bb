SUMMARY = "ALSA topology configuration files"
DESCRIPTION = "Provides a method for audio drivers to load their mixers, \
routing, PCMs and capabilities from user space at runtime without changing \
any driver source code."
HOMEPAGE = "https://alsa-project.org"
BUGTRACKER = "https://alsa-project.org/wiki/Bug_Tracking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20d74d74db9741697903372ad001d3b4"

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2"
SRC_URI[sha256sum] = "55e0e6e42eca4cc7656c257af2440cdc65b83689dca49fc60ca0194db079ed07"

inherit allarch

do_install() {
        install -d "${D}${datadir}/alsa"
        cp -r "${S}/topology" "${D}${datadir}/alsa"
}

PACKAGES = "${PN}"

FILES_${PN} = "*"
