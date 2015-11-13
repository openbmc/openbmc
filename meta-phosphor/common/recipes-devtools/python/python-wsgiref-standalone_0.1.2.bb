SRCNAME = "wsgiref"
SRC_URI = "https://pypi.python.org/packages/source/w/${SRCNAME}/${SRCNAME}-${PV}.zip;name=tarball file://LICENSE"
SRC_URI[tarball.sha256sum] = "c7e610c800957046c04c8014aab8cce8f0b9f0495c8cd349e57c1f7cabf40e79"
SRC_URI[tarball.md5sum] = "29b146e6ebd0f9fb119fe321f7bcf6cb"
require python-wsgiref-standalone.inc
