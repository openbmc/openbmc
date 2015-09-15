SUMMARY = "Python bindings for the Cairo canvas library"
HOMEPAGE = "http://cairographics.org/pycairo"
BUGTRACKER = "http://bugs.freedesktop.org"
SECTION = "python-devel"
LICENSE = "LGPLv2.1 & MPL-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=f2e071ab72978431b294a0d696327421 \
                    file://COPYING-LGPL-2.1;md5=fad9b3332be894bab9bc501572864b29 \
                    file://COPYING-MPL-1.1;md5=bfe1f75d606912a4111c90743d6c7325"

# cairo >= 1.8.8
DEPENDS = "cairo"
PR = "r2"

SRC_URI = "http://cairographics.org/releases/py2cairo-${PV}.tar.bz2"

SRC_URI[md5sum] = "20337132c4ab06c1146ad384d55372c5"
SRC_URI[sha256sum] = "d30439f06c2ec1a39e27464c6c828b6eface3b22ee17b2de05dc409e429a7431"

S = "${WORKDIR}/py2cairo-${PV}"

inherit distutils pkgconfig

BBCLASSEXTEND = "native"

do_configure() {
	BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} ./waf configure --prefix=${D}${prefix} --libdir=${D}${libdir}
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
		pycairo.pc.in > pycairo.pc
	install -m 0644 pycairo.pc ${D}${libdir}/pkgconfig/
}
