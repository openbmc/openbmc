require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=87da2b84884860b71f5f24ab37e7da78"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
"

SRC_URI[md5sum] = "19d43be679cb0d55363feb8926af3a0f"
SRC_URI[sha256sum] = "2676b9ce09c21978032070b6794696e0aa5a476e3d21d60afc036dc0a9c09405"
