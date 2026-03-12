# SPDX-License-Identifier: MIT
#
# Copyright Leica Geosystems AG
#

SUMMARY = "systemd-repart"
DESCRIPTION = "systemd-repart grows and adds partitions to a partition table, based on the configuration files described in repart.d(5), or generates a Discoverable Disk Image (DDI) for a system extension (sysext, see systemd-sysext(8))."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/systemd"

LICENSE = "GPL-2.0-only & LGPL-2.1-or-later"
LICENSE:libsystemd = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
                    file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c"

SRCREV = "00a12c234e2506f5cab683460199575f13c454db"
SRCBRANCH = "v257-stable"
SRC_URI = "git://github.com/systemd/systemd.git;protocol=https;branch=${SRCBRANCH}"

DEPENDS = " \
    cryptsetup-native \
    gperf-native \
    libcap-native \
    python3-jinja2-native \
    util-linux-native \
"

inherit meson pkgconfig gettext native

MESON_TARGET = "systemd-repart"

# Helper variables to clarify locations. This mirrors the logic in systemd's
# build system.
rootprefix ?= "${root_prefix}"
rootlibdir ?= "${base_libdir}"
rootlibexecdir = "${rootprefix}/lib"

EXTRA_OEMESON += "-Dnobody-user=nobody \
    -Dnobody-group=nogroup \
    -Drootlibdir=${rootlibdir} \
    -Drootprefix=${rootprefix} \
    -Ddefault-locale=C \
    -Dmode=release \
    -Dsystem-alloc-uid-min=101 \
    -Dsystem-uid-max=999 \
    -Dsystem-alloc-gid-min=101 \
    -Dsystem-gid-max=999 \
"

do_install() {
    install -d ${D}${bindir}/
    install -m 0755 ${B}/systemd-repart ${D}${bindir}/systemd-repart
    install -d ${D}${libdir}/
    install -m 0644 ${B}/src/shared/libsystemd-shared-257.so ${D}${libdir}/libsystemd-shared-257.so

    install -d ${D}${libdir}/systemd/repart/
    cp -r ${S}/src/repart/definitions ${D}${libdir}/systemd/repart/
}
