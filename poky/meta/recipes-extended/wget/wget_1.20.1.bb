SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://0002-improve-reproducibility.patch \
           file://CVE-2019-5953.patch \
          "

SRC_URI[md5sum] = "f6ebe9c7b375fc9832fb1b2028271fb7"
SRC_URI[sha256sum] = "b783b390cb571c837b392857945f5a1f00ec6b043177cc42abb8ee1b542ee1b3"

require wget.inc
