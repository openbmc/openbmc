SUMMARY = "YouTube Chromecast API"
HOMEPAGE = "https://github.com/ur1katz/casttube"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[md5sum] = "4bb24ba1639d16c8fa367537bf3b88a6"
SRC_URI[sha256sum] = "f25b3c634efe702896233690f7590e8d6311590910f18dbb763b90419d9ef53c"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    python3-requests \
"
