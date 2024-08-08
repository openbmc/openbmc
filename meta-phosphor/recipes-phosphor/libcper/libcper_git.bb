LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a832eda17114b48ae16cda6a500941c2"

DEPENDS = " \
    json-c \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gtest', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gmock', '', d)} \
    "

PV = "1.0+git${SRCPV}"
SRCREV = "9fe2cb5f64b21b8a9a29720423be3c76e059e7db"

SRC_URI = "git://git@github.com/openbmc/libcper.git;protocol=ssh;branch=main"

S = "${WORKDIR}/git"

inherit pkgconfig meson ptest

EXTRA_OEMESON = " \
    -Dtests=${@bb.utils.contains('PTEST_ENABLED', '1', 'enabled', 'disabled', d)} \
"

