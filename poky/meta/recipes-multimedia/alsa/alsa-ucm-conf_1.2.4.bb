SUMMARY = "ALSA Use Case Manager configuration"
DESCRIPTION = "This package contains ALSA Use Case Manager configuration \
of audio input/output names and routing for specific audio hardware. \
They can be used with the alsaucm tool. "
HOMEPAGE = "https://alsa-project.org"
BUGTRACKER = "https://alsa-project.org/wiki/Bug_Tracking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20d74d74db9741697903372ad001d3b4"

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2"
SRC_URI[sha256sum] = "2c3b535c77dcb9aaf62a61f4f8324f1ab184162f105f7ec9ed1e37c742fcd340"

inherit allarch

do_install() {
        install -d "${D}${datadir}/alsa"
        cp -r "${S}/ucm" "${D}${datadir}/alsa"
        cp -r "${S}/ucm2" "${D}${datadir}/alsa"
}

PACKAGES = "${PN}"

FILES_${PN} = "*"
