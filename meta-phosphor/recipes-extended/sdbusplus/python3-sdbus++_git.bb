SUMMARY = "sdbus++ dbus API / binding generator"
DESCRIPTION = "Generates bindings against sdbusplus for dbus APIs"
## The sdbusplus repository has an Apache LICENSE file, which we would
## normally check here, but the python setup script is in a subdirectory
## which requires us to set ${S} below.  When we change ${S} from the root
## of the repository, bitbake can no longer find the LICENSE file.  Point
## to the common Apache license file in poky's meta instead.
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS += " \
    ${PYTHON_PN}-inflection-native \
    ${PYTHON_PN}-jsonschema-native \
    ${PYTHON_PN}-mako-native \
    ${PYTHON_PN}-pyyaml-native \
    "
PV = "1.0"

PYPI_PACKAGE = "sdbusplus"

S = "${WORKDIR}/git/tools"

inherit setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-inflection \
    ${PYTHON_PN}-jsonschema \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-pyyaml \
    "

include sdbusplus-rev.inc

BBCLASSEXTEND += "native nativesdk"
