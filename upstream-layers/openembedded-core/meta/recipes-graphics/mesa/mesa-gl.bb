require mesa.inc

SUMMARY += " (OpenGL only, no EGL/GLES)"

PROVIDES = "virtual/libgl virtual/mesa"

TARGET_CFLAGS = "-I${STAGING_INCDIR}/drm"

# At least one DRI rendering engine is required to build mesa.
PACKAGECONFIG = "expat opengl gallium ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} xmlconfig zlib"

PACKAGECONFIG:append:x86 = " libclc gallium-llvm intel amd nouveau svga"
PACKAGECONFIG:append:x86-64 = " libclc gallium-llvm intel amd nouveau svga"
PACKAGECONFIG:append:i686 = " libclc gallium-llvm intel amd nouveau svga"
