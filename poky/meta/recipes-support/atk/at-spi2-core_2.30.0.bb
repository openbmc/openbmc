SUMMARY = "Assistive Technology Service Provider Interface (dbus core)"
HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=e9f288ba982d60518f375b5898283886"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz \
           "

SRC_URI[md5sum] = "d4f22c66b3210ffe6b10d01c04e008b5"
SRC_URI[sha256sum] = "0175f5393d19da51f4c11462cba4ba6ef3fa042abf1611a70bdfed586b7bfb2b"

X11DEPENDS = "virtual/libx11 libxi libxtst"

DEPENDS = "dbus glib-2.0"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${X11DEPENDS}', '', d)}"

inherit meson gtk-doc gettext systemd pkgconfig upstream-version-is-even gobject-introspection

EXTRA_OEMESON = " -Dsystemd_user_dir=${systemd_user_unitdir} \
                  -Ddbus_daemon=${bindir}/dbus-daemon"

GTKDOC_ENABLE_FLAG = "-Denable_docs=true"
GTKDOC_DISABLE_FLAG = "-Denable_docs=false"

GI_ENABLE_FLAG = "-Denable-introspection=yes"
GI_DISABLE_FLAG = "-Denable-introspection=no"

EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '${GI_ENABLE_FLAG}', \
                                                                                       '${GI_DISABLE_FLAG}', d)} "

EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GTKDOC_ENABLED', 'True', '${GTKDOC_ENABLE_FLAG}', \
                                                                                     '${GTKDOC_DISABLE_FLAG}', d)} "

FILES_${PN} += "${datadir}/dbus-1/services/*.service \
                ${datadir}/dbus-1/accessibility-services/*.service \
                ${datadir}/defaults/at-spi2 \
                ${systemd_user_unitdir}/at-spi-dbus-bus.service \
                "
BBCLASSEXTEND = "native nativesdk"
