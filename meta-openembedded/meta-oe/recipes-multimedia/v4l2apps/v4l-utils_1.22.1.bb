SUMMARY = "v4l2 and IR applications"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=48da9957849056017dc568bbc43d8975 \
                    file://COPYING.libv4l;md5=d749e86a105281d7a44c2328acebc4b0"
PROVIDES = "libv4l media-ctl"

DEPENDS = "jpeg \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'virtual/libx11', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
DEPENDS:append:libc-musl = " argp-standalone"
DEPENDS:append:class-target = " udev"
LDFLAGS:append = " -pthread"

inherit autotools gettext pkgconfig

PACKAGECONFIG ??= "media-ctl"
PACKAGECONFIG[media-ctl] = "--enable-v4l-utils,--disable-v4l-utils,,"

SRC_URI = "http://linuxtv.org/downloads/v4l-utils/v4l-utils-${PV}.tar.bz2 \
           file://0001-Revert-media-ctl-Don-t-install-libmediactl-and-libv4.patch \
           file://0002-original-patch-mediactl-pkgconfig.patch \
           file://0003-original-patch-export-mediactl-headers.patch \
           file://0007-Do-not-use-getsubopt.patch \
           "

SRC_URI[md5sum] = "8aa73287320a49e9170a8255d7b2c7e6"
SRC_URI[sha256sum] = "65c6fbe830a44ca105c443b027182c1b2c9053a91d1e72ad849dfab388b94e31"

EXTRA_OECONF = "--disable-qv4l2 --enable-shared --with-udevdir=${base_libdir}/udev \
                --disable-v4l2-compliance-32 --disable-v4l2-ctl-32"

VIRTUAL-RUNTIME_ir-keytable-keymaps ?= "rc-keymaps"

PACKAGES =+ "media-ctl ir-keytable rc-keymaps libv4l libv4l-dev"

RPROVIDES:${PN}-dbg += "libv4l-dbg"

FILES:media-ctl = "${bindir}/media-ctl ${libdir}/libmediactl.so.*"

FILES:ir-keytable = "${bindir}/ir-keytable ${base_libdir}/udev/rules.d/*-infrared.rules"
RDEPENDS:ir-keytable += "${VIRTUAL-RUNTIME_ir-keytable-keymaps}"

FILES:rc-keymaps = "${sysconfdir}/rc* ${base_libdir}/udev/rc*"

FILES:${PN} = "${bindir} ${sbindir}"

FILES:libv4l += "${libdir}/libv4l*${SOLIBS} ${libdir}/libv4l/*.so ${libdir}/libv4l/plugins/*.so \
                 ${libdir}/libdvbv5*${SOLIBS} \
                 ${libdir}/libv4l/*-decomp"

FILES:libv4l-dev += "${includedir} ${libdir}/pkgconfig \
                     ${libdir}/libv4l*${SOLIBSDEV} ${libdir}/*.la \
                     ${libdir}/v4l*${SOLIBSDEV} ${libdir}/libv4l/*.la ${libdir}/libv4l/plugins/*.la"

PARALLEL_MAKE:class-native = ""
BBCLASSEXTEND = "native"
