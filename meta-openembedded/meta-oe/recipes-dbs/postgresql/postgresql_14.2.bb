require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=75af6e3eeec4a06cdd2e578673236fc3"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
   file://0001-configure.ac-bypass-autoconf-2.69-version-check.patch \
   file://remove_duplicate.patch \
"

SRC_URI[sha256sum] = "2cf78b2e468912f8101d695db5340cf313c2e9f68a612fb71427524e8c9a977a"
