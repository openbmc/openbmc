SUMMARY = "libnvdimm utility library"
DESCRIPTION = "Utility library for managing the libnvdimm \
(non-volatile memory device) sub-system in the Linux kernel. \
The LIBNVDIMM subsystem provides support for three types of \
NVDIMMs, namely,PMEM, BLK, and NVDIMM devices that can \
simultaneously support both PMEM and BLK mode access."
HOMEPAGE = "https://git.kernel.org/cgit/linux/kernel/git/nvdimm/nvdimm.git/tree/Documentation/nvdimm/nvdimm.txt?h=libnvdimm-for-next"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & MIT & CC0-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=74a614eac8b2657a4b8e6607421a0883"

inherit meson pkgconfig bash-completion systemd

SRCREV = "dd58d43458943d20ff063850670bf54a5242c9c5"
SRC_URI = "git://github.com/pmem/ndctl.git;branch=main;protocol=https \
           file://0001-util-Correct-path-to-iniparser.h.patch \
           file://0001-meson-Use-pkg-config-to-detect-iniparser.patch \
           file://0001-build-set-HAVE_STATEMENT_EXPR-var.patch"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>v\d+(\.\d+)*)"

DEPENDS = "kmod udev json-c keyutils iniparser"

S = "${WORKDIR}/git"

EXTRA_OECONF += "-Ddestructive=enabled"

PACKAGECONFIG ??= "tests ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[tests] = "-Dtest=enabled, -Dtest=disabled,"
PACKAGECONFIG[docs] = "-Ddocs=enabled -Dasciidoctor=enabled,-Ddocs=disabled -Dasciidoctor=disabled, asciidoc-native"

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "ndctl-monitor.service daxdev-reconfigure@.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

FILES:${PN} += "${datadir}/daxctl/daxctl.conf "
