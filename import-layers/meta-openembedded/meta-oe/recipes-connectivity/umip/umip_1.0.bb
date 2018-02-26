SUMMARY = "Mobile IPv6 and NEMO for Linux"
DESCRIPTION = "UMIP is an open source implementation of Mobile IPv6 and NEMO \
Basic Support for Linux. It is released under the GPLv2 license. It supports \
the following IETF RFC: RFC6275 (Mobile IPv6), RFC3963 (NEMO), RFC3776 and \
RFC4877 (IPsec and IKEv2)."
HOMEPAGE = "http://umip.org/"
SECTION = "System Environment/Base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=073dc31ccb2ebed70db54f1e8aeb4c33"
DEPENDS = "rpm indent-native"

SRC_URI = "git://github.com/jlanza/umip \
           file://add-dependency-to-support-parallel-compilation.patch \
           file://mip6d \
           file://mip6d.service \
           file://0001-Add-format-string-to-fprintf-call.patch \
           "
SRCREV = "7d67209cd1bba2dd0e183a0fa07eeef07964dd14"

S = "${WORKDIR}/git"

EXTRA_OECONF = "--enable-vt"

inherit autotools-brokensep systemd update-rc.d

INITSCRIPT_NAME = "mip6d"
INITSCRIPT_PARAMS = "start 64 . stop 36 0 1 2 3 4 5 6 ."

SYSTEMD_SERVICE_${PN} = "mip6d.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_install_append() {
	install -D -m 0755 ${WORKDIR}/mip6d ${D}${sysconfdir}/init.d/mip6d
	install -D -m 0644 ${WORKDIR}/mip6d.service ${D}${systemd_system_unitdir}/mip6d.service
	sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' \
	    -e 's,@SBINDIR@,${sbindir},g' \
	    ${D}${systemd_system_unitdir}/mip6d.service
}

RRECOMMENDS_${PN} = "kernel-module-mip6 kernel-module-ipv6"
