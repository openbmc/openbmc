SUMMARY = "ALSA topology configuration files"
DESCRIPTION = "Provides a method for audio drivers to load their mixers, \
routing, PCMs and capabilities from user space at runtime without changing \
any driver source code."
HOMEPAGE = "https://alsa-project.org"
BUGTRACKER = "https://alsa-project.org/wiki/Bug_Tracking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20d74d74db9741697903372ad001d3b4"

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2"
SRC_URI[sha256sum] = "f7c5bae1545abcd73824bc97f4e72c340e11abea188ba0f1c06f5e0ad776b179"
# Something went wrong at upstream tarballing

inherit allarch

do_install() {
        install -d "${D}${datadir}/alsa"
        cp -r "${S}/topology" "${D}${datadir}/alsa"
}

PACKAGES = "${PN}"

FILES:${PN} = "*"
