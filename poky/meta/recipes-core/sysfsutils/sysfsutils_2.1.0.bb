SUMMARY = "Tools for working with sysfs"
DESCRIPTION = "Tools for working with the sysfs virtual filesystem.  The tool 'systool' can query devices by bus, class and topology."
HOMEPAGE = "http://linux-diag.sourceforge.net/Sysfsutils.html"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LICENSE:${PN} = "GPL-2.0-only"
LICENSE:libsysfs = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3d06403ea54c7574a9e581c6478cc393 \
                    file://cmd/GPL;md5=d41d4e2e1e108554e0388ea4aecd8d27 \
                    file://lib/LGPL;md5=b75d069791103ffe1c0d6435deeff72e"
PR = "r5"

SRC_URI = "git://github.com/linux-ras/sysfsutils.git;protocol=https;branch=master \
           file://sysfsutils-2.0.0-class-dup.patch \
           file://obsolete_automake_macros.patch \
           file://separatebuild.patch"

SRCREV = "0d5456e1c9d969cdad6accef2ae2d4881d5db085"

S = "${WORKDIR}/git"

inherit autotools

PACKAGES =+ "libsysfs"
FILES:libsysfs = "${libdir}/lib*${SOLIBS}"

export libdir = "${base_libdir}"
