SUMMARY = "klibc utils for initramfs statically compiled"

FILESPATH =. "${FILE_DIRNAME}/klibc-${PV}:"

PACKAGES = "${PN}"
FILES_${PN} = ""

KLIBC_UTILS_VARIANT = "static"
KLIBC_UTILS_PKGNAME = "klibc-static-utils"

require klibc-utils.inc
require klibc.inc

SRC_URI += "file://0001-klibc-static-utils-do-not-build-shared-binaries.patch"

# avoid textrel if linking with -pie
SECURITY_CFLAGS = ""
SECURITY_LDFLAGS = ""
