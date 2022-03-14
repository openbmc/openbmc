# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A library which provides glibc-compatible APIs for use on musl libc systems"
HOMEPAGE = "https://git.adelielinux.org/adelie/gcompat"

LICENSE = "NCSA"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eb33ef4af05a9c7602843afb7adfe792"

SRC_URI = "git://git.adelielinux.org/adelie/gcompat.git;protocol=https;branch=current"

PV = "1.0.0+1.1+git${SRCPV}"
SRCREV = "af5a49e489fdc04b9cf02547650d7aeaccd43793"

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
	oe_runmake install 'DESTDIR=${D}'
	if [ "${SITEINFO_BITS}" = "64" ]; then
		install -d ${D}/lib64
		ln -rs ${D}${GLIBC_LDSO} ${D}/lib64/`basename ${GLIBC_LDSO}`
	fi
}

FILES:${PN} += "/lib64"

INSANE_SKIP:${PN} = "libdir"

RPROVIDES:${PN} += "musl-glibc-compat"
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"

UPSTREAM_CHECK_COMMITS = "1"
