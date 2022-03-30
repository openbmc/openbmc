SUMMARY = "Filtering tool for a Linux-based bridging firewall"
HOMEPAGE = "http://sourceforge.net/projects/ebtables/"
DESCRIPTION = "Utility for basic Ethernet frame filtering on a Linux bridge, \
               advanced logging, MAC DNAT/SNAT and brouting."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=53b4a999993871a28ab1488fdbd2e73e"
SECTION = "net"

RDEPENDS:${PN} += "bash"

RRECOMMENDS:${PN} += "kernel-module-ebtables \
    "

SRC_URI = "http://ftp.netfilter.org/pub/ebtables/ebtables-${PV}.tar.gz \
           file://0001-Makefile.am-do-not-install-etc-ethertypes.patch \
           file://ebtables-legacy-save \
           file://ebtables.common \
           file://ebtables.service \
           "

SRC_URI:append:libc-musl = " file://0010-Adjust-header-include-sequence.patch"

SRC_URI[md5sum] = "071c8b0a59241667a0044fb040d4fc72"
SRC_URI[sha256sum] = "b71f654784a726329f88b412ef7b96b4e5d786ed2bd28193ed7b4c0d677dfd2a"

inherit systemd autotools

do_install:append () {
	# Replace upstream ebtables-save perl script with Fedora bash based rewrite
	# http://pkgs.fedoraproject.org/cgit/rpms/ebtables.git/tree/ebtables-save
	rm -f ${D}${sbindir}/ebtables-legacy-save
	install -m 0755 ${WORKDIR}/ebtables-legacy-save ${D}${sbindir}/ebtables-legacy-save

	# Install systemd service files
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${WORKDIR}/ebtables.service ${D}${systemd_unitdir}/system
		sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/ebtables.service
		install -m 0755 ${WORKDIR}/ebtables.common ${D}${sbindir}/ebtables.common
	fi

	install -d ${D}${base_sbindir}
	ln -sf ${sbindir}/ebtables-legacy ${D}${base_sbindir}/ebtables
}

do_configure:prepend () {
    ( cd ${S}; ./autogen.sh )
}

FILES:${PN}-dbg += "${base_libdir}/ebtables/.debug"
FILES:${PN} += "${base_libdir}/ebtables/*.so"

SYSTEMD_SERVICE:${PN} = "ebtables.service"
