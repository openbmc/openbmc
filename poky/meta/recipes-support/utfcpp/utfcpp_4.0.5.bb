SUMMARY = " UTF-8 with C++ in a Portable Way"
HOMEPAGE = "https://github.com/nemtrif/utfcpp"

LICENSE = "BSL-1.0 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4224ccaecb14d942c71d31bef20d78c \
                    file://extern/ftest/LICENSE;md5=d33c6488d3b003723a5f17ac984db030"

SRC_URI = "gitsm://github.com/nemtrif/utfcpp;protocol=https;branch=master"

SRCREV = "6be08bbea14ffa0a5c594257fb6285a054395cd7"

S = "${WORKDIR}/git"

inherit cmake

FILES:${PN}-dev += "${datadir}/utf8cpp/cmake"
