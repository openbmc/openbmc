SUMMARY = "Simple powerful testing with python"
HOMEPAGE = "https://pypi.org/project/pytest/"
DESCRIPTION = "The pytest framework makes it easy to write small tests, yet scales to support complex functional testing for applications and libraries."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bd27e41b6550fe0fc45356d1d81ee37c"

SRC_URI[sha256sum] = "2f2301e797521b23e4d2585a0a3d7b5e50fdddaaf7e7d6773ea26ddb17c213ab"

DEPENDS += "python3-setuptools-scm-native"

inherit update-alternatives pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-atomicwrites \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-importlib-metadata \
    ${PYTHON_PN}-iniconfig \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-more-itertools \
    ${PYTHON_PN}-packaging \
    ${PYTHON_PN}-pathlib2 \
    ${PYTHON_PN}-pluggy \
    ${PYTHON_PN}-py \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-tomllib \
    ${PYTHON_PN}-wcwidth \
"

ALTERNATIVE:${PN} += "py.test pytest"

NATIVE_LINK_NAME[pytest] = "${bindir}/pytest"
ALTERNATIVE_TARGET[pytest] = "${bindir}/pytest"

ALTERNATIVE_LINK_NAME[py.test] = "${bindir}/py.test"
ALTERNATIVE_TARGET[py.test] = "${bindir}/py.test"

BBCLASSEXTEND = "native nativesdk"
