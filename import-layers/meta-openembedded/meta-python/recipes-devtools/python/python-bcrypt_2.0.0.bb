DESCRIPTION = "Modern password hashing for your software and your servers."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=e19d0fe40c5ce4109e6bcbf4aab2a5bd"

DEPENDS = "python-cffi-native"

SRC_URI[md5sum] = "e7fb17be46904cdb2ae6a062859ee58c"
SRC_URI[sha256sum] = "8b2d197ef220d10eb74625dde7af3b10daa973ae9a1eadd6366f763fad4387fa"

inherit pypi setuptools

RDEPENDS_${PN} = "\
    python-cffi \
    python-six \
"
