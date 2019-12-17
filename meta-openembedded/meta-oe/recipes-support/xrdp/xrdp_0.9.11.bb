SUMMARY = "An open source remote desktop protocol(rdp) server."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=72cfbe4e7bd33a0a1de9630c91195c21 \
"

inherit features_check autotools pkgconfig useradd systemd

DEPENDS = "openssl virtual/libx11 libxfixes libxrandr libpam nasm-native"

REQUIRED_DISTRO_FEATURES = "x11 pam"

SRC_URI = "git://github.com/neutrinolabs/xrdp.git \
           file://xrdp.sysconfig \
           file://0001-Added-req_distinguished_name-in-etc-xrdp-openssl.con.patch \
           file://0001-Fix-the-compile-error.patch \
           "

SRCREV = "1e4b03eb3c9aa7173de251a328c93c073dcc0fca"

PV = "0.9.11"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[fuse] = " --enable-fuse, --disable-fuse, fuse"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system xrdp"
USERADD_PARAM_${PN}  = "--system --home /var/run/xrdp -g xrdp \
                        --no-create-home --shell /bin/false xrdp"

FILES_${PN} += "${datadir}/dbus-1/services/*.service \
                ${datadir}/dbus-1/accessibility-services/*.service "

FILES_${PN}-dev += "${libdir}/xrdp/libcommon.so \
                    ${libdir}/xrdp/libxrdp.so \
                    ${libdir}/xrdp/libscp.so \
                    ${libdir}/xrdp/libxrdpapi.so "

EXTRA_OECONF = "--enable-pam-config=suse"

do_configure_prepend() {
    cd ${S}
    ./bootstrap
    cd -
}

do_compile_prepend() {
    sed -i 's/(MAKE) $(AM_MAKEFLAGS) install-exec-am install-data-am/(MAKE) $(AM_MAKEFLAGS) install-exec-am/g' ${S}/keygen/Makefile.in
}


do_install_append() {
	install -d ${D}${sysconfdir} 
	install -d ${D}${sysconfdir}/xrdp
	install -d ${D}${sysconfdir}/xrdp/pam.d
	install -d ${D}${sysconfdir}/sysconfig/xrdp
   
	# deal with systemd unit files
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/instfiles/xrdp.service.in ${D}${systemd_unitdir}/system/xrdp.service
	install -m 0644 ${S}/instfiles/xrdp-sesman.service.in ${D}${systemd_unitdir}/system/xrdp-sesman.service 
	sed -i -e 's,@localstatedir@,${localstatedir},g' ${D}${systemd_unitdir}/system/xrdp.service ${D}${systemd_unitdir}/system/xrdp-sesman.service
	sed -i -e 's,@sysconfdir@,${sysconfdir},g' ${D}${systemd_unitdir}/system/xrdp.service ${D}${systemd_unitdir}/system/xrdp-sesman.service
	sed -i -e 's,@sbindir@,${sbindir},g' ${D}${systemd_unitdir}/system/xrdp.service ${D}${systemd_unitdir}/system/xrdp-sesman.service

	install -m 0644 ${S}/instfiles/*.ini ${D}${sysconfdir}/xrdp/
	install -m 0644 ${S}/sesman/sesman.ini.in ${D}${sysconfdir}/xrdp/
	install -m 0644 ${S}/sesman/startwm.sh ${D}${sysconfdir}/xrdp/
	install -m 0644 ${S}/xrdp/xrdp.ini.in ${D}${sysconfdir}/xrdp/
	install -m 0644 ${S}/xrdp/xrdp_keyboard.ini ${D}${sysconfdir}/xrdp/
	install -m 0644 ${S}/instfiles/xrdp.sh ${D}${sysconfdir}/xrdp/
	install -m 0644 ${S}/keygen/openssl.conf ${D}${sysconfdir}/xrdp/
	install -m 0644 ${WORKDIR}/xrdp.sysconfig ${D}${sysconfdir}/sysconfig/xrdp/
	chown xrdp:xrdp ${D}${sysconfdir}/xrdp
}

SYSTEMD_SERVICE_${PN} = "xrdp.service xrdp-sesman.service"

pkg_postinst_${PN}() {
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
