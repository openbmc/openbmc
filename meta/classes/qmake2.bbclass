#
# QMake variables for Qt4
#
inherit qmake_base

DEPENDS_prepend = "qt4-tools-native "

export QMAKESPEC = "${STAGING_DATADIR}/qt4/mkspecs/${TARGET_OS}-oe-g++"
export OE_QMAKE_QT_CONFIG = "${STAGING_DATADIR}/qt4/mkspecs/qconfig.pri"
export OE_QMAKE_UIC = "${STAGING_BINDIR_NATIVE}/uic4"
export OE_QMAKE_UIC3 = "${STAGING_BINDIR_NATIVE}/uic34"
export OE_QMAKE_MOC = "${STAGING_BINDIR_NATIVE}/moc4"
export OE_QMAKE_RCC = "${STAGING_BINDIR_NATIVE}/rcc4"
export OE_QMAKE_QDBUSCPP2XML = "${STAGING_BINDIR_NATIVE}/qdbuscpp2xml4"
export OE_QMAKE_QDBUSXML2CPP = "${STAGING_BINDIR_NATIVE}/qdbusxml2cpp4"
export OE_QMAKE_QMAKE = "${STAGING_BINDIR_NATIVE}/qmake2"
export OE_QMAKE_LINK = "${CXX}"
export OE_QMAKE_CXXFLAGS = "${CXXFLAGS}"
export OE_QMAKE_INCDIR_QT = "${STAGING_INCDIR}/qt4"
export OE_QMAKE_LIBDIR_QT = "${STAGING_LIBDIR}"
export OE_QMAKE_LIBS_QT = "qt"
export OE_QMAKE_LIBS_X11 = "-lXext -lX11 -lm"
export OE_QMAKE_LIBS_X11SM = "-lSM -lICE"
export OE_QMAKE_LCONVERT = "${STAGING_BINDIR_NATIVE}/lconvert4"
export OE_QMAKE_LRELEASE = "${STAGING_BINDIR_NATIVE}/lrelease4"
export OE_QMAKE_LUPDATE = "${STAGING_BINDIR_NATIVE}/lupdate4"
export OE_QMAKE_XMLPATTERNS = "${STAGING_BINDIR_NATIVE}/xmlpatterns4"
