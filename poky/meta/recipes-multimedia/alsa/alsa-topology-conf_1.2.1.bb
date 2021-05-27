SUMMARY = "ALSA topology configuration files"
DESCRIPTION = "Provides a method for audio drivers to load their mixers, \
routing, PCMs and capabilities from user space at runtime without changing \
any driver source code."
HOMEPAGE = "https://alsa-project.org"
BUGTRACKER = "https://alsa-project.org/wiki/Bug_Tracking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20d74d74db9741697903372ad001d3b4"

# The tarball doesn't have any toplevel directory. The subdir option tells
# Bitbake to unpack the archive to the correct place.
SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2;subdir=${BP}"
SRC_URI[md5sum] = "7fdf5fff3f1e0603456e719f6033e922"
SRC_URI[sha256sum] = "354a43f4031c98bef1349ac722d83b2603ef439f81a1ab1eba8814c28243a9b2"

inherit allarch

do_install() {
        install -d "${D}${datadir}/alsa"
        cp -r "${S}/topology" "${D}${datadir}/alsa"
}

PACKAGES = "${PN}"

FILES_${PN} = "*"
