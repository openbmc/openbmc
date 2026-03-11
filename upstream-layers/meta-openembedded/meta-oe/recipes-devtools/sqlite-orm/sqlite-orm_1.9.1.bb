SUMMARY = "SQLite ORM light header only library for modern C++"
HOMEPAGE = "https://github.com/fnc12/sqlite_orm"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ae09d45eac4aa08d013b5f2e01c67f6"

inherit cmake

DEPENDS += "sqlite3"

SRCREV = "5f1a2ce84a3d72711b4f0a440fdaba977868ae67"
SRC_URI = " \
    git://github.com/fnc12/sqlite_orm;protocol=https;branch=master \
"

EXTRA_OECMAKE += "-DBUILD_TESTING=OFF -DLIB_INSTALL_DIR=${libdir} \
                  -DCMAKE_INSTALL_DIR=${libdir}/cmake \
                  -DPKGCONFIG_INSTALL_DIR=${libdir}/pkgconfig"

BBCLASSEXTEND = "native nativesdk"

FILES:${PN}-dev += "${libdir}/cmake/${BPN}"

# Header-only library
RDEPENDS:${PN}-dev = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
