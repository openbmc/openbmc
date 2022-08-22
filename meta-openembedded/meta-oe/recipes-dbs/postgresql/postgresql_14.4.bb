require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=75af6e3eeec4a06cdd2e578673236fc3"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
   file://0001-configure.ac-bypass-autoconf-2.69-version-check.patch \
   file://remove_duplicate.patch \
   file://0001-config_info.c-not-expose-build-info.patch \
"

SRC_URI[sha256sum] = "c23b6237c5231c791511bdc79098617d6852e9e3bdf360efd8b5d15a1a3d8f6a"

CVE_CHECK_IGNORE += "\
   CVE-2017-8806 \
"
