SUMMARY = "SQLite ORM light header only library for modern C++"
HOMEPAGE = "https://github.com/fnc12/sqlite_orm"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b5bf5ee8bb16d8e19359efe11bdc3f2"

inherit cmake

DEPENDS += "sqlite3"

SRCREV = "e8a9e9416f421303f4b8970caab26dadf8bae98b"
SRC_URI = "git://github.com/fnc12/sqlite_orm;protocol=https;branch=master"
S = "${WORKDIR}/git"

EXTRA_OECMAKE += "-DSqliteOrm_BuildTests=OFF -DLIB_INSTALL_DIR=${libdir} \
                  -DCMAKE_INSTALL_DIR=${libdir}/cmake \
                  -DPKGCONFIG_INSTALL_DIR=${libdir}/pkgconfig"

BBCLASSEXTEND = "native nativesdk"

FILES:${PN}-dev += "${libdir}/cmake/${BPN}"

# Header-only library
RDEPENDS:${PN}-dev = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
