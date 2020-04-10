require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=87da2b84884860b71f5f24ab37e7da78"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
"

SRC_URI[md5sum] = "2ee1bd4ec5f49363a3f456f07e599b41"
SRC_URI[sha256sum] = "a09bf3abbaf6763980d0f8acbb943b7629a8b20073de18d867aecdb7988483ed"
