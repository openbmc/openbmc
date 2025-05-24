SUMMARY = "nanobind: tiny and efficient C++/Python bindings"
DESCRIPTION = "nanobind: tiny and efficient C++/Python bindings"
HOMEPAGE = "https://github.com/wjakob/nanobind"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7646f9ee25e49eaf53f89a10665c568c"

SRC_URI[sha256sum] = "e05c6816a844aa699e46408add3bff8c743af9fd3d38eafa307a73633fddf94e"

inherit pypi python_setuptools_build_meta cmake lib_package

EXTRA_OECMAKE += "-DNB_TEST=OFF"

DEPENDS += "\
    python3-scikit-build-native \
    python3-scikit-build-core-native \
    ninja-native \
"

do_install:append() {
    install -d ${D}${base_libdir}/cmake/${PN}
    install -m 0644 ${S}/cmake/* ${D}${base_libdir}/cmake/${PN}/
}

FILES:${PN} += "${prefix_native}/* ${prefix_native}/${PN}/* ${base_libdir}/cmake/*"

BBCLASSEXTEND = "native nativesdk"
