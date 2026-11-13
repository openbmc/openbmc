SUMMARY = "Meson Python build backend (PEP 517)"
HOMEPAGE = "https://github.com/mesonbuild/meson-python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d580b27e67cc0892a5b005b0be114b60"

DEPENDS = " \
	meson-native \
	ninja-native \
	patchelf-native \
	python3-pyproject-metadata-native \
"

inherit pypi python_mesonpy
SRC_URI[sha256sum] = "52c88628b0e5671592dc2306613fb5f6f3615fd24def059b9894f143b7f9a139"

DEPENDS:remove:class-native = "python3-meson-python-native"

RDEPENDS:${PN} = " \
	meson \
	ninja \
	patchelf \
	python3-pyproject-metadata \
"

BBCLASSEXTEND = "native nativesdk"
