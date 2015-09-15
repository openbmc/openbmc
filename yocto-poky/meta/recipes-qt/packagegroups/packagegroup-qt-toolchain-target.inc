LICENSE = "MIT"

# Qt4 could NOT be built on MIPS64 with 64 bits userspace
COMPATIBLE_HOST_mips64 = "null"

inherit packagegroup

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

RDEPENDS_${PN} += " \
        packagegroup-core-standalone-sdk-target \
        qt4${QTLIBPREFIX}-mkspecs \
        libqt${QTLIBPREFIX}multimedia4-dev \
        libqt${QTLIBPREFIX}3support4-dev \
        libqt${QTLIBPREFIX}clucene4-dev \
        libqt${QTLIBPREFIX}core4-dev \
        libqt${QTLIBPREFIX}dbus4-dev \
        libqt${QTLIBPREFIX}designercomponents4-dev \
        libqt${QTLIBPREFIX}designer4-dev \
        libqt${QTLIBPREFIX}uitools4-dev \
        libqt${QTLIBPREFIX}gui4-dev \
        libqt${QTLIBPREFIX}help4-dev \
        libqt${QTLIBPREFIX}network4-dev \
        libqt${QTLIBPREFIX}script4-dev \
        libqt${QTLIBPREFIX}scripttools4-dev \
        libqt${QTLIBPREFIX}sql4-dev \
        libqt${QTLIBPREFIX}svg4-dev \
        libqt${QTLIBPREFIX}test4-dev \
        libqt${QTLIBPREFIX}webkit4-dev \
        libqt${QTLIBPREFIX}xml4-dev \
        libqt${QTLIBPREFIX}declarative4-dev \
        libqt${QTLIBPREFIX}xmlpatterns4-dev \
        libsqlite3-dev \
        expat-dev \
        "

RRECOMMENDS_${PN} += " \
        libqt${QTLIBPREFIX}phonon4-dev \
        "
