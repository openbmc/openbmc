require libgpiod.inc

DEPENDS += "autoconf-archive-native"

SRC_URI[md5sum] = "d4826720a8de13379436f9c207237bc0"
SRC_URI[sha256sum] = "0ad080d1eb89c422cf13001293ffa72869ca13028e85dab5f6b3ba90f88da46d"

# enable cxx bindings
PACKAGECONFIG ?= "cxx"

PACKAGECONFIG[cxx] = "--enable-bindings-cxx,--disable-bindings-cxx"
PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev"

PACKAGECONFIG[python3] = "--enable-bindings-python,--disable-bindings-python,python3"

inherit python3native

PACKAGES =+ "${PN}-python"
FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RRECOMMENDS_PYTHON = "${@bb.utils.contains('PACKAGECONFIG', 'python3', '${PN}-python', '',d)}"
RRECOMMENDS_${PN}-python += "${RRECOMMENDS_PYTHON}"
