SUMMARY = "ALSA Use Case Manager configuration"
DESCRIPTION = "This package contains ALSA Use Case Manager configuration \
of audio input/output names and routing for specific audio hardware. \
They can be used with the alsaucm tool. "
HOMEPAGE = "https://alsa-project.org"
BUGTRACKER = "https://alsa-project.org/wiki/Bug_Tracking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20d74d74db9741697903372ad001d3b4"

SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2"
SRC_URI += "file://0001-Qualcomm-qcs615-Remove-JackControl-from-TALOS-EVK-Hi.patch"
SRC_URI += "file://0001-ucm2-Qualcomm-sa8775p-Remove-Fixed-channel-setting-f.patch"
SRC_URI += "file://0002-ucm2-Qualcomm-qcs8300-Remove-Fixed-channel-setting-f.patch"
SRC_URI[sha256sum] = "9f79e813c08fc86cfa46dd75c4fcda1a4a51b482db2607e1fcfaafb92f588a31"
# Something went wrong at upstream tarballing

inherit allarch

do_install() {
        install -d "${D}${datadir}/alsa"
        cp -r "${S}/ucm" "${D}${datadir}/alsa"
        cp -r "${S}/ucm2" "${D}${datadir}/alsa"
}

PACKAGES = "${PN}"

FILES:${PN} = "*"
