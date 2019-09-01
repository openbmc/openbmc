require libgpiod.inc

DEPENDS += "autoconf-archive-native"

SRC_URI[md5sum] = "585b4bb431f99c4ba9b3ee58b9d494c1"
SRC_URI[sha256sum] = "21ae8fd1f8dafc2eb2ba50e652390cf533d21351419a7426255895cb52e21b1c"

# enable tools and cxx bindings
PACKAGECONFIG ?= "cxx tools"

PACKAGECONFIG[cxx] = "--enable-bindings-cxx,--disable-bindings-cxx"
PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev"

PACKAGECONFIG[python3] = "--enable-bindings-python,--disable-bindings-python,python3"

inherit python3native

PACKAGES =+ "${PN}-python"
FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RRECOMMENDS_PYTHON = "${@bb.utils.contains('PACKAGECONFIG', 'python3', '${PN}-python', '',d)}"
RRECOMMENDS_${PN}-python += "${RRECOMMENDS_PYTHON}"
