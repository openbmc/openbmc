# To allow util-linux to optionally build-depend on cryptsetup, libuuid is
# split out of the main recipe, as it's needed by cryptsetup

require util-linux.inc

inherit autotools gettext pkgconfig

S = "${WORKDIR}/util-linux-${PV}"
EXTRA_OECONF += "--disable-all-programs --enable-libuuid"
LICENSE = "BSD-3-Clause"

do_install:append() {
	rm -rf ${D}${datadir} ${D}${bindir} ${D}${base_bindir} ${D}${sbindir} ${D}${base_sbindir} ${D}${exec_prefix}/sbin
}

BBCLASSEXTEND = "native nativesdk"
