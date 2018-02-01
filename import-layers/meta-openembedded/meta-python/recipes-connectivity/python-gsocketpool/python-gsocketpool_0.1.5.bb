SUMMARY = "A simple connection pool for gevent"
DESCRIPTION = "creates a pool of connections that can be used with gevent"
HOMEPAGE = "https://github.com/studio-ousia/gsocketpool"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ba825394aec026b5f94edca44426859"
DEPENDS += "python-gevent"
RDEPENDS_${PN} += "python-gevent"

SRC_URI_append = " \
    file://0001-fix_setup_py.patch;patch=1;pnum=1 \
"

SRC_URI[md5sum] = "04f618864b18d6b06f774994f172ef49"
SRC_URI[sha256sum] = "b6b73deab9bcbc428d4813697eebe5c3b9c40a971f62e13607b881aa749af9d0"

inherit pypi setuptools
