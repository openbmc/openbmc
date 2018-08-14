SUMMARY = "klibc utils for initramfs"

FILESPATH =. "${FILE_DIRNAME}/klibc-${PV}:"

PACKAGES = "${PN}"
FILES_${PN} = ""

KLIBC_UTILS_VARIANT = "shared"
KLIBC_UTILS_PKGNAME = "klibc-utils"

require klibc-utils.inc
require klibc.inc

DEPENDS = "klibc"
