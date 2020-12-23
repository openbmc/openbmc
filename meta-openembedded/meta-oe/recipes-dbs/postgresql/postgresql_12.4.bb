require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=fc4ce21960f0c561460d750bc270d11f"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
"

SRC_URI[sha256sum] = "bee93fbe2c32f59419cb162bcc0145c58da9a8644ee154a30b9a5ce47de606cc"
