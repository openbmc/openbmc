require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=87da2b84884860b71f5f24ab37e7da78"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
"

SRC_URI[md5sum] = "87545416ef021eee8621d31a93fcc899"
SRC_URI[sha256sum] = "cda2397215f758b793f741c86be05468257b0e6bcb1a6113882ab5d0df0855c6"
