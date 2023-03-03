require mesa.inc

SUMMARY += " (OpenGL only, no EGL/GLES)"

PROVIDES = "virtual/libgl virtual/mesa"

S = "${WORKDIR}/mesa-${PV}"

TARGET_CFLAGS = "-I${STAGING_INCDIR}/drm"

# At least one DRI rendering engine is required to build mesa.
# When no X11 is available, use osmesa for the rendering engine.
PACKAGECONFIG ??= "opengl gallium ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', 'osmesa', d)}"
PACKAGECONFIG:class-target = "opengl gallium ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', 'osmesa', d)}"

