SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://0002-improve-reproducibility.patch \
          "

SRC_URI[md5sum] = "2db6f03d655041f82eb64b8c8a1fa7da"
SRC_URI[sha256sum] = "b39212abe1a73f2b28f4c6cb223c738559caac91d6e416a6d91d4b9d55c9faee"

require wget.inc
