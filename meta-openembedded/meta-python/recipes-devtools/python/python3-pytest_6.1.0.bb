SUMMARY = "Simple powerful testing with python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=81eb9f71d006c6b268cf4388e3c98f7b"

SRC_URI[sha256sum] = "d010e24666435b39a4cf48740b039885642b6c273a3f77be3e7e03554d2806b7"

SRC_URI_append = " file://0001-setup.py-remove-the-setup_requires-for-setuptools-scm.patch "

inherit update-alternatives pypi setuptools3

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-iniconfig \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-pluggy \
    ${PYTHON_PN}-py \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-atomicwrites \
    ${PYTHON_PN}-importlib-metadata \
    ${PYTHON_PN}-pathlib2 \
    ${PYTHON_PN}-wcwidth \
    ${PYTHON_PN}-more-itertools \
    ${PYTHON_PN}-packaging \
"

ALTERNATIVE_${PN} += "py.test pytest"

NATIVE_LINK_NAME[pytest] = "${bindir}/pytest"
ALTERNATIVE_TARGET[pytest] = "${bindir}/pytest"

ALTERNATIVE_LINK_NAME[py.test] = "${bindir}/py.test"
ALTERNATIVE_TARGET[py.test] = "${bindir}/py.test"

BBCLASSEXTEND = "native nativesdk"
