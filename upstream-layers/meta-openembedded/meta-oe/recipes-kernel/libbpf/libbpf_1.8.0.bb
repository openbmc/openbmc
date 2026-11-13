SUMMARY = "Library for BPF handling"
DESCRIPTION = "Library for BPF handling"
HOMEPAGE = "https://github.com/libbpf/libbpf"
SECTION = "libs"
LICENSE = "LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "file://../LICENSE.LGPL-2.1-only;md5=21de888f1891efa635f1706a7ad3886b"

DEPENDS = "zlib elfutils"

SRC_URI = "git://github.com/libbpf/libbpf.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "85c0d575a447651bbeb8e2376eacd64c30dce9a6"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${UNPACKDIR}/${BP}/src"

EXTRA_OEMAKE += "DESTDIR=${D} LIBDIR=${libdir} INCLUDEDIR=${includedir}"
EXTRA_OEMAKE:append:class-native = " UAPIDIR=${includedir}"

inherit pkgconfig

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install
}

do_install:append:class-native() {
	oe_runmake install_uapi_headers
}

BBCLASSEXTEND = "native nativesdk"
