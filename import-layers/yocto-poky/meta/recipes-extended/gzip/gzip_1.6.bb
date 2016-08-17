require gzip.inc

LICENSE = "GPLv3+"

SRC_URI = "${GNU_MIRROR}/gzip/${BP}.tar.gz"
SRC_URI_append_class-target = " file://wrong-path-fix.patch"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://gzip.h;beginline=8;endline=20;md5=6e47caaa630e0c8bf9f1bc8d94a8ed0e"

PROVIDES_append_class-native = " gzip-replacement-native"
NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "38603cb2843bf5681ff41aab3bcd6a20"
SRC_URI[sha256sum] = "97eb83b763d9e5ad35f351fe5517e6b71521d7aac7acf3e3cacdb6b1496d8f7e"
