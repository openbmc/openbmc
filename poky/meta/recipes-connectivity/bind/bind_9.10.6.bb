SUMMARY = "ISC Internet Domain Name Server"
HOMEPAGE = "http://www.isc.org/sw/bind/"
SECTION = "console/network"

LICENSE = "ISC & BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=dba46507446198119bcde32a4feaab43"

DEPENDS = "openssl libcap"

SRC_URI = "https://ftp.isc.org/isc/bind9/${PV}/${BPN}-${PV}.tar.gz \
           file://conf.patch \
           file://make-etc-initd-bind-stop-work.patch \
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
           file://use-python3-and-fix-install-lib-path.patch \
           "

SRC_URI[md5sum] = "84e663284b17aee0df1ce6f248b137d7"
SRC_URI[sha256sum] = "17bbcd2bd7b1d32f5ba4b30d5dbe8a39bce200079048073d1e0d050fdf47e69d"

UPSTREAM_CHECK_URI = "https://ftp.isc.org/isc/bind9/"
UPSTREAM_CHECK_REGEX = "(?P<pver>9(\.\d+)+(-P\d+)*)/"


ENABLE_IPV6 = "--enable-ipv6=${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'yes', 'no', d)}"
EXTRA_OECONF = " ${ENABLE_IPV6} --with-libtool --enable-threads \
                 --disable-devpoll --enable-epoll --with-gost=no \
                 --with-gssapi=no --with-ecdsa=yes \
                 --sysconfdir=${sysconfdir}/bind \
                 --with-openssl=${STAGING_LIBDIR}/.. \
               "

inherit autotools update-rc.d systemd useradd pkgconfig python3-dir

export PYTHON_SITEPACKAGES_DIR

# PACKAGECONFIGs readline and libedit should NOT be set at same time
PACKAGECONFIG ?= "readline"
PACKAGECONFIG[httpstats] = "--with-libxml2,--without-libxml2,libxml2"
PACKAGECONFIG[readline] = "--with-readline=-lreadline,,readline"
PACKAGECONFIG[libedit] = "--with-readline=-ledit,,libedit"
PACKAGECONFIG[urandom] = "--with-randomdev=/dev/urandom,--with-randomdev=/dev/random,,"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home ${localstatedir}/cache/bind --no-create-home \
                       --user-group bind"

INITSCRIPT_NAME = "bind"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE_${PN} = "named.service"

PARALLEL_MAKE = ""

RDEPENDS_${PN} = "python3-core"
RDEPENDS_${PN}-dev = ""

PACKAGE_BEFORE_PN += "${PN}-utils"
FILES_${PN}-utils = "${bindir}/host ${bindir}/dig"
FILES_${PN}-dev += "${bindir}/isc-config.h"
FILES_${PN} += "${sbindir}/generate-rndc-key.sh ${PYTHON_SITEPACKAGES_DIR}"

PACKAGE_BEFORE_PN += "${PN}-libs"
FILES_${PN}-libs = "${libdir}/*.so*"

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
	install -d -o bind "${D}${localstatedir}/cache/bind"
	install -d "${D}${sysconfdir}/bind"
	install -d "${D}${sysconfdir}/init.d"
	install -m 644 ${S}/conf/* "${D}${sysconfdir}/bind/"
	install -m 755 "${S}/init.d" "${D}${sysconfdir}/init.d/bind"
	sed -i -e '1s,#!.*python3,#! /usr/bin/python3,' ${D}${sbindir}/dnssec-coverage ${D}${sbindir}/dnssec-checkds

	# Install systemd related files
	install -d ${D}${sbindir}
	install -m 755 ${WORKDIR}/generate-rndc-key.sh ${D}${sbindir}
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/named.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
	       -e 's,@SBINDIR@,${sbindir},g' \
	       ${D}${systemd_unitdir}/system/named.service

	install -d ${D}${sysconfdir}/default
	install -m 0644 ${WORKDIR}/bind9 ${D}${sysconfdir}/default

	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/tmpfiles.d
		echo "d /run/named 0755 bind bind - -" > ${D}${sysconfdir}/tmpfiles.d/bind.conf
	fi

    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/isc/*.pyc
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

