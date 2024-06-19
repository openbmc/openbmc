SUMMARY = "Basic TCP/IP networking init scripts and configuration files"
DESCRIPTION = "This package provides high level tools to configure network interfaces"
HOMEPAGE = "http://packages.debian.org/ifupdown"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${S}/copyright;md5=3dd6192d306f582dee7687da3d8748ab"

inherit update-rc.d

INITSCRIPT_NAME = "networking"
INITSCRIPT_PARAMS = "start 01 2 3 4 5 . stop 80 0 6 1 ."

SRC_URI = "file://copyright \
           file://init \
           file://interfaces \
           file://nfsroot"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install () {
	install -d ${D}${sysconfdir}/init.d \
			${D}${sysconfdir}/network/if-pre-up.d \
			${D}${sysconfdir}/network/if-up.d \
			${D}${sysconfdir}/network/if-down.d \
			${D}${sysconfdir}/network/if-post-down.d
	install -m 0755 ${S}/init ${D}${sysconfdir}/init.d/networking
	install -m 0644 ${S}/interfaces ${D}${sysconfdir}/network/interfaces
	install -m 0755 ${S}/nfsroot ${D}${sysconfdir}/network/if-pre-up.d
}

do_install:append:qemuall () {
	# Disable network manager on machines that commonly do NFS booting
	touch ${D}${sysconfdir}/network/nm-disabled-eth0
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
RDEPENDS:${PN} = "netbase"
RCONFLICTS:${PN} = "netbase (< 1:5.0)"

CONFFILES:${PN} = "${sysconfdir}/network/interfaces"
