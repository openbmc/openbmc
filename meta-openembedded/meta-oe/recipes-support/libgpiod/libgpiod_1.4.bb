require libgpiod.inc

DEPENDS += "autoconf-archive-native"

SRC_URI[md5sum] = "bd52d764017215a30e2f014d2081dc3e"
SRC_URI[sha256sum] = "ebde83aaf14be3abd33e7a90faa487a2ee231e242897afe7fdefb765386b3c8b"

# enable tools and cxx bindings
PACKAGECONFIG ?= "cxx tools"

PACKAGECONFIG[cxx] = "--enable-bindings-cxx,--disable-bindings-cxx"
PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev"

PACKAGECONFIG[python3] = "--enable-bindings-python,--disable-bindings-python,python3"
inherit ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3native', '', d)}

PACKAGES =+ "${PN}-python"
FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RRECOMMENDS_PYTHON = "${@bb.utils.contains('PACKAGECONFIG', 'python3', '${PN}-python', '',d)}"
RRECOMMENDS_${PN}-python += "${RRECOMMENDS_PYTHON}"
