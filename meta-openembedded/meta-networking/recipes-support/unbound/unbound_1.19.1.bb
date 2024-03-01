SUMMARY = "Unbound is a validating, recursive, and caching DNS resolver"
DESCRIPTION = "Unbound's design is a set of modular components which incorporate \
	features including enhanced security (DNSSEC) validation, Internet Protocol \
	Version 6 (IPv6), and a client resolver library API as an integral part of the \
	architecture"

HOMEPAGE = "https://www.unbound.net/"
SECTION = "net"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5308494bc0590c0cb036afd781d78f06"

SRC_URI = "git://github.com/NLnetLabs/unbound.git;protocol=https;branch=branch-1.19.1"
SRCREV = "c6dd1e116df75242a0c032295c152f06c721b3cd"

inherit autotools pkgconfig systemd update-rc.d

DEPENDS = "openssl libtool-native bison-native expat"
RDEPENDS:${PN} = "bash openssl-bin daemonize"

S = "${WORKDIR}/git"

EXTRA_OECONF = "--with-libexpat=${STAGING_EXECPREFIXDIR} \
		--with-ssl=${STAGING_EXECPREFIXDIR} \
                --enable-largefile"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[dnscrypt] = "--enable-dnscrypt, --disable-dnscrypt, libsodium"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"
PACKAGECONFIG[libevent] = "--with-libevent=${STAGING_EXECPREFIXDIR},,libevent"

do_configure:append() {
	sed -i -e 's#${RECIPE_SYSROOT}##g' ${B}/config.h
}

do_install:append() {
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${B}/contrib/unbound.service ${D}${systemd_unitdir}/system

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/contrib/unbound.init_yocto ${D}${sysconfdir}/init.d/unbound
}

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

INITSCRIPT_NAME = "unbound"
INITSCRIPT_PARAMS = "defaults"
