require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=55760ee57ce4e51e4b57f0801ff032dd"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0002-Improve-reproducibility.patch \
   file://0003-configure.ac-bypass-autoconf-2.69-version-check.patch \
   file://0004-config_info.c-not-expose-build-info.patch \
   file://0005-postgresql-fix-ptest-failure-of-sysviews.patch \
   file://0001-tcl.m4-Recognize-tclsh9.patch \
   "

SRC_URI[sha256sum] = "555610c24d53e4316da5b7d3fc25c279d96856d5e0e23ee308c328c5fa881d9f"

CVE_STATUS[CVE-2017-8806] = "not-applicable-config: Doesn't apply to our configuration of postgresql so we can safely ignore it."
