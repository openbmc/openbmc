require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=fc4ce21960f0c561460d750bc270d11f"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
   file://0001-configure.in-bypass-autoconf-2.69-version-check.patch \
"

SRC_URI[sha256sum] = "12345c83b89aa29808568977f5200d6da00f88a035517f925293355432ffe61f"
