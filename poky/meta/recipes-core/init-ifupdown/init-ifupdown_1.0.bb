SUMMARY = "Basic TCP/IP networking init scripts and configuration files"
DESCRIPTION = "This package provides high level tools to configure network interfaces"
HOMEPAGE = "http://packages.debian.org/ifupdown"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/copyright;md5=3dd6192d306f582dee7687da3d8748ab"
PR = "r7"

inherit update-rc.d

INITSCRIPT_NAME = "networking"
INITSCRIPT_PARAMS = "start 01 2 3 4 5 . stop 80 0 6 1 ."

SRC_URI = "file://copyright \
           file://init \
           file://interfaces \
           file://nfsroot"

S = "${WORKDIR}"

do_install () {
	install -d ${D}${sysconfdir}/init.d \
			${D}${sysconfdir}/network/if-pre-up.d \
			${D}${sysconfdir}/network/if-up.d \
			${D}${sysconfdir}/network/if-down.d \
			${D}${sysconfdir}/network/if-post-down.d
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/networking
	install -m 0644 ${WORKDIR}/interfaces ${D}${sysconfdir}/network/interfaces
	install -m 0755 ${WORKDIR}/nfsroot ${D}${sysconfdir}/network/if-pre-up.d
}

do_install_append_qemuall () {
	# Disable network manager on machines that commonly do NFS booting
	touch ${D}${sysconfdir}/network/nm-disabled-eth0
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
RDEPENDS_${PN} = "netbase"
RCONFLICTS_${PN} = "netbase (< 1:5.0)"

CONFFILES_${PN} = "${sysconfdir}/network/interfaces"
