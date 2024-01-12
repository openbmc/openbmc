SUMMARY = "A collection of powerful tools for manipulating EPROM load files."
SECTION = "devel"
LICENSE = "GPL-3.0-or-later & LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = " \
    https://sourceforge.net/projects/${BPN}/files/srecord/${@oe.utils.trim_version('${PV}', 2)}/${BP}-Source.tar.gz \
    file://0001-Disable-doxygen.patch \
    file://0001-cmake-Do-not-try-to-compute-library-dependencies-dur.patch \
    file://0001-cmake-respect-explicit-install-prefix.patch"
SRC_URI[sha256sum] = "81c3d07cf15ce50441f43a82cefd0ac32767c535b5291bcc41bd2311d1337644"
S = "${WORKDIR}/${BP}-Source"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/srecord/files/releases"

DEPENDS = "boost libgcrypt"

inherit cmake

BBCLASSEXTEND = "native"
