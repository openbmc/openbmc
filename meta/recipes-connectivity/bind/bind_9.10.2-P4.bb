SUMMARY = "ISC Internet Domain Name Server"
HOMEPAGE = "http://www.isc.org/sw/bind/"
SECTION = "console/network"

LICENSE = "ISC & BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=0a95f52a0ab6c5f52dedc9a45e7abb3f"

DEPENDS = "openssl libcap"

SRC_URI = "ftp://ftp.isc.org/isc/bind9/${PV}/${BPN}-${PV}.tar.gz \
           file://conf.patch \
           file://make-etc-initd-bind-stop-work.patch \
           file://mips1-not-support-opcode.diff \
           file://dont-test-on-host.patch \
           file://generate-rndc-key.sh \
           file://named.service \
           file://bind9 \
           file://init.d-add-support-for-read-only-rootfs.patch \
           file://bind-confgen-build-unix.o-once.patch \
           file://0001-build-use-pkg-config-to-find-libxml2.patch \
           file://bind-ensure-searching-for-json-headers-searches-sysr.patch \
           file://0001-gen.c-extend-DIRNAMESIZE-from-256-to-512.patch \
           file://0001-lib-dns-gen.c-fix-too-long-error.patch \
           file://CVE-2015-8704.patch \
           file://CVE-2015-8705.patch \
           file://CVE-2015-8000.patch \
           file://CVE-2015-8461.patch \
           "

SRC_URI[md5sum] = "8b1f5064837756c938eadc1537dec5c7"
SRC_URI[sha256sum] = "c00b21ec1def212957f28efe9d10aac52d6ec515e84fbf2c42143f5d71429cb8"

ENABLE_IPV6 = "--enable-ipv6=${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'yes', 'no', d)}"
EXTRA_OECONF = " ${ENABLE_IPV6} --with-randomdev=/dev/random --disable-threads \
                 --disable-devpoll --disable-epoll --with-gost=no \
                 --with-gssapi=no --with-ecdsa=yes \
                 --sysconfdir=${sysconfdir}/bind \
                 --with-openssl=${STAGING_LIBDIR}/.. \
               "
inherit autotools update-rc.d systemd useradd pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[httpstats] = "--with-libxml2,--without-libxml2,libxml2"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home /var/cache/bind --no-create-home \
                       --user-group bind"

INITSCRIPT_NAME = "bind"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE_${PN} = "named.service"

PARALLEL_MAKE = ""

RDEPENDS_${PN} = "python-core"
RDEPENDS_${PN}-dev = ""

PACKAGE_BEFORE_PN += "${PN}-utils"
FILES_${PN}-utils = "${bindir}/host ${bindir}/dig"
FILES_${PN}-dev += "${bindir}/isc-config.h"
FILES_${PN} += "${sbindir}/generate-rndc-key.sh"

do_install_prepend() {
	# clean host path in isc-config.sh before the hardlink created
	# by "make install":
	#   bind9-config -> isc-config.sh
	sed -i -e "s,${STAGING_LIBDIR},${libdir}," ${B}/isc-config.sh
}

do_install_append() {
	rm "${D}${bindir}/nslookup"
	rm "${D}${mandir}/man1/nslookup.1"
	rmdir "${D}${localstatedir}/run"
	rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"
	install -d "${D}${localstatedir}/cache/bind"
	install -d "${D}${sysconfdir}/bind"
	install -d "${D}${sysconfdir}/init.d"
	install -m 644 ${S}/conf/* "${D}${sysconfdir}/bind/"
	install -m 755 "${S}/init.d" "${D}${sysconfdir}/init.d/bind"
	sed -i -e '1s,#!.*python,#! /usr/bin/env python,' ${D}${sbindir}/dnssec-coverage ${D}${sbindir}/dnssec-checkds

	# Install systemd related files
	install -d ${D}${localstatedir}/cache/bind
	install -d ${D}${sbindir}
	install -m 755 ${WORKDIR}/generate-rndc-key.sh ${D}${sbindir}
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/named.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
	       -e 's,@SBINDIR@,${sbindir},g' \
	       ${D}${systemd_unitdir}/system/named.service

	install -d ${D}${sysconfdir}/default
	install -m 0644 ${WORKDIR}/bind9 ${D}${sysconfdir}/default
}

CONFFILES_${PN} = " \
	${sysconfdir}/bind/named.conf \
	${sysconfdir}/bind/named.conf.local \
	${sysconfdir}/bind/named.conf.options \
	${sysconfdir}/bind/db.0 \
	${sysconfdir}/bind/db.127 \
	${sysconfdir}/bind/db.empty \
	${sysconfdir}/bind/db.local \
	${sysconfdir}/bind/db.root \
	"

