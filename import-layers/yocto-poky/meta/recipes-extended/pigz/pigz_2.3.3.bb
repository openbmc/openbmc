require pigz.inc
LIC_FILES_CHKSUM = "file://pigz.c;beginline=7;endline=21;md5=a21d4075cb00ab4ca17fce5e7534ca95"

UPSTREAM_CHECK_URI := "${SRC_URI}"
SRC_URI = "http://downloads.yoctoproject.org/mirror/sources/${BP}.tar.gz"
SRC_URI += "file://link-order.patch"

SRC_URI[md5sum] = "01d7a16cce77929cc1a78aa1bdfb68cb"
SRC_URI[sha256sum] = "4e8b67b432ce7907575a549f3e1cac4709781ba0f6b48afea9f59369846b509c"

NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

BBCLASSEXTEND = "native nativesdk"

