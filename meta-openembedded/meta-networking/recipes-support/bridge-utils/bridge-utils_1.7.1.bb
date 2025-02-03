SUMMARY = "Tools for ethernet bridging"
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/networking/bridge"
SECTION = "net"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=f9d20a453221a1b7e32ae84694da2c37"

SRCREV = "75d949b9fae9718201422f0bd3d1103e67dd597c"

SRC_URI = "\
    git://git.kernel.org/pub/scm/network/bridge/bridge-utils.git;branch=main \
    file://0001-include-missing-kernel-header.patch \
    file://0002-build-don-t-ignore-CFLAGS-from-environment.patch \
    file://0003-libbridge-Modifying-the-AR-to-cross-toolchain.patch \
    file://0004-cleanup-includes.patch \
"

S = "${WORKDIR}/git"

DEPENDS = "sysfsutils"

CVE_PRODUCT = "kernel:bridge-utils"

inherit autotools-brokensep update-alternatives

ALTERNATIVE:${PN} = "brctl"
ALTERNATIVE_PRIORITY[brctl] = "100"
ALTERNATIVE_LINK_NAME[brctl] = "${sbindir}/brctl"

EXTRA_OECONF = "--with-linux-headers=${STAGING_INCDIR}"

do_install:append () {
    install -d ${D}/${datadir}/bridge-utils
    install -d ${D}/${sysconfdir}/network/if-pre-up.d
    install -d ${D}/${sysconfdir}/network/if-post-down.d
}

RRECOMMENDS:${PN} = "kernel-module-bridge"
