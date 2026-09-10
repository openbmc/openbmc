LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4f59d6446bf2e004e80df1a0937129fa"

SRC_URI = "git://gitlab.freedesktop.org/mesa/parallel-deqp-runner.git;protocol=https;branch=master \
           file://0001-meson.build-WORKAROUND-Remove-vulkan-dependency.patch \
           file://0001-memmove-and-memchr-are-C-APIs-not-C-std-namespace.patch \
           "

# Modify these as desired
PV = "2020.06.15+git"
SRCREV = "e1642fb691d29b1462504b58916f7f514a963e80"

# Upstream only has mesa-ci-YYYY-MM-DD tags, which the default tag regex
# truncates to the year (e.g. "2020") and which use "-" separators that do not
# compare against the dotted PV; track commits instead.
UPSTREAM_CHECK_COMMITS = "1"


inherit pkgconfig meson
