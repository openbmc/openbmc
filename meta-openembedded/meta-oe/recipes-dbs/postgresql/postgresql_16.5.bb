require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=89afbb2d7716371015101c2b2cb4297a"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0002-Improve-reproducibility.patch \
   file://0003-configure.ac-bypass-autoconf-2.69-version-check.patch \
   file://0004-config_info.c-not-expose-build-info.patch \
   file://0005-postgresql-fix-ptest-failure-of-sysviews.patch \
   file://0001-tcl.m4-Recognize-tclsh9.patch \
"

SRC_URI[sha256sum] = "a6cbbb7037f98cb8afa7d3970b7c48040cf02b115e39253a0c037a8bb8e778f0"

CVE_STATUS[CVE-2017-8806] = "not-applicable-config: Ddoesn't apply to out configuration of postgresql so we can safely ignore it."
