SUMMARY = "Xilinx's fork of a fast open source processor emulator"
HOMEPAGE = "https://github.com/xilinx/qemu/"

QEMU_TARGETS = "aarch64 arm microblaze microblazeel"

XILINX_RELEASE_VERSION = "v2019.1"
XILINX_QEMU_VERSION ?= "v2.11.1"
BRANCH ?= "branch/xilinx-v2019.1"
SRCREV ?= "5f38ea92fb697b94ad43f01fe162f3ed6e6b0e16"


LIC_FILES_CHKSUM = " \
		file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
		file://COPYING.LIB;endline=24;md5=c04def7ae38850e7d3ef548588159913 \
		"
DEPENDS = "glib-2.0 zlib pixman"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PV = "${XILINX_QEMU_VERSION}-xilinx-${XILINX_RELEASE_VERSION}+git${SRCPV}"
BRANCH ?= ""
REPO ?= "git://github.com/Xilinx/qemu.git;protocol=https"

BRANCHARG = "${@['nobranch=1', 'branch=${BRANCH}'][d.getVar('BRANCH', True) != '']}"
SRC_URI = "${REPO};${BRANCHARG}"

S = "${WORKDIR}/git"

# Disable KVM completely
PACKAGECONFIG_remove = "kvm"

# Enable libgcrypt
PACKAGECONFIG_append = " gcrypt"

DISABLE_STATIC_pn-${PN} = ""

PTEST_ENABLED = ""

# append a suffix dir, to allow multiple versions of QEMU to be installed
EXTRA_OECONF_append = " \
		--bindir=${bindir}/qemu-xilinx \
		--libexecdir=${libexecdir}/qemu-xilinx \
		"

do_configure_prepend() {
	# rewrite usage of 'libgcrypt-config' with 'pkg-config libgcrypt'
	sed -r -i 's/libgcrypt-config(\s*--)/pkg-config libgcrypt\1/g' ${S}/configure
}

do_install_append() {
	# Prevent QA warnings about installed ${localstatedir}/run
	if [ -d ${D}${localstatedir}/run ]; then rmdir ${D}${localstatedir}/run; fi
}