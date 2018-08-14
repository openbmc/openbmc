require ccache.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=dae379a85bb6e9d594773e0aa64876f6"

SRC_URI[md5sum] = "eee58db7cce892febddb989308dc568f"
SRC_URI[sha256sum] = "190576a6e938760ec8113523e6fd380141117303e90766cc4802e770422b30c6"

SRC_URI += " \
            file://0002-dev.mk.in-fix-file-name-too-long.patch \
            file://Revert-Create-man-page-in-the-make-install-from-git-.patch \
"
