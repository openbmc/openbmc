SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://0002-improve-reproducibility.patch \
          "

SRC_URI[md5sum] = "db4e6dc7977cbddcd543b240079a4899"
SRC_URI[sha256sum] = "31cccfc6630528db1c8e3a06f6decf2a370060b982841cfab2b8677400a5092e"

require wget.inc
