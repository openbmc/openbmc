require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=75af6e3eeec4a06cdd2e578673236fc3"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
   file://0001-configure.ac-bypass-autoconf-2.69-version-check.patch \
   file://remove_duplicate.patch \
"

SRC_URI[sha256sum] = "279057368bf59a919c05ada8f95c5e04abb43e74b9a2a69c3d46a20e07a9af38"
