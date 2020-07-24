LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4f59d6446bf2e004e80df1a0937129fa"

SRC_URI = "git://gitlab.freedesktop.org/mesa/parallel-deqp-runner.git;protocol=https \
           file://0001-meson.build-WORKAROUND-Remove-vulkan-dependency.patch \
           "

# Modify these as desired
PV = "2020.06.15+git${SRCPV}"
SRCREV = "e1642fb691d29b1462504b58916f7f514a963e80"

S = "${WORKDIR}/git"

inherit pkgconfig meson
