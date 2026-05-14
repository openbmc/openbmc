SUMMARY = "Markdown for C"
DESCRIPTION = "MD4C is Markdown parser implementation in C."
HOMEPAGE = "http://github.com/mity/md4c"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=383f39920f391605af6e8e46e60e2378"



SRC_URI = " \
        git://github.com/mity/md4c.git;protocol=https;branch=master;tag=release-${PV} \
"

SRCREV = "472c417005c2c71b8617de4f7b8d6b30411d78f4"

inherit cmake
