FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# needed by u-boot Makefile if ENV_FILE is used
DEPENDS:append = " xxd-native"

UBOOT_INITIAL_ENV_BINARY = "1"
UBOOT_INITIAL_ENV_BINARY_SIZE = "0x10000"

SRC_URI:append = "\
   file://fvp.cfg \
   file://fvp.env \
   file://saveenv.cfg \
"

do_configure:append() {
  install -m 644 ${UNPACKDIR}/fvp.env ${B}/source/board/armltd/vexpress64/fvp.env
}

