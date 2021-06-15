PYNQ_NOTEBOOK_DIR ?= "${datadir}/notebooks"

PYNQ_ARCH_arm = "armv7l"
PYNQ_ARCH_aarch64 = "aarch64"

PYNQ_BUILD_ARCH="${PYNQ_ARCH_${TARGET_ARCH}}"
PYNQ_BUILD_ROOT="${STAGING_DIR_TARGET}"
BBCLASSEXTEND = "native nativesdk"

PACKAGES += " ${PN}-notebooks"
FILES_${PN}-notebooks += "${PYNQ_NOTEBOOK_DIR}"

INSANE_SKIP_${PN} += "staticdev dev-so"
inherit python3-dir

# Used for setup.py on PYNQ
BOARD_NAME_ultra96 = "Ultra96"
BOARD_NAME_zcu104-zynqmp = "ZCU104"

do_compile_prepend() {
  export BOARD=${BOARD_NAME}
  export PYNQ_BUILD_ARCH=${PYNQ_BUILD_ARCH}
  export PYNQ_JUPYTER_NOTEBOOKS=${D}${PYNQ_NOTEBOOK_DIR}
}

do_install_prepend() {
  export BOARD=${BOARD_NAME}
  export PYNQ_BUILD_ARCH=${PYNQ_BUILD_ARCH}
  export PYNQ_JUPYTER_NOTEBOOKS=${D}${PYNQ_NOTEBOOK_DIR}
  install -d ${PYNQ_JUPYTER_NOTEBOOKS}
}

do_configure_prepend() {
  export BOARD=${BOARD_NAME}
  export PYNQ_BUILD_ARCH=${PYNQ_BUILD_ARCH}
  export PYNQ_JUPYTER_NOTEBOOKS=${D}${PYNQ_NOTEBOOK_DIR}
  install -d ${PYNQ_JUPYTER_NOTEBOOKS}
}
