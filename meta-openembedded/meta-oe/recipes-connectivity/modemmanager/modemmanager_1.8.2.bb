SUMMARY = "ModemManager is a daemon controlling broadband devices/connections"
DESCRIPTION = "ModemManager is a DBus-activated daemon which controls mobile broadband (2G/3G/4G) devices and connections"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/ModemManager/"
LICENSE = "GPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit gnomebase gettext systemd vala gobject-introspection bash-completion

DEPENDS = "glib-2.0 libgudev dbus-glib intltool-native"

SRC_URI = "http://www.freedesktop.org/software/ModemManager/ModemManager-${PV}.tar.xz \
           file://0001-Do-not-set-Wno-unused-but-set-variable.patch \
           "

SRC_URI[md5sum] = "a49c9f73e46c7b89e5efedda250d22c0"
SRC_URI[sha256sum] = "96f2a5f0ed15532b4c4c185b756fdc0326e7c2027cea26a1264f91e098260f80"

S = "${WORKDIR}/ModemManager-${PV}"

PACKAGECONFIG ??= "mbim qmi polkit \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"

PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,,"
PACKAGECONFIG[polkit] = "--with-polkit=yes,--with-polkit=no,polkit"
# Support WWAN modems and devices which speak the Mobile Interface Broadband Model (MBIM) protocol.
PACKAGECONFIG[mbim] = "--with-mbim,--with-mbim=no,libmbim"
# Support WWAN modems and devices which speak the Qualcomm MSM Interface (QMI) protocol.
PACKAGECONFIG[qmi] = "--with-qmi,--without-qmi,libqmi"

EXTRA_OECONF = " \
    --with-udev-base-dir=${nonarch_base_libdir}/udev \
"
CFLAGS_append_toolchain-gcc = " -Wno-unused-but-set-variable"

FILES_${PN} += " \
    ${datadir}/icons \
    ${datadir}/polkit-1 \
    ${datadir}/dbus-1 \
    ${libdir}/ModemManager \
    ${systemd_unitdir}/system \
"

FILES_${PN}-dev += " \
    ${libdir}/ModemManager/*.la \
"

FILES_${PN}-staticdev += " \
    ${libdir}/ModemManager/*.a \
"

FILES_${PN}-dbg += "${libdir}/ModemManager/.debug"

SYSTEMD_SERVICE_${PN} = "ModemManager.service"
