require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=c31f662bb2bfb3b4187fe9a53e0ffe7c"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
   file://0001-configure.ac-bypass-autoconf-2.69-version-check.patch \
   file://0001-config_info.c-not-expose-build-info.patch \
   file://0001-postgresql-fix-ptest-failure-of-sysviews.patch \
"

SRC_URI[sha256sum] = "8f53aa95d78eb8e82536ea46b68187793b42bba3b4f65aa342f540b23c9b10a6"

CVE_STATUS[CVE-2017-8806] = "not-applicable-config: Ddoesn't apply to out configuration of postgresql so we can safely ignore it."
