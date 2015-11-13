SRCNAME = "Rocket"
SRC_URI = "https://pypi.python.org/packages/source/r/rocket/${SRCNAME}-${PV}.zip;name=tarball file://LICENSE file://setup.patch"
SRC_URI[tarball.sha256sum] = "7b7a3df4d9257e369d85bcc2013351704ffe364ff45d4710e6a30b2efc63a731"
SRC_URI[tarball.md5sum] = "fa611955154b486bb91e632a43e90f4b"
require python-rocket.inc
