require python-pywbem.inc
inherit setuptools update-alternatives

DEPENDS += " \
    ${PYTHON_PN}-m2crypto-native \
    ${PYTHON_PN}-typing-native \
"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-m2crypto \
    ${PYTHON_PN}-subprocess \
"

ALTERNATIVE_${PN} = "mof_compiler pywbemcli wbemcli"
ALTERNATIVE_TARGET[mof_compiler] = "${bindir}/mof_compiler"
ALTERNATIVE_TARGET[pywbemcli] = "${bindir}/pywbemcli"
ALTERNATIVE_TARGET[wbemcli] = "${bindir}/wbemcli"

ALTERNATIVE_PRIORITY = "30"
