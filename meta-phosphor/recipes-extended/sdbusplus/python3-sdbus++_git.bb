SUMMARY = "sdbus++ dbus API / binding generator"
DESCRIPTION = "Generates bindings against sdbusplus for dbus APIs"

inherit setuptools3

include sdbusplus-rev.inc

## The sdbusplus repository has an Apache LICENSE file, which we would
## normally check here, but the python setup script is in a subdirectory
## which requires us to set ${S} below.  When we change ${S} from the root
## of the repository, bitbake can no longer find the LICENSE file.  Point
## to the common Apache license file in poky's meta instead.
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S="${WORKDIR}/git/tools"

DEPENDS += " \
    autoconf-archive-native \
    ${PYTHON_PN}-inflection-native \
    ${PYTHON_PN}-mako-native \
    ${PYTHON_PN}-pyyaml-native \
    "

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-inflection \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-pyyaml \
    "

BBCLASSEXTEND += "native nativesdk"
