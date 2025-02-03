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

SRCREV = "16f45755f991f4fb6d76fec70a42992426c84234"
SRC_URI = "git://github.com/pmem/ndctl.git;branch=main;protocol=https \
           file://0001-include-libgen.h-for-basename.patch"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>v\d+(\.\d+)*)"

DEPENDS = "kmod udev json-c keyutils iniparser libtraceevent libtracefs"

S = "${WORKDIR}/git"

EXTRA_OEMESON += "-Diniparserdir=${STAGING_INCDIR}/iniparser"

PACKAGECONFIG ??= "tests ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=enabled,-Dsystemd=disabled,systemd"
PACKAGECONFIG[tests] = "-Dtest=enabled -Ddestructive=enabled, -Dtest=disabled,"
PACKAGECONFIG[docs] = "-Ddocs=enabled -Dasciidoctor=disabled,-Ddocs=disabled, asciidoc-native xmlto-native"

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "ndctl-monitor.service daxdev-reconfigure@.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

FILES:${PN} += "${datadir}/daxctl/daxctl.conf ${nonarch_libdir}/systemd/system"
