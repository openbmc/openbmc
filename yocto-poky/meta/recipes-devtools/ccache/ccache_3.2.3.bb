require ccache.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b3c337e7664559a789d9f7a93e5283c1"

SRCREV = "4cad46e8ee0053144bb00919f0dadd20c1f87013"

SRC_URI += "file://0001-Fix-regression-in-recent-change-related-to-zlib-in-n.patch \
            file://0002-dev.mk.in-fix-file-name-too-long.patch \
"
