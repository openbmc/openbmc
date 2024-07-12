SUMMARY = "Client for Wi-Fi Protected Access (WPA)"
DESCRIPTION = "wpa_supplicant is a WPA Supplicant for Linux, BSD, Mac OS X, and Windows with support for WPA and WPA2 (IEEE 802.11i / RSN). Supplicant is the IEEE 802.1X/WPA component that is used in the client stations. It implements key negotiation with a WPA Authenticator and it controls the roaming and IEEE 802.11 authentication/association of the wlan driver."
HOMEPAGE = "http://w1.fi/wpa_supplicant/"
BUGTRACKER = "http://w1.fi/security/"
SECTION = "network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=5ebcb90236d1ad640558c3d3cd3035df \
                    file://README;beginline=1;endline=56;md5=e3d2f6c2948991e37c1ca4960de84747 \
                    file://wpa_supplicant/wpa_supplicant.c;beginline=1;endline=12;md5=76306a95306fee9a976b0ac1be70f705"

DEPENDS = "dbus libnl"

SRC_URI = "http://w1.fi/releases/wpa_supplicant-${PV}.tar.gz \
           file://wpa-supplicant.sh \
           file://wpa_supplicant.conf \
           file://wpa_supplicant.conf-sane \
           file://99_wpa_supplicant \
           file://0001-build-Re-enable-options-for-libwpa_client.so-and-wpa.patch \
           file://0002-Fix-removal-of-wpa_passphrase-on-make-clean.patch \
           file://0001-Install-wpa_passphrase-when-not-disabled.patch \
           file://0001-PEAP-client-Update-Phase-2-authentication-requiremen.patch \
           "
SRC_URI[sha256sum] = "20df7ae5154b3830355f8ab4269123a87affdea59fe74fe9292a91d0d7e17b2f"

S = "${WORKDIR}/wpa_supplicant-${PV}"

inherit pkgconfig systemd

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG[gnutls] = ",,gnutls libgcrypt"
PACKAGECONFIG[openssl] = ",,openssl"

CVE_PRODUCT = "wpa_supplicant"

EXTRA_OEMAKE = "'LIBDIR=${libdir}' 'INCDIR=${includedir}' 'BINDIR=${sbindir}'"

