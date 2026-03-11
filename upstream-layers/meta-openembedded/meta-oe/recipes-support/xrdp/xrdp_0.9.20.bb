SUMMARY = "An open source remote desktop protocol(rdp) server."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=72cfbe4e7bd33a0a1de9630c91195c21 \
"

inherit features_check autotools pkgconfig useradd systemd

DEPENDS = "openssl virtual/libx11 libxfixes libxrandr libpam nasm-native imlib2 pixman libsm"

REQUIRED_DISTRO_FEATURES = "x11 pam"

SRC_URI = "https://github.com/neutrinolabs/${BPN}/releases/download/v${PV}/${BPN}-${PV}.tar.gz \
           file://xrdp.sysconfig \
           file://0001-Added-req_distinguished_name-in-etc-xrdp-openssl.con.patch \
           file://0001-Fix-the-compile-error.patch \
           file://0001-arch-Define-NO_NEED_ALIGN-on-ppc64.patch \
           file://0001-mark-count-with-unused-attribute.patch \
           "

SRC_URI[sha256sum] = "db693401da95b71b4d4e4c99aeb569a546dbdbde343f6d3302b0c47653277abb"

UPSTREAM_CHECK_URI = "https://github.com/neutrinolabs/xrdp/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

CFLAGS += " -Wno-deprecated-declarations"

PACKAGECONFIG ??= ""
PACKAGECONFIG[fuse] = " --enable-fuse, --disable-fuse, fuse"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system xrdp"
USERADD_PARAM:${PN}  = "--system --home /var/run/xrdp -g xrdp \
                        --no-create-home --shell /bin/false xrdp"

FILES:${PN} += "${datadir}/dbus-1/services/*.service \
                ${datadir}/dbus-1/accessibility-services/*.service "

FILES:${PN}-dev += "${libdir}/xrdp/libcommon.so \
                    ${libdir}/xrdp/libxrdp.so \
                    ${libdir}/xrdp/libscp.so \
                    ${libdir}/xrdp/libxrdpapi.so "

EXTRA_OECONF = "--enable-pam-config=suse --enable-fuse \
                --enable-pixman --enable-painter --enable-vsock \
                --enable-ipv6 --with-imlib2 --with-socketdir=${localstatedir}/run/${PN}"

do_configure:prepend() {
    cd ${S}
    ./bootstrap
    cd -
}

do_compile:prepend() {
    sed -i 's/(MAKE) $(AM_MAKEFLAGS) install-exec-am install-data-am/(MAKE) $(AM_MAKEFLAGS) install-exec-am/g' ${S}/keygen/Makefile.in
    echo "" > ${B}/xrdp_configure_options.h
}

do_install:append() {

	# deal with systemd unit files
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/instfiles/xrdp.service.in ${D}${systemd_unitdir}/system/xrdp.service
	install -m 0644 ${S}/instfiles/xrdp-sesman.service.in ${D}${systemd_unitdir}/system/xrdp-sesman.service
	sed -i -e 's,@localstatedir@,${localstatedir},g' ${D}${systemd_unitdir}/system/xrdp.service ${D}${systemd_unitdir}/system/xrdp-sesman.service
	sed -i -e 's,@sysconfdir@,${sysconfdir},g' ${D}${systemd_unitdir}/system/xrdp.service ${D}${systemd_unitdir}/system/xrdp-sesman.service
	sed -i -e 's,@sbindir@,${sbindir},g' ${D}${systemd_unitdir}/system/xrdp.service ${D}${systemd_unitdir}/system/xrdp-sesman.service

	install -d ${D}${sysconfdir}/sysconfig/xrdp
	install -m 0644 ${S}/instfiles/*.ini ${D}${sysconfdir}/xrdp/
	install -m 0644 ${S}/keygen/openssl.conf ${D}${sysconfdir}/xrdp/
	install -m 0644 ${UNPACKDIR}/xrdp.sysconfig ${D}${sysconfdir}/sysconfig/xrdp/
	chown xrdp:xrdp ${D}${sysconfdir}/xrdp
}

SYSTEMD_SERVICE:${PN} = "xrdp.service xrdp-sesman.service"

pkg_postinst:${PN}() {
	if test -z "$D"
	then
		if test -x ${bindir}/xrdp-keygen
		then
			${bindir}/xrdp-keygen xrdp ${sysconfdir}/xrdp/rsakeys.ini >/dev/null
                fi
		if test ! -s ${sysconfdir}/xrdp/cert.pem
		then
			openssl req -x509 -newkey rsa:2048 -sha256 -nodes -days 3652 \
			-keyout ${sysconfdir}/xrdp/key.pem \
			-out ${sysconfdir}/xrdp/cert.pem \
			-config ${sysconfdir}/xrdp/openssl.conf >/dev/null 2>&1
			chmod 400 ${sysconfdir}/xrdp/key.pem
		fi
        fi
}
