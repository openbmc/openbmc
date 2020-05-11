SUMMARY = "ALSA Use Case Manager configuration"
HOMEPAGE = "https://alsa-project.org"
BUGTRACKER = "https://alsa-project.org/wiki/Bug_Tracking"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20d74d74db9741697903372ad001d3b4"

# The tarball doesn't have any toplevel directory. The subdir option tells
# Bitbake to unpack the archive to the correct place.
SRC_URI = "https://www.alsa-project.org/files/pub/lib/${BP}.tar.bz2;subdir=${BP}"
SRC_URI[md5sum] = "b7fa43cfd79df978184a6333766d2a50"
SRC_URI[sha256sum] = "ea8a86875f4cf430d49a662a04a6d6c606c5c9d67e54cb944c4d77b24554062f"

inherit allarch

do_install() {
        install -d ${D}/usr/share/alsa
        cp -r ${S}/ucm ${D}/usr/share/alsa
        cp -r ${S}/ucm2 ${D}/usr/share/alsa
}

PACKAGES = "${PN}"

FILES_${PN} = "*"
