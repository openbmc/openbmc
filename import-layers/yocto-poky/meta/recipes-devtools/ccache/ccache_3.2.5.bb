require ccache.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b8a4fa173ed91c1a5204ea4f9c9eadc3"

SRCREV = "424d3ae1fb73444c6c38bf189f8fc048f66d6499"

SRC_URI += " \
            file://0002-dev.mk.in-fix-file-name-too-long.patch \
            file://Revert-Create-man-page-in-the-make-install-from-git-.patch \
"
