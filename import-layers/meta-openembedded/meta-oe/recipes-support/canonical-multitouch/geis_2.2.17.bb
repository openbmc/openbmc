SUMMARY = "An implementation of the GEIS interface"
DESCRIPTION = "An implementation of the GEIS (Gesture Engine Interface and Support) \
interface\
GEIS is a library for applications and toolkit programmers which \
provides a consistent platform independent interface for any \
system-wide input gesture recognition mechanism."

HOMEPAGE = "https://launchpad.net/geis"

LICENSE = "GPLv3 & LGPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=6a6a8e020838b23406c81b19c1d46df6 \
    file://COPYING.GPL;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
"

inherit autotools pkgconfig python3native lib_package distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "grail dbus-glib python3 virtual/libx11 libxext libxi libxcb dbus frame"

SRC_URI = "https://launchpad.net/${BPN}/trunk/${PV}/+download/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "2ff9d76a3ea5794516bb02c9d1924faf"
SRC_URI[sha256sum] = "8a60f5683852094038904e690d23cc5a90a980fc52da67f0f28890baa25c70eb"

EXTRA_OECONF = "--disable-integration-tests"

FILES_${PN}-bin = "${bindir}"
RDEPENDS_${PN}-bin = " \
    python3-compression \
    python3-core \
    python3-crypt \
    python3-ctypes \
    python3-fcntl \
    python3-misc \
    python3-pickle \
    python3-shell \
    python3-stringold \
    python3-subprocess \
    python3-textutils \
    python3-threading \
"

FILES_${PN} += " \
    ${datadir}/geisview \
    ${libdir}/${PYTHON_DIR}/site-packages/geis* \
    ${libdir}/${PYTHON_DIR}/site-packages/_*.so \
"

FILES_${PN}-dbg += "${libdir}/${PYTHON_DIR}/site-packages/.debug"

FILES_${PN}-dev += "${libdir}/${PYTHON_DIR}/site-packages/_*.la"

FILES_${PN}-staticdev += "${libdir}/${PYTHON_DIR}/site-packages/_*.a"
