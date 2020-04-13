SUMMARY = "libqmi is a library for talking to WWAN devices by QMI protocol"
DESCRIPTION = "libqmi is a glib-based library for talking to WWAN modems and devices which speak the Qualcomm MSM Interface (QMI) protocol"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libqmi"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

DEPENDS = "glib-2.0 glib-2.0-native"

inherit autotools pkgconfig bash-completion

SRC_URI = "http://www.freedesktop.org/software/${BPN}/${BPN}-${PV}.tar.xz \
           "

SRC_URI[sha256sum] = "c793db2c91d7928160341b357b26315d9c879ecb36699cb7a6b36054cba60893"

PACKAGECONFIG ??= "udev mbim"
PACKAGECONFIG[udev] = ",--without-udev,libgudev"
PACKAGECONFIG[mbim] = "--enable-mbim-qmux,--disable-mbim-qmux,libmbim"

EXTRA_OECONF_append_toolchain-clang = " --enable-more-warnings=no"
