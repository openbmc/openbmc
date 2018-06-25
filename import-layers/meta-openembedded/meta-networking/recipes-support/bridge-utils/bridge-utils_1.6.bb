SUMMARY = "Tools for ethernet bridging"
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/networking/bridge"
SECTION = "net"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=f9d20a453221a1b7e32ae84694da2c37"

SRCREV = "42c1aefc303fdf891fbb099ea51f00dca83ab606"

SRC_URI = "\
    git://git.kernel.org/pub/scm/linux/kernel/git/shemminger/bridge-utils.git \
    file://kernel-headers.patch \
    file://0005-build-don-t-ignore-CFLAGS-from-environment.patch \
    file://0006-libbridge-Modifying-the-AR-to-cross-toolchain.patch \
"

S = "${WORKDIR}/git"

DEPENDS = "sysfsutils"

inherit autotools-brokensep update-alternatives

ALTERNATIVE_${PN} = "brctl"
ALTERNATIVE_PRIORITY[brctl] = "100"
ALTERNATIVE_LINK_NAME[brctl] = "${sbindir}/brctl"

EXTRA_OECONF = "--with-linux-headers=${STAGING_INCDIR}"

do_install_append () {
    install -d ${D}/${datadir}/bridge-utils
    install -d ${D}/${sysconfdir}/network/if-pre-up.d
    install -d ${D}/${sysconfdir}/network/if-post-down.d
}

RRECOMMENDS_${PN} = "kernel-module-bridge"
