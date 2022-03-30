SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://0002-improve-reproducibility.patch \
          "

SRC_URI[sha256sum] = "5726bb8bc5ca0f6dc7110f6416e4bb7019e2d2ff5bf93d1ca2ffcc6656f220e5"

require wget.inc
