SUMMARY = "v4l2 and IR applications"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=48da9957849056017dc568bbc43d8975 \
                    file://COPYING.libv4l;md5=d749e86a105281d7a44c2328acebc4b0"
PROVIDES = "libv4l media-ctl"

DEPENDS = "jpeg \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'virtual/libx11', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa-lib', '', d)} \
           ${@bb.utils.contains_any('PACKAGECONFIG', 'qv4l2 qvidcap', 'qtbase qtbase-native', '', d)}"

DEPENDS:append:libc-musl = " argp-standalone"
DEPENDS:append:class-target = " udev"
LDFLAGS:append = " -pthread"
# v4l2 explicitly sets _FILE_OFFSET_BITS=32 to get access to
# both 32 and 64 bit file APIs.  But it does not handle the time side?
# Needs further investigation
GLIBC_64BIT_TIME_FLAGS = ""

inherit autotools gettext pkgconfig

PACKAGECONFIG ??= "media-ctl"
PACKAGECONFIG[media-ctl] = "--enable-v4l-utils,--disable-v4l-utils,,"
PACKAGECONFIG[qv4l2] = ",--disable-qv4l2"
PACKAGECONFIG[qvidcap] = ",--disable-qvidcap"
PACKAGECONFIG[v4l2-tracer] = ",--disable-v4l2-tracer,json-c"

SRC_URI = "\
    git://git.linuxtv.org/v4l-utils.git;protocol=https;branch=stable-1.24 \
    file://0001-Revert-media-ctl-Don-t-install-libmediactl-and-libv4.patch \
    file://0002-original-patch-mediactl-pkgconfig.patch \
    file://0003-original-patch-export-mediactl-headers.patch \
    file://0004-Do-not-use-getsubopt.patch \
"

SRCREV = "8799081b143627c9c09dea0c60ad3d1cc17cc848"

PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"

do_configure:prepend() {
    cd ${S}; ./bootstrap.sh; cd -
}

EXTRA_OECONF = "--enable-shared --with-udevdir=${base_libdir}/udev \
                --disable-v4l2-compliance-32 --disable-v4l2-ctl-32"

VIRTUAL-RUNTIME_ir-keytable-keymaps ?= "rc-keymaps"

PACKAGES =+ "media-ctl ir-keytable rc-keymaps libv4l libv4l-dev qv4l2 qvidcap"

RPROVIDES:${PN}-dbg += "libv4l-dbg"

FILES:media-ctl = "${bindir}/media-ctl ${libdir}/libmediactl.so.*"
FILES:qv4l2 = "\
    ${bindir}/qv4l2 \
    ${datadir}/applications/qv4l2.desktop \
    ${datadir}/icons/hicolor/*/apps/qv4l2.* \
"
FILES:qvidcap = "\
    ${bindir}/qvidcap \
    ${datadir}/applications/qvidcap.desktop \
    ${datadir}/icons/hicolor/*/apps/qvidcap.* \
"

FILES:ir-keytable = "${bindir}/ir-keytable ${base_libdir}/udev/rules.d/*-infrared.rules"
RDEPENDS:ir-keytable += "${VIRTUAL-RUNTIME_ir-keytable-keymaps}"
RDEPENDS:qv4l2 += "\
    ${@bb.utils.contains('PACKAGECONFIG', 'qv4l2', 'qtbase', '', d)}"
RDEPENDS:qvidcap += "\
    ${@bb.utils.contains('PACKAGECONFIG', 'qvidcap', 'qtbase', '', d)}"

FILES:rc-keymaps = "${sysconfdir}/rc* ${base_libdir}/udev/rc*"

FILES:${PN} = "${bindir} ${sbindir}"

FILES:libv4l += "${libdir}/libv4l*${SOLIBS} ${libdir}/libv4l/*.so ${libdir}/libv4l/plugins/*.so \
                 ${libdir}/libdvbv5*${SOLIBS} \
                 ${libdir}/libv4l/*-decomp \
                 ${libdir}/libv4l2tracer.so \
"

FILES:libv4l-dev += "${includedir} ${libdir}/pkgconfig \
                     ${libdir}/libv4l*${SOLIBSDEV} ${libdir}/*.la \
                     ${libdir}/v4l*${SOLIBSDEV} ${libdir}/libv4l/*.la ${libdir}/libv4l/plugins/*.la"

PARALLEL_MAKE:class-native = ""
BBCLASSEXTEND = "native"
