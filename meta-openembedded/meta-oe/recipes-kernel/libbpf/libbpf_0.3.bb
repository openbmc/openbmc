SUMMARY = "Library for BPF handling"
DESCRIPTION = "Library for BPF handling"
HOMEPAGE = "https://github.com/libbpf/libbpf"
SECTION = "libs"
LICENSE = "LGPLv2.1+"

# There is a typo in the filename, LPGL should really be LGPL.
# Keep this until the correct name is set upstream.
LIC_FILES_CHKSUM = "file://../LICENSE.LPGL-2.1;md5=b370887980db5dd40659b50909238dbd"

DEPENDS = "zlib elfutils"

SRC_URI = "git://github.com/libbpf/libbpf.git;protocol=https"
SRCREV = "051a4009f94d5633a8f734ca4235f0a78ee90469"

# Backported from version 0.4
SRC_URI += "file://0001-install-don-t-preserve-file-owner.patch"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64).*-linux"

S = "${WORKDIR}/git/src"

EXTRA_OEMAKE += "DESTDIR=${D} LIBDIR=${libdir}"

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

BBCLASSEXTEND = "native"
