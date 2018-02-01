require gzip.inc

LICENSE = "GPLv3+"

SRC_URI = "${GNU_MIRROR}/gzip/${BP}.tar.gz"
SRC_URI_append_class-target = " file://wrong-path-fix.patch"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://gzip.h;beginline=8;endline=20;md5=6e47caaa630e0c8bf9f1bc8d94a8ed0e"

PROVIDES_append_class-native = " gzip-replacement-native"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "732553152814b22dc35aa0267df5286c"
SRC_URI[sha256sum] = "1ff7aedb3d66a0d73f442f6261e4b3860df6fd6c94025c2cb31a202c9c60fe0e"

