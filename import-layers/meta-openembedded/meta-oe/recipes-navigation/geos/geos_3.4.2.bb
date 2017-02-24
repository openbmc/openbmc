require geos.inc

SRC_URI += "file://geos-config-Add-includedir-variable.patch \
            file://fix-gcc6-isnan.patch"

SRC_URI[md5sum] = "fc5df2d926eb7e67f988a43a92683bae"
SRC_URI[sha256sum] = "15e8bfdf7e29087a957b56ac543ea9a80321481cef4d4f63a7b268953ad26c53"
