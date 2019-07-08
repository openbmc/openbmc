require python-pywbem.inc
inherit setuptools

DEPENDS += " \
    ${PYTHON_PN}-m2crypto-native \
    ${PYTHON_PN}-typing-native \
"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-m2crypto \
    ${PYTHON_PN}-subprocess \
"
