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

inherit meson gettext pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[qv4l2] = ",-Dqv4l2=disabled"
PACKAGECONFIG[qvidcap] = ",-Dqvidcap=disabled"
PACKAGECONFIG[v4l2-tracer] = ",-Dv4l2-tracer=disabled,json-c"

SRC_URI = "\
    git://git.linuxtv.org/v4l-utils.git;protocol=https;branch=stable-1.28 \
    file://0001-media-ctl-Install-media-ctl-header-and-library-files.patch \
    file://0002-media-ctl-Install-media-ctl-pkg-config-files.patch \
"

SRCREV = "fc15e229d9d337e46d730f00647821adbbd58548"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "v4l-utils-(?P<pver>\d+(\.\d+)+)"

EXTRA_OEMESON = "-Dudevdir=${base_libdir}/udev -Dv4l2-compliance-32=false -Dv4l2-ctl-32=false"

# Disable the erroneous installation of gconv-modules that would break glib
# like it is done in Debian and ArchLinux.
EXTRA_OEMESON += "-Dgconv=disabled"

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
