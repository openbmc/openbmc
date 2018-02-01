SUMMARY = "World timezone definitions, modern and historical"
HOMEPAGE = "http://pythonhosted.org/pytz"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=39ea92752a35cf67d8a885d8e3af3c69"

inherit pypi
PYPI_PACKAGE_EXT = "zip"

SRC_URI[md5sum] = "f89bde8a811c8a1a5bac17eaaa94383c"
SRC_URI[sha256sum] = "f5c056e8f62d45ba8215e5cb8f50dfccb198b4b9fbea8500674f3443e4689589"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
