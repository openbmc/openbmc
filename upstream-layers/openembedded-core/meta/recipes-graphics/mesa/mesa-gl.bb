require mesa.inc

SUMMARY += " (OpenGL only, no EGL/GLES)"

PROVIDES = "virtual/libgl virtual/mesa"

S = "${UNPACKDIR}/mesa-${PV}"

TARGET_CFLAGS = "-I${STAGING_INCDIR}/drm"

# At least one DRI rendering engine is required to build mesa.
PACKAGECONFIG ??= "opengl gallium ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG:class-target = "opengl gallium ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"

