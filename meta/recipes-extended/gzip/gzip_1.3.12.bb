require gzip.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://gzip.h;endline=22;md5=c0934ad1900d927f86556153d4c76d23 \
                    file://lzw.h;endline=19;md5=c273e09a02edd9801cc74d39683049e9 "

SRC_URI = "${GNU_MIRROR}/gzip/gzip-${PV}.tar.gz \
           file://m4-extensions-fix.patch \
           file://dup-def-fix.patch"

SRC_URI[md5sum] = "b5bac2d21840ae077e0217bc5e4845b1"
SRC_URI[sha256sum] = "3f565be05f7f3d1aff117c030eb7c738300510b7d098cedea796ca8e4cd587af"

PR = "r2"
