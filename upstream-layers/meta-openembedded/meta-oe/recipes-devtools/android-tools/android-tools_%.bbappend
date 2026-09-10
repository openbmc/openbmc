FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# e2fsprogs expecting headers in sparse/ subdirectory
do_install:append:class-native() {
    # to resolve the "Multiple shlib providers" conflict.
    rm -f ${D}${libdir}/android/libsparse.so*
    rm -f ${D}${libdir}/android/libbase.so*
    rm -f ${D}${libdir}/android/liblog.so*

    if [ -d "${S}/system/core/libsparse/include/sparse" ]; then
        install -d ${D}${includedir}/sparse
        cp -r ${S}/system/core/libsparse/include/sparse/* ${D}${includedir}/sparse/
    else
        bbfatal "Sparse headers not found in ${S}/system/core/libsparse/include/sparse"
    fi

    install -d ${D}${libdir}
    for lib in libsparse libbase liblog; do
        if [ -f "${S}/debian/out/system/${lib}.so" ]; then
            bbnote "Installing ${lib} from ${S}/debian/out/system/${lib}.so"
            install -m 0755 ${S}/debian/out/system/${lib}.so ${D}${libdir}/${lib}.so.0
            ln -sf ${lib}.so.0 ${D}${libdir}/${lib}.so
        elif [ -f "${S}/debian/out/system/${lib}.so.0" ]; then
            bbnote "Installing ${lib} from ${S}/debian/out/system/${lib}.so.0"
            install -m 0755 ${S}/debian/out/system/${lib}.so.0 ${D}${libdir}/${lib}.so.0
            ln -sf ${lib}.so.0 ${D}${libdir}/${lib}.so
        else
            bberror "${lib} not found in ${S}/debian/out/system"
        fi
    done

    install -d ${D}${libdir}/android
    for lib in libsparse libbase liblog; do
        if [ -f "${D}${libdir}/${lib}.so.0" ]; then
            ln -sf ../${lib}.so.0 ${D}${libdir}/android/${lib}.so.0
            ln -sf ../${lib}.so ${D}${libdir}/android/${lib}.so
        fi
    done
}

FILES:${PN}-dev += " \
    ${includedir}/sparse \
    ${libdir}/lib*.so \
    ${libdir}/android/lib*.so \
"

FILES:${PN} += " \
    ${libdir}/lib*.so.* \
    ${libdir}/android/lib*.so.* \
"

SYSROOT_DIRS:append:class-native = " ${includedir} ${libdir}"

DEPENDS:append:class-native = " googletest-native"
