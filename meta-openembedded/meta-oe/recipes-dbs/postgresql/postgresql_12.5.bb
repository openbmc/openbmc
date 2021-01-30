require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=fc4ce21960f0c561460d750bc270d11f"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
"

SRC_URI[sha256sum] = "bd0d25341d9578b5473c9506300022de26370879581f5fddd243a886ce79ff95"
