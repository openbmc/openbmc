SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://0002-improve-reproducibility.patch \
          "

SRC_URI[sha256sum] = "e6d4c76be82c676dd7e8c61a29b2ac8510ae108a810b5d1d18fc9a1d2c9a2497"

require wget.inc
