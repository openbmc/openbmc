require python-setuptools.inc

DEPENDS += "python3"
DEPENDS_class-native += "python3-native"

inherit distutils3

DISTUTILS_INSTALL_ARGS += "--install-lib=${D}${libdir}/${PYTHON_DIR}/site-packages"

# The installer puts the wrong path in the setuptools.pth file.  Correct it.
do_install_append() {
    rm ${D}${PYTHON_SITEPACKAGES_DIR}/setuptools.pth
    mv ${D}${bindir}/easy_install ${D}${bindir}/easy3_install
    echo "./${SRCNAME}-${PV}-py${PYTHON_BASEVERSION}.egg" > ${D}${PYTHON_SITEPACKAGES_DIR}/setuptools.pth
}

RDEPENDS_${PN} = "\
  python3-distutils \
  python3-compression \
"
RDEPENDS_${PN}_class-target = "\
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
BBCLASSEXTEND = "native"
