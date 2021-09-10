require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=255f15687738db8068fbe9b938c90217"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
   file://0001-configure.in-bypass-autoconf-2.69-version-check.patch \
"

SRC_URI[sha256sum] = "3cd9454fa8c7a6255b6743b767700925ead1b9ab0d7a0f9dcb1151010f8eb4a1"
