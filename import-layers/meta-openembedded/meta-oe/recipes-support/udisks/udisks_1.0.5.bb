DESCRIPTION = "A storage daemon that implements well-defined D-Bus interfaces that can be used to query and manipulate storage devices."
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=73d83aebe7e4b62346afde80e0e94273"

DEPENDS = "libatasmart sg3-utils polkit libgudev dbus-glib glib-2.0 intltool-native \
    dbus-glib-native \
"
# optional dependencies: device-mapper parted

DEPENDS += "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
    dbus-glib-native \
"

SRC_URI = " \
    http://hal.freedesktop.org/releases/${BPN}-${PV}.tar.gz;name=${BPN} \
    file://optional-depends.patch \
    file://0001-fix-build-with-newer-glibc-versions.patch \
    file://udisks-1.0.5-fix-service-file.patch \
    file://0001-Make-udev-rules-directory-configurable.patch \
"

SRC_URI[udisks.md5sum] = "70d48dcfe523a74cd7c7fbbc2847fcdd"
SRC_URI[udisks.sha256sum] = "f2ec82eb0ea7e01dc299b5b29b3c18cdf861236ec43dcff66b3552b4b31c6f71"

inherit autotools-brokensep systemd gtk-doc

PACKAGECONFIG ??= "libdevmapper"
PACKAGECONFIG[libdevmapper] = "--enable-devmapper,--disable-devmapper,libdevmapper"
PACKAGECONFIG[parted] = "--enable-parted,--disable-parted,parted"

EXTRA_OECONF = "--disable-man-pages --libdir=${base_libdir} --sbindir=${base_sbindir}"
EXTRA_OEMAKE = "udevrulesdir=${nonarch_base_libdir}/udev/rules.d/"

FILES_${PN} += "${libdir}/polkit-1/extensions/*.so \
                ${datadir}/dbus-1/ \
                ${datadir}/polkit-1 \
                ${nonarch_base_libdir}/udev/* \
"

FILES_${PN}-dbg += "${nonarch_base_libdir}/udev/.debug"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "udisks.service"
SYSTEMD_AUTO_ENABLE = "disable"
