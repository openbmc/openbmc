SUMMARY = "libnvdimm utility library"
DESCRIPTION = "Utility library for managing the libnvdimm \
(non-volatile memory device) sub-system in the Linux kernel. \
The LIBNVDIMM subsystem provides support for three types of \
NVDIMMs, namely,PMEM, BLK, and NVDIMM devices that can \
simultaneously support both PMEM and BLK mode access."
HOMEPAGE = "https://git.kernel.org/cgit/linux/kernel/git/nvdimm/nvdimm.git/tree/Documentation/nvdimm/nvdimm.txt?h=libnvdimm-for-next"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=e66651809cac5da60c8b80e9e4e79e08"

inherit autotools-brokensep pkgconfig module-base bash-completion systemd

# v62
SRCREV = "11f560f4048c1d38b7011a49566871a1e8a07c94"
SRC_URI = "git://github.com/pmem/ndctl.git"

DEPENDS = "virtual/kernel kmod udev json-c"

PV = "v62+git${SRCPV}"
S = "${WORKDIR}/git"

EXTRA_OECONF += "--enable-test --enable-destructive --disable-docs"

PACKAGECONFIG ??= ""
PACKAGECONFIG[systemd] = "--with-systemd-unit-dir=${systemd_system_unitdir}, --without-systemd-unit-dir,"

do_configure_prepend() {
    ${S}/autogen.sh
}

SYSTEMD_SERVICE_${PN} = "ndctl-monitor.service"

COMPATIBLE_HOST='(x86_64).*'
