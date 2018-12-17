SUMMARY = "Error - Error/exception handling in an OO-ish way"
DESCRIPTION = "The Error package provides two interfaces. Firstly \
Error provides a procedural interface to exception handling. \
Secondly Error is a base class for errors/exceptions that can \
either be thrown, for subsequent catch, or can simply be recorded."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=8f3499d09ee74a050c0319391ff9d100"

DEPENDS += "perl"

SRC_URI = "http://cpan.metacpan.org/authors/id/S/SH/SHLOMIF/Error-${PV}.tar.gz"

SRC_URI[md5sum] = "0dcd94640f617df02b6d6c1e4e92018c"
SRC_URI[sha256sum] = "37590a962cd73ae03470e1ff16459a6cbc5273fc57626b8981dab9c2433155d9"

S = "${WORKDIR}/Error-${PV}"

inherit cpan ptest-perl

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

do_install_prepend() {
	# test requires "-T" (taint) command line option
	rm -rf ${B}/t/pod-coverage.t
}

BBCLASSEXTEND = "native"
