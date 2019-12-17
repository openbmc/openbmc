require python-setuptools.inc

PROVIDES = "python-distribute"

inherit setuptools

RREPLACES_${PN} = "python-distribute"
RPROVIDES_${PN} = "python-distribute"
RCONFLICTS_${PN} = "python-distribute"
