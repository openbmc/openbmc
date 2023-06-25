SUMMARY = "A simple connection pool for gevent"
DESCRIPTION = "creates a pool of connections that can be used with gevent"
HOMEPAGE = "https://github.com/studio-ousia/gsocketpool"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ba825394aec026b5f94edca44426859"

RDEPENDS:${PN} += " \
    python3-gevent \
    python3-logging \
"

SRC_URI[md5sum] = "49f5f292ef1b60944ae92ca426a5e550"
SRC_URI[sha256sum] = "f2e2749aceadce6b27ca52e2b0a64af99797746a8681e1a2963f72007c14cb14"

inherit pypi setuptools3
