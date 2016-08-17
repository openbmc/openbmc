SUMMARY = "All packages multimedia packages available for XFCE"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

inherit packagegroup

RDEPENDS_${PN} = " \
    parole \
    xfmpc \
    xfce4-mpc-plugin \
"
