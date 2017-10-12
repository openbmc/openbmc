HOMEPAGE = "https://bitbucket.org/noppo/gevent-websocket"
SUMMARY = "A websocket library for gevent Python networking library"
DESCRIPTION = "\
  WebSocket is a computer communications protocol, providing full-duplex \
  communication channels over a single TCP connection. gevent-websocket \
  library provides websocket support for gevent. \
  "
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5aa2f1d4ea55d60497aa8c3debf29ab2"

RDEPENDS_${PN} = "python-gevent"

SRCNAME = "gevent-websocket"

SRC_URI = "https://pypi.python.org/packages/98/d2/6fa19239ff1ab072af40ebf339acd91fb97f34617c2ee625b8e34bf42393/gevent-websocket-${PV}.tar.gz"
SRC_URI[md5sum] = "e095bf3358175489a956949c1b4de9ff"
SRC_URI[sha256sum] = "7eaef32968290c9121f7c35b973e2cc302ffb076d018c9068d2f5ca8b2d85fb0"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
