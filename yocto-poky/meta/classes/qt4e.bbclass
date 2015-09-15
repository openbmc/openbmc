QT4EDEPENDS ?= "qt4-embedded "
DEPENDS_prepend = "${QT4EDEPENDS}"

inherit qmake2

QT_BASE_NAME = "qt4-embedded"
QT_DIR_NAME = "qtopia"
QT_LIBINFIX = "E"
# override variables set by qmake-base to compile Qt/Embedded apps
#
export QMAKESPEC = "${STAGING_DATADIR}/${QT_DIR_NAME}/mkspecs/${TARGET_OS}-oe-g++"
export OE_QMAKE_QT_CONFIG = "${STAGING_DATADIR}/${QT_DIR_NAME}/mkspecs/qconfig.pri"
export OE_QMAKE_INCDIR_QT = "${STAGING_INCDIR}/${QT_DIR_NAME}"
export OE_QMAKE_LIBDIR_QT = "${STAGING_LIBDIR}"
export OE_QMAKE_LIBS_QT = "qt"
export OE_QMAKE_LIBS_X11 = ""
export OE_QMAKE_EXTRA_MODULES = "network"
EXTRA_QMAKEVARS_PRE += " QT_LIBINFIX=${QT_LIBINFIX} "

# Qt4 uses atomic instructions not supported in thumb mode
ARM_INSTRUCTION_SET = "arm"
