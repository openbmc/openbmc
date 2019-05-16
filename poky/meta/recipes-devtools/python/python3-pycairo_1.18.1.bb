SUMMARY = "Python bindings for the Cairo canvas library"
HOMEPAGE = "http://cairographics.org/pycairo"
BUGTRACKER = "http://bugs.freedesktop.org"
SECTION = "python-devel"
LICENSE = "LGPLv2.1 & MPLv1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=f2e071ab72978431b294a0d696327421 \
                    file://COPYING-LGPL-2.1;md5=fad9b3332be894bab9bc501572864b29 \
                    file://COPYING-MPL-1.1;md5=bfe1f75d606912a4111c90743d6c7325"

# cairo >= 1.14
DEPENDS = "cairo python3"

SRC_URI = "https://github.com/pygobject/pycairo/releases/download/v${PV}/pycairo-${PV}.tar.gz"
UPSTREAM_CHECK_URI = "https://github.com/pygobject/pycairo/releases/"

SRC_URI[md5sum] = "7610da8a40a7bed548991aa3416431d1"
SRC_URI[sha256sum] = "70172e58b6bad7572a3518c26729b074acdde15e6fee6cbab6d3528ad552b786"

S = "${WORKDIR}/pycairo-${PV}"

inherit meson pkgconfig

CFLAGS += "-fPIC"

BBCLASSEXTEND = "native"

FILES_${PN} = "${PYTHON_SITEPACKAGES_DIR}/*"
