require freeglut_${PV}.bb

PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles', '', d)}"

S = "${UNPACKDIR}/freeglut-${PV}"
