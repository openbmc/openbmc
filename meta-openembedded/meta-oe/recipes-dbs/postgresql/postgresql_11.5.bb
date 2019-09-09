require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=87da2b84884860b71f5f24ab37e7da78"

SRC_URI += "\
   file://not-check-libperl.patch \
   file://0001-Add-support-for-RISC-V.patch \
   file://0001-Improve-reproducibility.patch \
"

SRC_URI[md5sum] = "580da94f6d85046ff2a228785ab2cc89"
SRC_URI[sha256sum] = "7fdf23060bfc715144cbf2696cf05b0fa284ad3eb21f0c378591c6bca99ad180"
