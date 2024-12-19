FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# needed by u-boot Makefile if ENV_FILE is used
DEPENDS:append = " xxd-native"

SRC_URI:append = "\
   file://fvp.cfg \
   file://fvp.env \
"

do_configure:append() {
  install -m 644 ${UNPACKDIR}/fvp.env ${B}
}

