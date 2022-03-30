# Copyright (c) 2021 Koninklijke Philips N.V.
#
# SPDX-License-Identifier: MIT
#
SUMMARY = "USBGuard daemon for blacklisting and whitelisting of USB devices"
DESCRIPTION = "The USBGuard software framework helps to protect your computer against \
rogue USB devices (a.k.a. Bad USB) by implementing basic whitelisting and blacklisting \
capabilities based on device attributes. This recipe takes OpenSSL as crypto-backend for \
computing device hashes (Supported values are sodium, gcrypt, openssl)."
HOMEPAGE = "https://usbguard.github.io/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://github.com/USBGuard/usbguard/releases/download/${BPN}-${PV}/${BPN}-${PV}.tar.gz \
    file://0001-Add-and-use-pkgconfig-instead-of-libgcrypt-config.patch"

SRC_URI[sha256sum] = "460ebfb4ffc5609739a202a3a1d9fda1c30de033b634845b8baa136352bfb432"

inherit autotools-brokensep bash-completion pkgconfig systemd

DEPENDS = "glib-2.0-native libcap-ng libqb libxml2-native libxslt-native pegtl protobuf protobuf-native xmlto-native"

S = "${WORKDIR}/${BPN}-${PV}"

EXTRA_OECONF += "\
    --with-bundled-catch \
    --with-bundled-pegtl \
"

PACKAGECONFIG ?= "\
    openssl \
    ${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"

# USBGuard has made polkit mandatory to configure with-dbus
PACKAGECONFIG[dbus] = "--with-dbus,--without-dbus,dbus-glib polkit"
PACKAGECONFIG[libgcrypt] = "--with-crypto-library=gcrypt,,libgcrypt,,,libsodium openssl"
PACKAGECONFIG[libsodium] = "--with-crypto-library=sodium,,libsodium,,,libgcrypt openssl"
PACKAGECONFIG[openssl] = "--with-crypto-library=openssl,,openssl,,,libgcrypt libsodium"
PACKAGECONFIG[polkit] = "--with-polkit,--without-polkit,polkit"
PACKAGECONFIG[seccomp] = "--enable-seccomp,--disable-seccomp,libseccomp"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"

SYSTEMD_PACKAGES = "${PN}"

SYSTEMD_SERVICE:${PN} = "usbguard.service ${@bb.utils.contains('PACKAGECONFIG', 'dbus', 'usbguard-dbus.service', '', d)}"

FILES:${PN} += "\
    ${systemd_unitdir}/system/usbguard.service \
    ${systemd_unitdir}/system/usbguard-dbus.service \
    ${datadir}/polkit-1 \
    ${datadir}/dbus-1 \
    ${nonarch_libdir}/tmpfiles.d \
"

do_install:append() {
# Create /var/log/usbguard in runtime.
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}" ]; then
        install -d ${D}${nonarch_libdir}/tmpfiles.d
        echo "d ${localstatedir}/log/${BPN} 0755 root root -" > ${D}${nonarch_libdir}/tmpfiles.d/${BPN}.conf
    fi
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'sysvinit', d)}" ]; then
        install -d ${D}${sysconfdir}/default/volatiles
        echo "d root root 0755 ${localstatedir}/log/${BPN} none" > ${D}${sysconfdir}/default/volatiles/99_${BPN}
    fi
    rm -rf ${D}${localstatedir}/log
}
