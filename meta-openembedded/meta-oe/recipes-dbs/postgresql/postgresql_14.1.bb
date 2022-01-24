require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=255f15687738db8068fbe9b938c90217"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
   file://0001-configure.ac-bypass-autoconf-2.69-version-check.patch \
"

SRC_URI[sha256sum] = "4d3c101ea7ae38982f06bdc73758b53727fb6402ecd9382006fa5ecc7c2ca41f"
