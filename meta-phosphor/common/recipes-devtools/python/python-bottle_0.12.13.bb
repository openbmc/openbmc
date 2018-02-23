SRCNAME = "bottle"
SRC_URI = "https://pypi.python.org/packages/bd/99/04dc59ced52a8261ee0f965a8968717a255ea84a36013e527944dbf3468c/${SRCNAME}-${PV}.tar.gz;name=tarball file://LICENSE file://json-format.patch"
SRC_URI[tarball.sha256sum] = "39b751aee0b167be8dffb63ca81b735bbf1dd0905b3bc42761efedee8f123355"
SRC_URI[tarball.md5sum] = "d2fe1b48c1d49217e78bf326b1cad437"
require python-bottle.inc
