SUMMARY = "Python bindings for the Cairo canvas library"
HOMEPAGE = "http://cairographics.org/pycairo"
BUGTRACKER = "http://bugs.freedesktop.org"
SECTION = "python-devel"
LICENSE = "LGPLv2.1 & MPLv1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=f3713ca2c28d9312ad718520b6dc3eee \
                    file://COPYING-LGPL-2.1;md5=fad9b3332be894bab9bc501572864b29 \
                    file://COPYING-MPL-1.1;md5=bfe1f75d606912a4111c90743d6c7325"

# cairo >= 1.14
DEPENDS = "cairo python3"

SRC_URI = "https://github.com/pygobject/pycairo/releases/download/v${PV}/pycairo-${PV}.tar.gz"
UPSTREAM_CHECK_URI = "https://github.com/pygobject/pycairo/releases/"

SRC_URI[md5sum] = "59bc5c5d1debc3af0f6791af9d612551"
SRC_URI[sha256sum] = "2c143183280feb67f5beb4e543fd49990c28e7df427301ede04fc550d3562e84"

S = "${WORKDIR}/pycairo-${PV}"

inherit meson pkgconfig

CFLAGS += "-fPIC"

BBCLASSEXTEND = "native"

FILES_${PN} = "${PYTHON_SITEPACKAGES_DIR}/*"
