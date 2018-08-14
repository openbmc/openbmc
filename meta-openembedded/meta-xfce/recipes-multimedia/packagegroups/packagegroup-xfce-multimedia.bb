SUMMARY = "All packages multimedia packages available for XFCE"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

# While this item does not require it, it depends on xfmpc and xfc4-mpc-plugin
# that wants mpd which does
LICENSE_FLAGS = "commercial"

RDEPENDS_${PN} = " \
    parole \
    xfmpc \
    xfce4-mpc-plugin \
"
