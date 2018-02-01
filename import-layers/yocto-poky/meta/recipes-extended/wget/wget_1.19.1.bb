SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://CVE-2017-6508.patch \
          "

SRC_URI[md5sum] = "87cea36b7161fd43e3fd51a4e8b89689"
SRC_URI[sha256sum] = "9e4f12da38cc6167d0752d934abe27c7b1599a9af294e73829be7ac7b5b4da40"

require wget.inc
