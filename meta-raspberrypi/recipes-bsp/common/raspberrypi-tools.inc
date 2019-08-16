SRCREV ?= "76f64a28dc4685507d12f57fe5cfc89f6c32f8e6"
RPITOOLS_SRC_URI ?= "https://github.com/raspberrypi/tools/archive/${SRCREV}.tar.gz"
RPITOOLS_S ?= "${WORKDIR}/tools-${SRCREV}"

SRC_URI = "${RPITOOLS_SRC_URI}"
SRC_URI[md5sum] = "e2d4709b62e1536aeaaeabcbae82a03e"
SRC_URI[sha256sum] = "5f1c5eb8af9dff71b7cae30ffa6541d689d269a7b86013b365215adfb4c1c27d"

PV = "0.0+git${SRCREV}"
