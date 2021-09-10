SUMMARY = "Library for BPF handling"
DESCRIPTION = "Library for BPF handling"
HOMEPAGE = "https://github.com/libbpf/libbpf"
SECTION = "libs"
LICENSE = "LGPLv2.1+"

LIC_FILES_CHKSUM = "file://../LICENSE.LGPL-2.1;md5=b370887980db5dd40659b50909238dbd"

DEPENDS = "zlib elfutils"

do_compile[depends] += "virtual/kernel:do_shared_workdir"

SRC_URI = "git://github.com/libbpf/libbpf.git;protocol=https"
SRCREV = "db9614b6bd69746809d506c2786f914b0f812c37"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64).*-linux"

S = "${WORKDIR}/git/src"

EXTRA_OEMAKE += "DESTDIR=${D} LIBDIR=${libdir} INCLUDEDIR=${includedir}"

inherit pkgconfig

do_compile() {
	if grep -q "CONFIG_BPF_SYSCALL=y" ${STAGING_KERNEL_BUILDDIR}/.config
	then
		oe_runmake
	else
		bbnote "BFP syscall is not enabled"
	fi
}

do_install() {
	if grep -q "CONFIG_BPF_SYSCALL=y" ${STAGING_KERNEL_BUILDDIR}/.config
	then
		oe_runmake install
	else
		bbnote "no files to install"
	fi
}
