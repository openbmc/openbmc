SUMMARY = "Python bindings for the Cairo canvas library"
HOMEPAGE = "http://cairographics.org/pycairo"
BUGTRACKER = "http://bugs.freedesktop.org"
SECTION = "python-devel"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"

# cairo >= 1.8.8
DEPENDS = "cairo"
PR = "r2"

SRC_URI = "http://cairographics.org/releases/pycairo-${PV}.tar.bz2"

SRC_URI[md5sum] = "e6fd3f2f1e6a72e0db0868c4985669c5"
SRC_URI[sha256sum] = "9aa4078e7eb5be583aeabbe8d87172797717f95e8c4338f0d4a17b683a7253be"

S = "${WORKDIR}/pycairo-${PV}"

inherit distutils3 pkgconfig

CFLAGS += "-fPIC"

BBCLASSEXTEND = "native"

do_configure() {
	PYTHON=${PYTHON} ./waf configure --prefix=${D}${prefix} --libdir=${D}${libdir}
}

do_compile() {
	./waf build ${PARALLEL_MAKE}
}

do_install() {
	./waf install
	sed \
		-e 's:@prefix@:${prefix}:' \
		-e 's:@VERSION@:${PV}:' \
		-e 's:@includedir@:${includedir}:' \
		py3cairo.pc.in > py3cairo.pc
	install -m 0644 py3cairo.pc ${D}${libdir}/pkgconfig/
}
