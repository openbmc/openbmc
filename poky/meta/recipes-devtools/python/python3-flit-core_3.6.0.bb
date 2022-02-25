SUMMARY = "This provides a PEP 517 build backend for packages using Flit."
DESCRIPTION = "This provides a PEP 517 build backend for packages using \
Flit. The only public interface is the API specified by PEP 517, at \
flit_core.buildapi."
HOMEPAGE = "https://github.com/pypa/flit"
BUGTRACKER = "https://github.com/pypa/flit/issues"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41eb78fa8a872983a882c694a8305f08"

SRC_URI[sha256sum] = "b1464e006df4df4c8eeb37671c0e0ce66e1d04e4a36d91b702f180a25fde3c11"

inherit pip_install_wheel python3native python3-dir pypi setuptools3-base

DEPENDS:remove:class-native = " python3-pip-native"
DEPENDS:append:class-native = " unzip-native"

# We need the full flit tarball
PYPI_PACKAGE = "flit"

PIP_INSTALL_PACKAGE = "flit_core"
PIP_INSTALL_DIST_PATH = "${S}/flit_core/dist"

do_compile () {
    nativepython3 flit_core/build_dists.py
}

do_install:class-native () {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip -d ${D}${PYTHON_SITEPACKAGES_DIR} ./flit_core/dist/flit_core-${PV}-py3-none-any.whl
}

FILES:${PN} += "\
    ${PYTHON_SITEPACKAGES_DIR}/flit_core/* \
    ${PYTHON_SITEPACKAGES_DIR}/flit_core-${PV}.dist-info/* \
"

PACKAGES =+ "${PN}-tests"

FILES:${PN}-tests += "\
    ${PYTHON_SITEPACKAGES_DIR}/flit_core/tests/* \
"

BBCLASSEXTEND = "native nativesdk"

