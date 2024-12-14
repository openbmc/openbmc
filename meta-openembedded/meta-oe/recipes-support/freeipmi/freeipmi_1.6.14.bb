LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.ZRESEARCH;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.bmc-watchdog;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.ipmi-dcmi;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.ipmi-fru;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.ipmiconsole;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.ipmidetect;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.ipmimonitoring;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.ipmiping;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.ipmipower;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.ipmiseld;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.pstdout;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.sunbmc;md5=c03f21cd76ff5caba6b890d1213cbfbb"

SRC_URI = "${GNU_MIRROR}/freeipmi/freeipmi-${PV}.tar.gz \
           file://0001-Fix-undeclared-function-errors.patch"
SRC_URI[sha256sum] = "1a3dac5c76b7ccc4d4f86aa12b8ef9b212baef7489bf05e899b89abb7e14edb5"

DEPENDS = "libgcrypt"
DEPENDS:append:libc-musl = " argp-standalone"

inherit pkgconfig autotools

EXTRA_OECONF = "--without-random-device"

