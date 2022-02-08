DESCRIPTION = "A Python library for generating news feeds in RSS and Atom formats"
HOMEPAGE = "http://code.google.com/p/feedformatter/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=258e3f39e2383fbd011035d04311008d"
SRCREV = "7391193c83e10420b5a2d8ef846d23fc368c6d85"

SRC_URI = "git://github.com/marianoguerra/feedformatter.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-xml \
    "
