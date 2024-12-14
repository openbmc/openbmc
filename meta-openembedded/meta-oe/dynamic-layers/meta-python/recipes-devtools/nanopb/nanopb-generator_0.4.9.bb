require nanopb.inc

inherit python3-dir

DEPENDS = "protobuf-native"
RDEPENDS:${PN} += "python3-protobuf"

EXTRA_OECMAKE += " \
  -Dnanopb_PYTHON_INSTDIR_OVERRIDE=${PYTHON_SITEPACKAGES_DIR} \
  -Dnanopb_BUILD_RUNTIME=OFF \
  -Dnanopb_BUILD_GENERATOR=ON \
  "

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}"
