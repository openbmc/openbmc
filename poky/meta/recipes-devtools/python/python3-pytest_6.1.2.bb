SUMMARY = "Simple powerful testing with python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=81eb9f71d006c6b268cf4388e3c98f7b"

SRC_URI_append = " file://0001-setup.py-remove-the-setup_requires-for-setuptools-scm.patch "

SRC_URI[sha256sum] = "c0a7e94a8cdbc5422a51ccdad8e6f1024795939cc89159a0ae7f0b316ad3823e"

inherit update-alternatives pypi setuptools3

RDEPENDS_${PN}_class-target += " \
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
    ${PYTHON_PN}-toml \
    ${PYTHON_PN}-wcwidth \
"

ALTERNATIVE_${PN} += "py.test pytest"

NATIVE_LINK_NAME[pytest] = "${bindir}/pytest"
ALTERNATIVE_TARGET[pytest] = "${bindir}/pytest"

ALTERNATIVE_LINK_NAME[py.test] = "${bindir}/py.test"
ALTERNATIVE_TARGET[py.test] = "${bindir}/py.test"

BBCLASSEXTEND = "native nativesdk"
