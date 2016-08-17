SUMMARY = "Tools for ethernet bridging"
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/networking/bridge"
SECTION = "net"
LICENSE = "GPLv2"

DEPENDS = "sysfsutils"

SRC_URI = "${SOURCEFORGE_MIRROR}/bridge/bridge-utils-${PV}.tar.gz \
          file://bridge-utils-1.5-check-error-returns-from-write-to-sysfs.patch \
          file://bridge-utils-1.5-fix-error-message-for-incorrect-command.patch \
          file://bridge-utils-1.5-fix-incorrect-command-in-manual.patch \
"

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
