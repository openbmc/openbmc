SUMMARY = "ISC Internet Domain Name Server"
HOMEPAGE = "http://www.isc.org/sw/bind/"
SECTION = "console/network"

LICENSE = "ISC & BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=6ba7c9fe0c888a943c79c93e6de744fb"

DEPENDS = "openssl libcap zlib"

SRC_URI = "https://ftp.isc.org/isc/bind9/${PV}/${BPN}-${PV}.tar.gz \
           file://conf.patch \
           file://named.service \
           file://bind9 \
           file://generate-rndc-key.sh \
           file://make-etc-initd-bind-stop-work.patch \
           file://init.d-add-support-for-read-only-rootfs.patch \
           file://bind-ensure-searching-for-json-headers-searches-sysr.patch \
           file://0001-gen.c-extend-DIRNAMESIZE-from-256-to-512.patch \
           file://0001-lib-dns-gen.c-fix-too-long-error.patch \
           file://0001-configure.in-remove-useless-L-use_openssl-lib.patch \
           file://0001-named-lwresd-V-and-start-log-hide-build-options.patch \
           file://0001-avoid-start-failure-with-bind-user.patch \
           file://CVE-2018-5740.patch \
"

SRC_URI[md5sum] = "9b4834d78f30cdb796ce437262272a36"
SRC_URI[sha256sum] = "595070b031f869f8939656b5a5d11b121211967f15f6afeafa895df745279617"

UPSTREAM_CHECK_URI = "https://ftp.isc.org/isc/bind9/"
UPSTREAM_CHECK_REGEX = "(?P<pver>9(\.\d+)+(-P\d+)*)/"

inherit autotools update-rc.d systemd useradd pkgconfig multilib_script

MULTILIB_SCRIPTS = "${PN}:${bindir}/bind9-config ${PN}:${bindir}/isc-config.sh"

# PACKAGECONFIGs readline and libedit should NOT be set at same time
PACKAGECONFIG ?= "readline"
PACKAGECONFIG[httpstats] = "--with-libxml2=${STAGING_DIR_HOST}${prefix},--without-libxml2,libxml2"
PACKAGECONFIG[readline] = "--with-readline=-lreadline,,readline"
PACKAGECONFIG[libedit] = "--with-readline=-ledit,,libedit"
PACKAGECONFIG[urandom] = "--with-randomdev=/dev/urandom,--with-randomdev=/dev/random,,"
PACKAGECONFIG[python3] = "--with-python=${PYTHON} --with-python-install-dir=${D}/${PYTHON_SITEPACKAGES_DIR} , --without-python, python3-ply-native,"

ENABLE_IPV6 = "--enable-ipv6=${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'yes', 'no', d)}"
EXTRA_OECONF = " ${ENABLE_IPV6} --with-libtool --enable-threads \
                 --disable-devpoll --enable-epoll --with-gost=no \
                 --with-gssapi=no --with-ecdsa=yes --with-eddsa=no \
                 --with-lmdb=no \
                 --sysconfdir=${sysconfdir}/bind \
                 --with-openssl=${STAGING_DIR_HOST}${prefix} \
               "

inherit ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3native distutils3-base', '', d)}

# dhcp needs .la so keep them
REMOVE_LIBTOOL_LA = "0"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home ${localstatedir}/cache/bind --no-create-home \
                       --user-group bind"

INITSCRIPT_NAME = "bind"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE_${PN} = "named.service"

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
        if ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'true', 'false', d)}; then
		sed -i -e '1s,#!.*python3,#! /usr/bin/python3,' \
		${D}${sbindir}/dnssec-coverage \
		${D}${sbindir}/dnssec-checkds \
		${D}${sbindir}/dnssec-keymgr
	fi

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

PACKAGE_BEFORE_PN += "${PN}-utils"
FILES_${PN}-utils = "${bindir}/host ${bindir}/dig ${bindir}/mdig"
FILES_${PN}-dev += "${bindir}/isc-config.h"
FILES_${PN} += "${sbindir}/generate-rndc-key.sh"

PACKAGE_BEFORE_PN += "${PN}-libs"
FILES_${PN}-libs = "${libdir}/*.so*"
FILES_${PN}-staticdev += "${libdir}/*.la"

PACKAGE_BEFORE_PN += "${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3-bind', '', d)}"
FILES_python3-bind = "${sbindir}/dnssec-coverage ${sbindir}/dnssec-checkds \
                ${sbindir}/dnssec-keymgr ${PYTHON_SITEPACKAGES_DIR}"

RDEPENDS_${PN} = "bash"
RDEPENDS_${PN}-utils = "bash"
RDEPENDS_${PN}-dev = ""
RDEPENDS_python3-bind = "python3-core python3-ply"
