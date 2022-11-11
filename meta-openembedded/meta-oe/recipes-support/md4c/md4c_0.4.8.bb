SUMMARY = "Markdown for C"
DESCRIPTION = "MD4C is Markdown parser implementation in C."
HOMEPAGE = "http://github.com/mity/md4c"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7e0fbcf3701aad22f2d2e0624a703795"


S = "${WORKDIR}/git"

SRC_URI = " \
        git://github.com/mity/md4c.git;protocol=https;branch=master \
"

SRCREV = "c3340b480e5232711858108be07460a9836c8ab5"

inherit cmake
