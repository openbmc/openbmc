SUMMARY = "Library for BPF handling"
DESCRIPTION = "Library for BPF handling"
HOMEPAGE = "https://github.com/libbpf/libbpf"
SECTION = "libs"
LICENSE = "LGPLv2.1+"

LIC_FILES_CHKSUM = "file://../LICENSE.LGPL-2.1;md5=b370887980db5dd40659b50909238dbd"

DEPENDS = "zlib elfutils"

SRC_URI = "git://github.com/libbpf/libbpf.git;protocol=https;branch=master"
SRCREV = "5579664205e42194e1921d69d0839f660c801a4d"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_HOST = "(x86_64|i.86|aarch64).*-linux"

S = "${WORKDIR}/git/src"

EXTRA_OEMAKE += "DESTDIR=${D} LIBDIR=${libdir} INCLUDEDIR=${includedir}"

inherit pkgconfig

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install
}
