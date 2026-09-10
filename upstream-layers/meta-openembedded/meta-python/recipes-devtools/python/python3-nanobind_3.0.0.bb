SUMMARY = "nanobind: tiny and efficient C++/Python bindings"
DESCRIPTION = "nanobind: tiny and efficient C++/Python bindings"
HOMEPAGE = "https://github.com/wjakob/nanobind"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7646f9ee25e49eaf53f89a10665c568c"

SRC_URI[sha256sum] = "68583c41d58af77649b830407d2ba84627f21f07ad4ae2e32c6988fb96eaa71a"

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
