SUMMARY = "The GNU portability library"
DESCRIPTION = "A collection of software subroutines which are designed to \
be usable on many operating systems. The goal of the project \
is to make it easy for free software authors to make their \
software run on many operating systems. Since source is designed \
to be copied from gnulib, it is not a library per-se, as much \
as a collection of portable idioms to be used in other projects."

HOMEPAGE = "http://www.gnu.org/software/gnulib/"
SECTION = "devel"
LICENSE = "LGPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=56a22a6e5bcce45e2c8ac184f81412b5"
SRCREV = "c99c8d491850dc3a6e0b8604a2729d8bc5c0eff1"

SRC_URI = "git://git.savannah.gnu.org/git/gnulib.git;branch=stable-${PV};protocol=https \
"

S = "${WORKDIR}/git"

do_install () {
    install -d ${D}/${datadir}/gnulib
    cp --no-preserve=ownership --recursive ${S}/* ${D}/${datadir}/gnulib/
    cp --no-preserve=ownership --recursive ${S}/.git ${D}/${datadir}/gnulib/
}

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
deltask package_write_ipk
deltask package_write_deb
deltask package_write_rpm
deltask do_deploy_archives 

BBCLASSEXTEND = "native"
