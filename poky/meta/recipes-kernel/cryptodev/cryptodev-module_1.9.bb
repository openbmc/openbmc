require cryptodev.inc

SUMMARY = "A /dev/crypto device driver kernel module"

inherit module

# Header file provided by a separate package
DEPENDS += "cryptodev-linux"

SRC_URI += " \
file://0001-Disable-installing-header-file-provided-by-another-p.patch \
file://0001-ioctl.c-Fix-build-with-linux-4.13.patch \
file://0001-ioctl.c-Fix-build-with-linux-4.17.patch \
file://0001-refactoring-split-big-function-to-simplify-maintaina.patch \
file://0002-refactoring-relocate-code-to-simplify-later-patches.patch \
file://0003-convert-to-new-AEAD-interface-in-kernels-v4.2.patch \
"

EXTRA_OEMAKE='KERNEL_DIR="${STAGING_KERNEL_DIR}" PREFIX="${D}"'

RCONFLICTS_${PN} = "ocf-linux"
RREPLACES_${PN} = "ocf-linux"
