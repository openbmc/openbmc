DESCRIPTION = "A storage daemon that implements well-defined D-Bus interfaces that can be used to query and manipulate storage devices."
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=73d83aebe7e4b62346afde80e0e94273"

DEPENDS = "libatasmart sg3-utils polkit libgudev dbus-glib glib-2.0 intltool-native lvm2"
# optional dependencies: device-mapper parted

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

SRC_URI = " \
    http://hal.freedesktop.org/releases/${BPN}-${PV}.tar.gz;name=${BPN} \
    file://optional-depends.patch \
    file://0001-fix-build-with-newer-glibc-versions.patch \
    file://udisks-1.0.5-fix-service-file.patch \
"

SRC_URI[udisks.md5sum] = "70d48dcfe523a74cd7c7fbbc2847fcdd"
SRC_URI[udisks.sha256sum] = "f2ec82eb0ea7e01dc299b5b29b3c18cdf861236ec43dcff66b3552b4b31c6f71"

inherit autotools-brokensep systemd gtk-doc

PACKAGECONFIG ??= ""
PACKAGECONFIG[parted] = "--enable-parted,--disable-parted,parted"

EXTRA_OECONF = "--disable-man-pages"

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
