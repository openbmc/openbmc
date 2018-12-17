require libgpiod.inc

DEPENDS += "autoconf-archive-native"

SRC_URI[md5sum] = "34a9972f2f4e9c32fa940301301b007d"
SRC_URI[sha256sum] = "b6b9079c933f7c8524815437937dda6b795a16141bca202a9eec70ba5844b5ba"

PACKAGECONFIG[cxx] = "--enable-bindings-cxx,--disable-bindings-cxx"

PACKAGECONFIG[python3] = "--enable-bindings-python,--disable-bindings-python,python3,python3-core"
inherit ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'python3native', '', d)}

PACKAGES =+ "${PN}-python"
FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
RRECOMMENDS_PYTHON = "${@bb.utils.contains('PACKAGECONFIG', 'python3', '${PN}-python', '',d)}"
RRECOMMENDS_${PN} += "${RRECOMMENDS_PYTHON}"
