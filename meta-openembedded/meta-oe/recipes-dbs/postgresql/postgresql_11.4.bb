require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=87da2b84884860b71f5f24ab37e7da78"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
"

SRC_URI[md5sum] = "dab5eed8a5f9204bf2f03a209eead4c3"
SRC_URI[sha256sum] = "02802ddffd1590805beddd1e464dd28a46a41a5f1e1df04bab4f46663195cc8b"
