require ccache.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7fe21f9470f2305e95e7d8a632255079"

SRC_URI[md5sum] = "95ab3c56284129cc2a32460c23069516"
SRC_URI[sha256sum] = "24f15bf389e38c41548c9c259532187774ec0cb9686c3497bbb75504c8dc404f"

SRC_URI += " \
            file://0002-dev.mk.in-fix-file-name-too-long.patch \
            file://Revert-Create-man-page-in-the-make-install-from-git-.patch \
"
