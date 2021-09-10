SUMMARY = "sdbus-c++"
DESCRIPTION = "High-level C++ D-Bus library designed to provide easy-to-use yet powerful API in modern C++"

SECTION = "libs"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=1803fa9c2c3ce8cb06b4861d75310742"

inherit cmake pkgconfig systemd ptest

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'with-external-libsystemd', 'with-builtin-libsystemd', d)} \
                   ${@bb.utils.contains('PTEST_ENABLED', '1', 'with-tests', '', d)}"
PACKAGECONFIG[with-builtin-libsystemd] = ",,sdbus-c++-libsystemd,libcap"
PACKAGECONFIG[with-external-libsystemd] = ",,systemd,libsystemd"
PACKAGECONFIG[with-tests] = "-DBUILD_TESTS=ON -DTESTS_INSTALL_PATH=${libdir}/${BPN}/tests,-DBUILD_TESTS=OFF"

DEPENDS += "expat"

SRCREV = "6e8e5aadb674cccea5bdd55141db5dad887fbacd"
SRCREV_gtest = "a3460d1aeeaa43fdf137a6adefef10ba0b59fe4b"
SRCREV_FORMAT = "default_gtest"

SRC_URI = "git://github.com/Kistler-Group/sdbus-cpp.git;protocol=https;branch=master \
           git://github.com/google/googletest.git;protocol=https;branch=master;name=gtest;destsuffix=git/tests/googletest-src \
           file://0001-Do-not-download-gtest-automatically.patch \
"
SRC_URI += "file://run-ptest"

EXTRA_OECMAKE = "-DBUILD_CODE_GEN=ON \
                 -DBUILD_DOC=ON \
                 -DBUILD_DOXYGEN_DOC=OFF"

S = "${WORKDIR}/git"

do_install:append() {
    if ! ${@bb.utils.contains('PTEST_ENABLED', '1', 'true', 'false', d)}; then
        rm -rf ${D}${sysconfdir}/dbus-1
    fi
}

PTEST_PATH = "${libdir}/${BPN}/tests"
FILES:${PN}-ptest =+ "${sysconfdir}/dbus-1/system.d/"
FILES:${PN}-dev += "${bindir}/sdbus-c++-xml2cpp"
