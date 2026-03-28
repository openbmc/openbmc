SUMMARY = "v4l2 and IR applications"
HOMEPAGE = "https://git.linuxtv.org/v4l-utils.git"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0ebceacbd7029b5e7051e9f529542b7c \
                    file://COPYING.libv4l;md5=88b8889c2c4329d4cf18ce5895e64c16"
PROVIDES = "libv4l media-ctl"

DEPENDS = "jpeg \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'virtual/libx11', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa-lib', '', d)} \
           ${@bb.utils.contains_any('PACKAGECONFIG', 'qv4l2 qvidcap', 'qtbase qtbase-native', '', d)}"

DEPENDS:append:libc-musl = " argp-standalone"
DEPENDS:append:class-target = " udev"
LDFLAGS:append = " -pthread"

inherit meson gettext pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[qv4l2] = ",-Dqv4l2=disabled"
PACKAGECONFIG[qvidcap] = ",-Dqvidcap=disabled"
PACKAGECONFIG[v4l2-tracer] = ",-Dv4l2-tracer=disabled,json-c"

SRC_URI = "\
    git://git.linuxtv.org/v4l-utils.git;protocol=https;branch=master;tag=v4l-utils-${PV} \
    file://0001-media-ctl-Install-media-ctl-header-and-library-files.patch \
    file://0002-media-ctl-Install-media-ctl-pkg-config-files.patch \
"

SRCREV = "5a666c7ce89c00d66aa8e53c8f098a0c6c401f91"

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
