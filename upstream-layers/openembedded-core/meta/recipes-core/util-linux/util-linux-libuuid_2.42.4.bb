# To allow util-linux to optionally build-depend on cryptsetup, libuuid is
# split out of the main recipe, as it's needed by cryptsetup

require util-linux.inc

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://libuuid/COPYING;md5=6d2cafc999feb2c2de84d4d24b23290c \
                    file://Documentation/licenses/COPYING.BSD-3-Clause;md5=58dcd8452651fc8b07d1f65ce07ca8af"

inherit autotools gettext pkgconfig

S = "${UNPACKDIR}/util-linux-${PV}"

EXTRA_AUTORECONF += "--exclude=gtkdocize"
EXTRA_OECONF += "--disable-all-programs --enable-libuuid"

do_install:append() {
	rm -rf ${D}${datadir} ${D}${bindir} ${D}${base_bindir} ${D}${sbindir} ${D}${base_sbindir} ${D}${exec_prefix}/sbin
}

BBCLASSEXTEND = "native nativesdk"
