SUMMARY = "Downloads, builds, installs, upgrades, and uninstalls Python packages"
HOMEPAGE = "https://pypi.python.org/pypi/setuptools"
SECTION = "devel/python"
LICENSE = "Python-2.0 | ZPL-2.0"
LIC_FILES_CHKSUM = "file://setup.py;beginline=78;endline=78;md5=8a314270dd7a8dbca741775415f1716e"

SRCNAME = "setuptools"

PROVIDES = "python-distribute"

DEPENDS += "python"
DEPENDS_class-native += "python-native"

inherit distutils

SRC_URI = "https://pypi.python.org/packages/source/s/setuptools/setuptools-${PV}.tar.gz"
SRC_URI[md5sum] = "52b4e48939ef311d7204f8fe940764f4"
SRC_URI[sha256sum] = "0994a58df27ea5dc523782a601357a2198b7493dcc99a30d51827a23585b5b1d"

S = "${WORKDIR}/${SRCNAME}-${PV}"


DISTUTILS_INSTALL_ARGS += "--install-lib=${D}${libdir}/${PYTHON_DIR}/site-packages"

do_install_prepend() {
    install -d ${D}/${libdir}/${PYTHON_DIR}/site-packages
}

RDEPENDS_${PN} = "\
  python-stringold \
  python-email \
  python-shell \
  python-distutils \
  python-compression \
"

RDEPENDS_${PN}_class-native = "\
  python-distutils \
  python-compression \
"

RREPLACES_${PN} = "python-distribute"
RPROVIDES_${PN} = "python-distribute"
RCONFLICTS_${PN} = "python-distribute"

BBCLASSEXTEND = "native nativesdk"
