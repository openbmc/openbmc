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

SRC_URI = "${GNU_MIRROR}/freeipmi/freeipmi-${PV}.tar.gz"
SRC_URI[sha256sum] = "d6929c354639f5ce75b5b1897e8b366eb63625c23e5c4590a7aea034fe2b8caf"

DEPENDS = "libgcrypt"
DEPENDS:append:libc-musl = " argp-standalone"

inherit pkgconfig autotools

EXTRA_OECONF = "--without-random-device"

