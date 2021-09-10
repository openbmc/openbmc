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

SRC_URI[md5sum] = "fa88a28cadbfb34192fe743d32c0ee33"
SRC_URI[sha256sum] = "1ee72b035b21a475e1ed648e26541b04e5d7e753d75ca79de8c583b25785531b"

S = "${WORKDIR}/pycairo-${PV}"

inherit meson pkgconfig python3targetconfig

CFLAGS += "-fPIC"

BBCLASSEXTEND = "native"

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}/*"
