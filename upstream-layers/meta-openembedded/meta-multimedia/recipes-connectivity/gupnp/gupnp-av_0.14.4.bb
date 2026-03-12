SUMMARY = "Helpers for AV applications using UPnP"
DESCRIPTION = "GUPnP-AV is a collection of helpers for building AV (audio/video) applications using GUPnP."

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "gupnp"

inherit gi-docgen meson pkgconfig gobject-introspection vala ptest

SRC_URI = "${GNOME_MIRROR}/${BPN}/0.14/${BPN}-${PV}.tar.xz \
           file://run-ptest"
SRC_URI[sha256sum] = "21d974b3275cb5dcf5b8aa1d9a3fc80e7edca706935f6fbd004c79787138f8c7"

do_configure:prepend(){
    # set ABS_TOP_SRCDIR to ${PTEST_PATH instead of the source-dir on the host}
    sed -i "s!\(-DABS_TOP_SRCDIR=\"\).*!\1${PTEST_PATH}/tests\"'],!" ${S}/tests/meson.build
    # same for DATA_PATH in the other test folder
    sed -i "s!\(-DDATA_PATH=\"\).*!\1${PTEST_PATH}/tests\"']!" ${S}/tests/gtest/meson.build
}

do_install_ptest(){
    cd ${B}/tests
    find . -type f -executable -exec install -D {} ${D}${PTEST_PATH}/tests/{} \;
    cp -r ${S}/tests/gtest/data ${D}${PTEST_PATH}/tests

    # this test is not enabled for execution in 0.14.4 in meson.build
    rm ${D}${PTEST_PATH}/tests/test-search-criteria-parser
}
