require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=fc4ce21960f0c561460d750bc270d11f"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
"

SRC_URI[md5sum] = "a30c023dd7088e44d73be71af2ef404a"
SRC_URI[sha256sum] = "94ed64a6179048190695c86ec707cc25d016056ce10fc9d229267d9a8f1dcf41"
