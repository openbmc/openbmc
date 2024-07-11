SUMMARY = "An Open Source ODBC sub-system"
DESCRIPTION = "unixODBC is an Open Source ODBC sub-system and an ODBC SDK \
for Linux, Mac OSX, and UNIX."

HOMEPAGE = "http://www.unixodbc.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7b37bf80a3df5a65b355433ae36d206"

DEPENDS = "libtool readline"

SRC_URI = "https://www.unixodbc.org/unixODBC-${PV}.tar.gz \
           file://do-not-use-libltdl-source-directory.patch \
           file://0001-exe-Makefile.am-add-CROSS_LAUNCHER-to-run-odbc_confi.patch \
           file://CVE-2024-1013.patch \
"
SRC_URI[sha256sum] = "f210501445ce21bf607ba51ef8c125e10e22dffdffec377646462df5f01915ec"

UPSTREAM_CHECK_REGEX = "unixODBC-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools-brokensep multilib_header qemu

S = "${WORKDIR}/unixODBC-${PV}"

EXTRA_OEMAKE += "LIBS=-lltdl"
EXTRA_OECONF += "--enable-utf8ini"
DEPENDS:append:class-target = "${@' qemu-native' if bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', True, False, d) else ''}"
QEMU_WRAPPER = "${@qemu_wrapper_cmdline(d, '${STAGING_DIR_HOST}', ['${STAGING_DIR_HOST}/${libdir}','${STAGING_DIR_HOST}/${base_libdir}'])}"

do_configure:prepend() {
    # old m4 files will cause libtool version don't match
    rm -rf m4/*
    rm -fr libltdl
}

do_compile:prepend() {
    if ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'true', 'false', d)}; then
	export CROSS_LAUNCHER="${QEMU_WRAPPER} "
    fi
}

do_install:prepend() {
    if ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'true', 'false', d)}; then
	export CROSS_LAUNCHER="${QEMU_WRAPPER} "
    fi
}

do_install:append() {
    oe_multilib_header unixodbc.h unixODBC/unixodbc_conf.h
}
