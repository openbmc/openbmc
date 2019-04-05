require libgpiod.inc

DEPENDS += "autoconf-archive-native"

SRC_URI[md5sum] = "9f7530a5d56f070ba0af78d6ba077973"
SRC_URI[sha256sum] = "6ec837f23e8f2196e5976dec4ac81403170830075e7f33ede1394eaf67f2e962"

PACKAGECONFIG[cxx] = "--enable-bindings-cxx,--disable-bindings-cxx"
PACKAGECONFIG[tests] = "--enable-tests --enable-install-tests,--disable-tests --disable-install-tests,kmod udev"

PACKAGECONFIG[python3] = "--enable-bindings-python,--disable-bindings-python,python3"
inherit ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3native', '', d)}

PACKAGES =+ "${PN}-python"
FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RRECOMMENDS_PYTHON = "${@bb.utils.contains('PACKAGECONFIG', 'python3', '${PN}-python', '',d)}"
RRECOMMENDS_${PN}-python += "${RRECOMMENDS_PYTHON}"
