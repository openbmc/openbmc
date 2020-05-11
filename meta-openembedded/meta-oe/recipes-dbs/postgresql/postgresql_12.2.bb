require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=fc4ce21960f0c561460d750bc270d11f"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
"

SRC_URI[md5sum] = "a88ceea8ecf2741307f663e4539b58b7"
SRC_URI[sha256sum] = "ad1dcc4c4fc500786b745635a9e1eba950195ce20b8913f50345bb7d5369b5de"
