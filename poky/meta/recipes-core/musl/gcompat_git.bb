# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A library which provides glibc-compatible APIs for use on musl libc systems"
HOMEPAGE = "https://git.adelielinux.org/adelie/gcompat"

LICENSE = "NCSA"
LIC_FILES_CHKSUM = "file://LICENSE;md5=802b1aed7330d90086be4de63a3188e3"

SRC_URI = "git://git.adelielinux.org/adelie/gcompat.git;protocol=https;branch=current \
           file://0001-Add-fcntl64-wrapper.patch \
           "
SRC_URI:append:powerpc = "\
           file://0001-make-Static-PIE-does-not-work-on-musl-ppc.patch \
           "
PV = "1.1.0"
SRCREV = "b7bfe0b08c52fdc72e0c1d9d4dcb2129f1642bd6"

S = "${WORKDIR}/git"

inherit pkgconfig linuxloader siteinfo

DEPENDS += "musl-obstack"

GLIBC_LDSO = "${@get_glibc_loader(d)}"
MUSL_LDSO = "${@get_musl_loader(d)}"

EXTRA_OEMAKE = "LINKER_PATH=${MUSL_LDSO} \
                LOADER_NAME=`basename ${GLIBC_LDSO}` \
                "

do_configure () {
	:
}

do_compile () {
	oe_runmake
}

do_install () {
	oe_runmake install 'DESTDIR=${D}${root_prefix}'
	if [ "${SITEINFO_BITS}" = "64" ]; then
		install -d ${D}${nonarch_base_libdir}${SITEINFO_BITS}
		ln -rs ${D}${GLIBC_LDSO} ${D}${nonarch_base_libdir}${SITEINFO_BITS}/`basename ${GLIBC_LDSO}`
	fi
	install -d ${D}${libdir}
	ln -sf ${base_libdir}/libgcompat.so.0 ${D}${libdir}/libgcompat.so
}

FILES:${PN} += "${nonarch_base_libdir}${SITEINFO_BITS}"

INSANE_SKIP:${PN} = "libdir"

RPROVIDES:${PN} += "musl-glibc-compat"
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"

UPSTREAM_CHECK_COMMITS = "1"
