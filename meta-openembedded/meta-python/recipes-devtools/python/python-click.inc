SUMMARY = "A simple wrapper around optparse for powerful command line utilities."
DESCRIPTION = "\
Click is a Python package for creating beautiful command line interfaces \
in a composable way with as little code as necessary. It's the "Command \
Line Interface Creation Kit". It's highly configurable but comes with \
sensible defaults out of the box."
HOMEPAGE = "http://click.pocoo.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=09e8b7869f94ba9e9a7b260d53c65dab"

SRC_URI[md5sum] = "fc4cc00c4863833230d3af92af48abd4"
SRC_URI[sha256sum] = "f15516df478d5a56180fbf80e68f206010e6d160fc39fa508b65e035fd75130b"

CLEANBROKEN = "1"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-threading \
    "

BBCLASSEXTEND = "native nativesdk"
