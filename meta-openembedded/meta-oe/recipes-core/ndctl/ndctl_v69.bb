SUMMARY = "libnvdimm utility library"
DESCRIPTION = "Utility library for managing the libnvdimm \
(non-volatile memory device) sub-system in the Linux kernel. \
The LIBNVDIMM subsystem provides support for three types of \
NVDIMMs, namely,PMEM, BLK, and NVDIMM devices that can \
simultaneously support both PMEM and BLK mode access."
HOMEPAGE = "https://git.kernel.org/cgit/linux/kernel/git/nvdimm/nvdimm.git/tree/Documentation/nvdimm/nvdimm.txt?h=libnvdimm-for-next"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=e66651809cac5da60c8b80e9e4e79e08"

inherit autotools-brokensep pkgconfig bash-completion systemd

SRCREV = "ea62d6d53bf6f806c4841e97a370201e18446860"
SRC_URI = "git://github.com/pmem/ndctl.git"

DEPENDS = "kmod udev json-c keyutils"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--enable-test --enable-destructive --disable-docs"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"
PACKAGECONFIG[systemd] = "--with-systemd, --without-systemd, systemd"

do_configure:prepend() {
    ${S}/autogen.sh
}

SYSTEMD_SERVICE:${PN} = "ndctl-monitor.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

FILES:${PN} += "${datadir}/daxctl/daxctl.conf"
