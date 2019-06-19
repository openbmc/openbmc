require python-pywbem.inc
inherit setuptools3 update-alternatives

ALTERNATIVE_${PN} = "mof_compiler pywbemcli wbemcli"
ALTERNATIVE_TARGET[mof_compiler] = "${bindir}/mof_compiler"
ALTERNATIVE_TARGET[pywbemcli] = "${bindir}/pywbemcli"
ALTERNATIVE_TARGET[wbemcli] = "${bindir}/wbemcli"

ALTERNATIVE_PRIORITY = "60"
