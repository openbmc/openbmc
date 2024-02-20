SUMMARY = "Markdown for C"
DESCRIPTION = "MD4C is Markdown parser implementation in C."
HOMEPAGE = "http://github.com/mity/md4c"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=383f39920f391605af6e8e46e60e2378"


S = "${WORKDIR}/git"

SRC_URI = " \
        git://github.com/mity/md4c.git;protocol=https;branch=master \
"

SRCREV = "729e6b8b320caa96328968ab27d7db2235e4fb47"

inherit cmake
