SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
          "

SRC_URI[md5sum] = "a6a908c9ae0e6a4194c628974cc3f05a"
SRC_URI[sha256sum] = "029fbb93bdc1c0c5a7507b6076a6ec2f8d34204a85aa87e5b2f61a9405b290f5"

require wget.inc
