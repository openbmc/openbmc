SRCNAME = "inflection"
SRC_URI = "https://pypi.python.org/packages/source/i/${SRCNAME}/${SRCNAME}-${PV}.tar.gz;name=tarball"
SRC_URI[tarball.sha256sum] = "18ea7fb7a7d152853386523def08736aa8c32636b047ade55f7578c4edeb16ca"
SRC_URI[tarball.md5sum] = "7941165e9f148e0520023941c0886b40"
require python-inflection.inc
