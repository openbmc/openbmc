SRCNAME = "bottle"
SRC_URI = "https://pypi.python.org/packages/source/b/${SRCNAME}/${SRCNAME}-${PV}.tar.gz;name=tarball file://LICENSE file://json-format.patch"
SRC_URI[tarball.sha256sum] = "fe0a24b59385596d02df7ae7845fe7d7135eea73799d03348aeb9f3771500051"
SRC_URI[tarball.md5sum] = "f5850258a86224a791171e8ecbb66d99"
require python-bottle.inc
