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
SRC_URI[sha256sum] = "f95c2b73797c4a0341a42a7b3c43efb60954c4130d082ad348fd40da554b4e85"

UPSTREAM_CHECK_URI = "https://ftp.gnu.org/gnu/freeipmi/"
UPSTREAM_CHECK_REGEX = "${BPN}-(?P<pver>\d+(\.\d+)+)\.tar\.gz"

DEPENDS = "libgcrypt"
DEPENDS:append:libc-musl = " argp-standalone"

inherit pkgconfig autotools

EXTRA_OECONF = "--without-random-device"

CVE_STATUS[CVE-2026-33554] = "fixed-version: fixed since 1.6.17"
