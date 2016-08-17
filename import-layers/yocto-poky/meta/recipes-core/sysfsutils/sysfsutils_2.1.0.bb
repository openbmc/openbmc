SUMMARY = "Tools for working with sysfs"
DESCRIPTION = "Tools for working with the sysfs virtual filesystem.  The tool 'systool' can query devices by bus, class and topology."
HOMEPAGE = "http://linux-diag.sourceforge.net/Sysfsutils.html"

LICENSE = "GPLv2 & LGPLv2.1"
LICENSE_${PN} = "GPLv2"
LICENSE_libsysfs = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=3d06403ea54c7574a9e581c6478cc393 \
                    file://cmd/GPL;md5=d41d4e2e1e108554e0388ea4aecd8d27 \
                    file://lib/LGPL;md5=b75d069791103ffe1c0d6435deeff72e"
PR = "r5"

SRC_URI = "${SOURCEFORGE_MIRROR}/linux-diag/sysfsutils-${PV}.tar.gz \
           file://sysfsutils-2.0.0-class-dup.patch \
           file://obsolete_automake_macros.patch \
           file://separatebuild.patch"

SRC_URI[md5sum] = "14e7dcd0436d2f49aa403f67e1ef7ddc"
SRC_URI[sha256sum] = "e865de2c1f559fff0d3fc936e660c0efaf7afe662064f2fb97ccad1ec28d208a"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/linux-diag/files/sysfsutils/"
UPSTREAM_CHECK_REGEX = "/sysfsutils/(?P<pver>(\d+[\.\-_]*)+)/"

S = "${WORKDIR}/sysfsutils-${PV}"

inherit autotools

PACKAGES =+ "libsysfs"
FILES_libsysfs = "${libdir}/lib*${SOLIBS}"
FILES_libsysfs-dev = "${libdir}/lib*${SOLIBSDEV} ${includedir}"
FILES_libsysfs-staticdev = "${libdir}/lib*.a"

export libdir = "${base_libdir}"
