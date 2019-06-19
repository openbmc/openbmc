require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=87da2b84884860b71f5f24ab37e7da78"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
"

SRC_URI[md5sum] = "c2a729b754b8de86a969c86ec25db076"
SRC_URI[sha256sum] = "2a85e082fc225944821dfd23990e32dfcd2284c19060864b0ad4ca537d30522d"
