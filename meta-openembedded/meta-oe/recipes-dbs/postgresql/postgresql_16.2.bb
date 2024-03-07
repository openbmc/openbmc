require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=89afbb2d7716371015101c2b2cb4297a"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0002-Improve-reproducibility.patch \
   file://0003-configure.ac-bypass-autoconf-2.69-version-check.patch \
   file://0004-config_info.c-not-expose-build-info.patch \
   file://0005-postgresql-fix-ptest-failure-of-sysviews.patch \
"

SRC_URI[sha256sum] = "446e88294dbc2c9085ab4b7061a646fa604b4bec03521d5ea671c2e5ad9b2952"

CVE_STATUS[CVE-2017-8806] = "not-applicable-config: Ddoesn't apply to out configuration of postgresql so we can safely ignore it."
