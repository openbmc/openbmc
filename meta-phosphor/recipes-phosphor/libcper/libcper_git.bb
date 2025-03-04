LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a832eda17114b48ae16cda6a500941c2"

DEPENDS = " \
    json-c \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gtest', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gmock', '', d)} \
    "

PV = "1.0+git${SRCPV}"
SRCREV = "dd9e8d9847ea478c96b936361952e7ba2cdc832a"

SRC_URI = "git://github.com/openbmc/libcper.git;protocol=https;branch=main"

S = "${WORKDIR}/git"

inherit pkgconfig meson ptest

EXTRA_OEMESON = " \
    -Dtests=${@bb.utils.contains('PTEST_ENABLED', '1', 'enabled', 'disabled', d)} \
"

