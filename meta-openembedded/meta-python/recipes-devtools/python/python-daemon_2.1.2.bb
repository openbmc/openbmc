DESCRIPTION = "Library to implement a well-behaved Unix daemon process"
HOMEPAGE = "https://pagure.io/python-daemon/"
SECTION = "devel/python"

DEPENDS += "python-docutils-native"
RDEPENDS_${PN} = "python-docutils \
                  python-lockfile (>= 0.10) \
                  python-resource \
"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit pypi setuptools

SRC_URI[md5sum] = "9c57343d81f2a96c51cffeab982b04d2"
SRC_URI[sha256sum] = "261c859be5c12ae7d4286dc6951e87e9e1a70a882a8b41fd926efc1ec4214f73"

# Fix for build error in Yocto:
#     i = p.rfind('/') + 1
# AttributeError: 'NoneType' object has no attribute 'rfind'
#S = "${WORKDIR}/python-daemon"
SRC_URI_append = " \
                  file://0001-Workaround-for-issue-2-1.patch \
"

PYPI_PACKAGE = "python-daemon"