do_configure () {
	${MAKE} -C wpa_supplicant clean
	sed -e '/^CONFIG_TLS=/d' <wpa_supplicant/defconfig >wpa_supplicant/.config

	if ${@ bb.utils.contains('PACKAGECONFIG', 'openssl', 'true', 'false', d) }; then
		echo 'CONFIG_TLS=openssl' >>wpa_supplicant/.config
	elif ${@ bb.utils.contains('PACKAGECONFIG', 'gnutls', 'true', 'false', d) }; then
		echo 'CONFIG_TLS=gnutls' >>wpa_supplicant/.config
        sed -i -e 's/\(^CONFIG_DPP=\)/#\1/' \
               -e 's/\(^CONFIG_EAP_PWD=\)/#\1/' \
               -e 's/\(^CONFIG_SAE=\)/#\1/' wpa_supplicant/.config
	fi

	# For rebuild
	rm -f wpa_supplicant/*.d wpa_supplicant/dbus/*.d
}

do_compile () {
	oe_runmake -C wpa_supplicant
	if [ -z "${DISABLE_STATIC}" ]; then
		oe_runmake -C wpa_supplicant libwpa_client.a
	fi
}

do_install () {
	oe_runmake -C wpa_supplicant DESTDIR="${D}" install

	install -d ${D}${docdir}/wpa_supplicant
	install -m 644 wpa_supplicant/README ${WORKDIR}/wpa_supplicant.conf ${D}${docdir}/wpa_supplicant

	install -d ${D}${sysconfdir}
	install -m 600 ${WORKDIR}/wpa_supplicant.conf-sane ${D}${sysconfdir}/wpa_supplicant.conf

	install -d ${D}${sysconfdir}/network/if-pre-up.d/
	install -d ${D}${sysconfdir}/network/if-post-down.d/
	install -d ${D}${sysconfdir}/network/if-down.d/
	install -m 755 ${WORKDIR}/wpa-supplicant.sh ${D}${sysconfdir}/network/if-pre-up.d/wpa-supplicant
	ln -sf ../if-pre-up.d/wpa-supplicant ${D}${sysconfdir}/network/if-post-down.d/wpa-supplicant

	install -d ${D}/${sysconfdir}/dbus-1/system.d
	install -m 644 ${S}/wpa_supplicant/dbus/dbus-wpa_supplicant.conf ${D}/${sysconfdir}/dbus-1/system.d
	install -d ${D}/${datadir}/dbus-1/system-services
	install -m 644 ${S}/wpa_supplicant/dbus/*.service ${D}/${datadir}/dbus-1/system-services

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}/${systemd_system_unitdir}
		install -m 644 ${S}/wpa_supplicant/systemd/*.service ${D}/${systemd_system_unitdir}
	fi

	install -d ${D}/etc/default/volatiles
	install -m 0644 ${WORKDIR}/99_wpa_supplicant ${D}/etc/default/volatiles

	install -d ${D}${includedir}
	install -m 0644 ${S}/src/common/wpa_ctrl.h ${D}${includedir}

	if [ -z "${DISABLE_STATIC}" ]; then
		install -d ${D}${libdir}
		install -m 0644 wpa_supplicant/libwpa_client.a ${D}${libdir}
	fi
}

pkg_postinst:${PN} () {
	# If we're offline, we don't need to do this.
	if [ "x$D" = "x" ]; then
		killall -q -HUP dbus-daemon || true
	fi
}

PACKAGE_BEFORE_PN += "${PN}-passphrase ${PN}-cli"
PACKAGES =+ "${PN}-lib"
PACKAGES += "${PN}-plugins"
ALLOW_EMPTY:${PN}-plugins = "1"

PACKAGES_DYNAMIC += "^${PN}-plugin-.*$"
NOAUTOPACKAGEDEBUG = "1"

FILES:${PN}-passphrase = "${sbindir}/wpa_passphrase"
FILES:${PN}-cli = "${sbindir}/wpa_cli"
FILES:${PN}-lib = "${libdir}/libwpa_client*${SOLIBSDEV}"
FILES:${PN} += "${datadir}/dbus-1/system-services/* ${systemd_system_unitdir}/*"
FILES:${PN}-dbg += "${sbindir}/.debug ${libdir}/.debug"

CONFFILES:${PN} += "${sysconfdir}/wpa_supplicant.conf"

RRECOMMENDS:${PN} = "${PN}-passphrase ${PN}-cli ${PN}-plugins"

SYSTEMD_SERVICE:${PN} = "wpa_supplicant.service"
SYSTEMD_AUTO_ENABLE = "disable"

python split_wpa_supplicant_libs () {
    libdir = d.expand('${libdir}/wpa_supplicant')
    dbglibdir = os.path.join(libdir, '.debug')

    split_packages = do_split_packages(d, libdir, r'^(.*)\.so', '${PN}-plugin-%s', 'wpa_supplicant %s plugin', prepend=True)
    split_dbg_packages = do_split_packages(d, dbglibdir, r'^(.*)\.so', '${PN}-plugin-%s-dbg', 'wpa_supplicant %s plugin - Debugging files', prepend=True, extra_depends='${PN}-dbg')

    if split_packages:
        pn = d.getVar('PN')
        d.setVar('RRECOMMENDS:' + pn + '-plugins', ' '.join(split_packages))
        d.appendVar('RRECOMMENDS:' + pn + '-dbg', ' ' + ' '.join(split_dbg_packages))
}
PACKAGESPLITFUNCS += "split_wpa_supplicant_libs"
