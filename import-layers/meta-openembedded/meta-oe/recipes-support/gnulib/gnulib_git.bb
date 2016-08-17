SUMMARY = "The GNU portability library"
DESCRIPTION = "A collection of software subroutines which are designed to \
be usable on many operating systems. The goal of the project \
is to make it easy for free software authors to make their \
software run on many operating systems. Since source is designed \
to be copied from gnulib, it is not a library per-se, as much \
as a collection of portable idioms to be used in other projects."

HOMEPAGE = "http://www.gnu.org/software/gnulib/"
SECTION = "devel"
LICENSE = "LGPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=e4cf3810f33a067ea7ccd2cd889fed21"
SRCREV = "24379a9217fa4bd62685795aaaa010fd90ced9e3"
SRC_URI = "git://git.sv.gnu.org/gnulib;protocol=git \
"
S = "${WORKDIR}/git"

do_install () {
    cd ${S}
    git checkout master
    git clone ${S} ${D}/${datadir}/gnulib
}

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

BBCLASSEXTEND = "native"
