SECTION = "base"
SUMMARY = "Utilities and scripts for power management"
DESCRIPTION = "Simple shell command line tools to suspend and hibernate."
HOMEPAGE = "http://pm-utils.freedesktop.org/wiki/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/pm-pmu.c;beginline=1;endline=22;md5=3c1ddbc54e735fb4a0386e14c78a3147"


SRC_URI = "http://pm-utils.freedesktop.org/releases/pm-utils-${PV}.tar.gz"

SRC_URI[md5sum] = "1742a556089c36c3a89eb1b957da5a60"
SRC_URI[sha256sum] = "8ed899032866d88b2933a1d34cc75e8ae42dcde20e1cc21836baaae3d4370c0b"

inherit pkgconfig autotools manpages

PACKAGECONFIG[manpages] = "--enable-doc, --disable-doc, libxslt-native xmlto-native"

RDEPENDS:${PN} = "bash"

EXTRA_OECONF = "--libdir=${nonarch_libdir}"

do_configure:prepend () {
	( cd ${S}; autoreconf -f -i -s )
}

FILES:${PN} += "${nonarch_libdir}/${BPN}/*"
FILES:${PN}-dbg += "${datadir}/doc/pm-utils/README.debugging"
FILES:${PN}-dev += "${nonarch_libdir}/pkgconfig/pm-utils.pc"
