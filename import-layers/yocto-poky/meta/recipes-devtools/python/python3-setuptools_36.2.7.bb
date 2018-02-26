require python-setuptools.inc

DEPENDS += "python3"
DEPENDS_class-native += "python3-native"
DEPENDS_class-nativesdk += "nativesdk-python3"

inherit setuptools3

do_install_append() {
    mv ${D}${bindir}/easy_install ${D}${bindir}/easy3_install
}

RDEPENDS_${PN}_class-native = "\
  python3-distutils \
  python3-compression \
"
RDEPENDS_${PN} = "\
  python3-ctypes \
  python3-distutils \
  python3-email \
  python3-importlib \
  python3-numbers \
  python3-compression \
  python3-shell \
  python3-subprocess \
  python3-textutils \
  python3-pkgutil \
  python3-threading \
  python3-misc \
  python3-unittest \
  python3-xml \
"
BBCLASSEXTEND = "native nativesdk"
