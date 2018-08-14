require mesa_${PV}.bb

SUMMARY += " (OpenGL only, no EGL/GLES)"

PROVIDES = "virtual/libgl virtual/mesa"

S = "${WORKDIR}/mesa-${PV}"

PACKAGECONFIG ??= "opengl dri ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
