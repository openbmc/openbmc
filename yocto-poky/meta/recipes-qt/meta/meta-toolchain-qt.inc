TOOLCHAIN_HOST_TASK = "nativesdk-packagegroup-${QTNAME}-toolchain-host packagegroup-cross-canadian-${MACHINE}"
TOOLCHAIN_TARGET_TASK = "packagegroup-${QTNAME}-toolchain-target"
TOOLCHAIN_OUTPUTNAME = "${SDK_NAME}-toolchain-${QTNAME}-${DISTRO_VERSION}"

require recipes-core/meta/meta-toolchain.bb

QT_TOOLS_PREFIX = "$OECORE_NATIVE_SYSROOT${bindir_nativesdk}"

create_sdk_files_append() {
    mkdir -p ${SDK_OUTPUT}${SDKPATHNATIVE}/environment-setup.d/
    script=${SDK_OUTPUT}${SDKPATHNATIVE}/environment-setup.d/${QT_DIR_NAME}.sh

    echo 'export OE_QMAKE_CFLAGS="$CFLAGS"' > $script
    echo 'export OE_QMAKE_CXXFLAGS="$CXXFLAGS"' >> $script
    echo 'export OE_QMAKE_LDFLAGS="$LDFLAGS"' >> $script
    echo 'export OE_QMAKE_CC=$CC' >> $script
    echo 'export OE_QMAKE_CXX=$CXX' >> $script
    echo 'export OE_QMAKE_LINK=$CXX' >> $script
    echo 'export OE_QMAKE_AR=$AR' >> $script
    echo 'export OE_QMAKE_LIBDIR_QT=$OECORE_TARGET_SYSROOT${libdir}' >> $script
    echo 'export OE_QMAKE_INCDIR_QT=$OECORE_TARGET_SYSROOT${includedir}/${QT_DIR_NAME}' >> $script
    echo 'export OE_QMAKE_MOC=${QT_TOOLS_PREFIX}/moc4' >> $script
    echo 'export OE_QMAKE_UIC=${QT_TOOLS_PREFIX}/uic4' >> $script
    echo 'export OE_QMAKE_UIC3=${QT_TOOLS_PREFIX}/uic34' >> $script
    echo 'export OE_QMAKE_RCC=${QT_TOOLS_PREFIX}/rcc4' >> $script
    echo 'export OE_QMAKE_QDBUSCPP2XML=${QT_TOOLS_PREFIX}/qdbuscpp2xml4' >> $script
    echo 'export OE_QMAKE_QDBUSXML2CPP=${QT_TOOLS_PREFIX}/qdbusxml2cpp4' >> $script
    echo 'export OE_QMAKE_QT_CONFIG=$OECORE_TARGET_SYSROOT${datadir}/${QT_DIR_NAME}/mkspecs/qconfig.pri' >> $script
    echo 'export QMAKESPEC=$OECORE_TARGET_SYSROOT${datadir}/${QT_DIR_NAME}/mkspecs/linux-g++' >> $script
    echo 'export QT_CONF_PATH=$OECORE_NATIVE_SYSROOT${sysconfdir}/qt.conf' >> $script

    # make a symbolic link to mkspecs for compatibility with Qt SDK
    # and Qt Creator
    (cd ${SDK_OUTPUT}/${SDKPATHNATIVE}${bindir_nativesdk}/..; ln -s ${SDKTARGETSYSROOT}/usr/share/${QT_DIR_NAME}/mkspecs mkspecs;)
}
