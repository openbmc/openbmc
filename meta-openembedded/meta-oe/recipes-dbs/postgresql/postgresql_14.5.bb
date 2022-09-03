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

SRC_URI[sha256sum] = "d4f72cb5fb857c9a9f75ec8cf091a1771272802f2178f0b2e65b7b6ff64f4a30"

CVE_CHECK_IGNORE += "\
   CVE-2017-8806 \
"
