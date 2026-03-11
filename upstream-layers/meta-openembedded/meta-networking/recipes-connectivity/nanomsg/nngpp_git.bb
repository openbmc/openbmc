DESCRIPTION = "C++ wrapper around the nanomsg NNG API"
HOMEPAGE = "https://github.com/cwzx/nngpp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://license.txt;md5=6d17d78c3597e0d4452fb1c63bf7c58e"
DEPENDS = "nng"

SRCREV = "8da8c026bd551b7685a8a140909ff96cfe91bf90"
PV = "1.3.0+git"
SRC_URI = "git://github.com/cwzx/nngpp;branch=master;protocol=https \
           file://0001-cmake-Bump-minimum-version-to-3.10.patch \
          "

inherit cmake
