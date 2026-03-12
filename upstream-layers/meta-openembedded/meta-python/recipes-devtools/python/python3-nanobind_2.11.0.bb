SUMMARY = "nanobind: tiny and efficient C++/Python bindings"
DESCRIPTION = "nanobind: tiny and efficient C++/Python bindings"
HOMEPAGE = "https://github.com/wjakob/nanobind"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7646f9ee25e49eaf53f89a10665c568c"

SRC_URI[sha256sum] = "6d98d063c61dbbd05a2d903e59be398bfcff9d59c54fbbc9d4488960485d40d0"

inherit pypi cmake python_setuptools_build_meta

EXTRA_OECMAKE += "-DNB_TEST=OFF"

DEPENDS += "\
    python3-cmake-native \
    python3-scikit-build-native \
    python3-scikit-build-core-native \
    ninja-native \
"

do_install:append() {
    install -d ${D}${libdir}/cmake/${PYPI_PACKAGE}
    install -m 0644 ${S}/cmake/* ${D}${libdir}/cmake/${PYPI_PACKAGE}/
}

FILES:${PN} += "${prefix_native}/* ${prefix_native}/${PN}/* ${base_libdir}/cmake/*"

BBCLASSEXTEND = "native nativesdk"
