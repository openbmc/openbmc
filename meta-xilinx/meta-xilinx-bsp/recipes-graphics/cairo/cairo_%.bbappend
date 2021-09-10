PACKAGECONFIG_zynqmp += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 xcb', '', d)} \
                         egl glesv2"

# OpenGL comes from libmali on ev/eg
DEPENDS_append_zynqmpev = " libmali-xlnx"
DEPENDS_append_zynqmpeg = " libmali-xlnx"

PACKAGE_ARCH_zynqmpev = "${SOC_VARIANT_ARCH}"
PACKAGE_ARCH_zynqmpeg = "${SOC_VARIANT_ARCH}"
