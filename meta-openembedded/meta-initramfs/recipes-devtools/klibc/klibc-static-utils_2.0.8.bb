SUMMARY = "klibc utils for initramfs statically compiled"

FILESPATH =. "${FILE_DIRNAME}/klibc-${PV}:"

PACKAGES = "${PN}"
FILES:${PN} = ""

KLIBC_UTILS_VARIANT = "static"
KLIBC_UTILS_PKGNAME = "klibc-static-utils"

require klibc-utils.inc
require klibc.inc
