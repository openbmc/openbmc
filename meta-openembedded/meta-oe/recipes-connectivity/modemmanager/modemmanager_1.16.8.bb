SUMMARY = "ModemManager is a daemon controlling broadband devices/connections"
DESCRIPTION = "ModemManager is a DBus-activated daemon which controls mobile broadband (2G/3G/4G) devices and connections"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/ModemManager/"
LICENSE = "GPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit gnomebase gettext systemd vala gobject-introspection bash-completion

DEPENDS = "glib-2.0 libgudev intltool-native libxslt-native"

SRC_URI = "http://www.freedesktop.org/software/ModemManager/ModemManager-${PV}.tar.xz"
SRC_URI[sha256sum] = "21fdfaf94171261ad9d99771894f5ade4bc39ef3d1ff1d421054d14713e97880"
S = "${WORKDIR}/ModemManager-${PV}"

PACKAGECONFIG ??= "mbim qmi \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd polkit', d)} \
"

PACKAGECONFIG[at] = "--with-at-command-via-dbus"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,,"
PACKAGECONFIG[polkit] = "--with-polkit=yes,--with-polkit=no,polkit"
# Support WWAN modems and devices which speak the Mobile Interface Broadband Model (MBIM) protocol.
PACKAGECONFIG[mbim] = "--with-mbim,--without-mbim,libmbim"
# Support WWAN modems and devices which speak the Qualcomm MSM Interface (QMI) protocol.
PACKAGECONFIG[qmi] = "--with-qmi,--without-qmi,libqmi"

EXTRA_OECONF = " \
    --with-udev-base-dir=${nonarch_base_libdir}/udev \
"

FILES:${PN} += " \
    ${datadir}/icons \
    ${datadir}/polkit-1 \
    ${datadir}/dbus-1 \
    ${datadir}/ModemManager \
    ${libdir}/ModemManager \
    ${systemd_unitdir}/system \
"

FILES:${PN}-dev += " \
    ${libdir}/ModemManager/*.la \
"

FILES:${PN}-staticdev += " \
    ${libdir}/ModemManager/*.a \
"

FILES:${PN}-dbg += "${libdir}/ModemManager/.debug"

SYSTEMD_SERVICE:${PN} = "ModemManager.service"
