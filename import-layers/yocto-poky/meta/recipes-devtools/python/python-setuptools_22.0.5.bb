require python-setuptools.inc

PROVIDES = "python-distribute"

DEPENDS += "python"
DEPENDS_class-native += "python-native"

inherit distutils

DISTUTILS_INSTALL_ARGS += "--install-lib=${D}${PYTHON_SITEPACKAGES_DIR}"

RDEPENDS_${PN} = "\
  python-stringold \
  python-email \
  python-shell \
  python-distutils \
  python-compression \
  python-pkgutil \
  python-plistlib \
  python-numbers \
  python-html \
  python-netserver \
  python-ctypes \
  python-subprocess \
  python-unittest \
  python-compile \
"

RDEPENDS_${PN}_class-native = "\
  python-distutils \
  python-compression \
"

RREPLACES_${PN} = "python-distribute"
RPROVIDES_${PN} = "python-distribute"
RCONFLICTS_${PN} = "python-distribute"

BBCLASSEXTEND = "native nativesdk"
