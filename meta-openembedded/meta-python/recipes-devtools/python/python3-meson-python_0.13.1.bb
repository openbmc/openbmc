SUMMARY = "Meson Python build backend (PEP 517)"
HOMEPAGE = "https://github.com/mesonbuild/meson-python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d580b27e67cc0892a5b005b0be114b60"

DEPENDS = " \
	meson-native ninja-native patchelf-native \
	python3-pyproject-metadata-native \
"

PYPI_PACKAGE = "meson_python"

inherit pypi python_mesonpy
SRC_URI[sha256sum] = "63b3170001425c42fa4cfedadb9051cbd28925ff8eed7c40d36ba0099e3c7618"

DEPENDS:remove:class-native = "python3-meson-python-native"

RDEPENDS:${PN} = " \
	meson ninja patchelf \
	python3-pyproject-metadata \
"

BBCLASSEXTEND = "native nativesdk"
