SUMMARY = "A gevent based messagpack rpc library"
DESCRIPTION = "mprpc is a fast implementation of the messagepack rpc protocol for python. \
It is based on gevent for handling connections and enabling concurrent connections."
HOMEPAGE = "https://github.com/studio-ousia/mprpc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ba825394aec026b5f94edca44426859"
DEPENDS += "python-gevent"
RDEPENDS_${PN} += "python-gevent python-msgpack python-gsocketpool"

SRC_URI_append = " \
    file://0001-fix_setup_py.patch;patch=1;pnum=1 \
"

SRC_URI[md5sum] = "449e6239eb5ff07b9cceb86e1ab0c2ee"
SRC_URI[sha256sum] = "5881cc7fbb8de814e2b4aa5958bfe147c5c301e46749190f0e6abf373cf56d82"

inherit pypi setuptools
