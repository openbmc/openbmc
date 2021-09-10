# OpenGL comes from libmali on ev/eg, when egl is enabled
DEPENDS_MALI_XLNX = "${@bb.utils.contains('PACKAGECONFIG', 'egl', 'libmali-xlnx', '', d)}"
PKG_ARCH_XLNX = "${@bb.utils.contains('PACKAGECONFIG', 'egl', '${SOC_VARIANT_ARCH}', '${TUNE_PKGARCH}', d)}"

DEPENDS_append_zynqmpev = " ${DEPENDS_MALI_XLNX}"
DEPENDS_append_zynqmpeg = " ${DEPENDS_MALI_XLNX}"

PACKAGE_ARCH_zynqmpev = "${PKG_ARCH_XLNX}"
PACKAGE_ARCH_zynqmpeg = "${PKG_ARCH_XLNX}"
