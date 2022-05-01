SUMMARY = "Assistive Technology Service Provider Interface (dbus core)"

DESCRIPTION = "It provides a Service Provider Interface for the Assistive Technologies available on the GNOME platform and a library against which applications can be linked."

HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
BUGTRACKER = "http://bugzilla.gnome.org/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz \
           file://08036a4c4491eea57d7b713bb4440f541584204b.patch \
          "

SRC_URI[sha256sum] = "7eee3cf285b089060fd6b6e51b3eb2cacf752cca3a082c7f4c2c5ab841e51353"

X11DEPENDS = "virtual/libx11 libxi libxtst"

DEPENDS = "dbus glib-2.0"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${X11DEPENDS}', '', d)}"

inherit meson gtk-doc gettext systemd pkgconfig upstream-version-is-even gobject-introspection

EXTRA_OEMESON = " -Dsystemd_user_dir=${systemd_user_unitdir} \
                  -Ddbus_daemon=${bindir}/dbus-daemon \
                  ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '-Dx11=yes', '-Dx11=no', d)} \
"

GTKDOC_MESON_OPTION = "docs"

GIR_MESON_OPTION = 'introspection'
GIR_MESON_ENABLE_FLAG = 'yes'
GIR_MESON_DISABLE_FLAG = 'no'

FILES:${PN} += "${datadir}/dbus-1/services/*.service \
                ${datadir}/dbus-1/accessibility-services/*.service \
                ${datadir}/defaults/at-spi2 \
                ${systemd_user_unitdir}/at-spi-dbus-bus.service \
                "
BBCLASSEXTEND = "native nativesdk"
