DESCRIPTION = "Trusted Services libpsats library for the arm-linux enviroment. \
               Libpsats carries psa client implementations to be used by linux user-space applications."

TS_ENV = "arm-linux"

require trusted-services.inc

OECMAKE_SOURCEPATH = "${S}/deployments/libpsats/${TS_ENV}"

DEPENDS += "libts"

do_install:append () {
    # Move the dynamic libraries into the standard place.
    install -d ${D}${libdir}
    mv ${D}${TS_INSTALL}/lib/libpsats* ${D}${libdir}

    # Update generated cmake file to use correct paths.
    target_cmake=$(find ${D}${TS_INSTALL}/lib/cmake/libpsats -type f -iname "libpsatsTargets-*.cmake")
    if [ ! -z "$target_cmake" ]; then
        sed -i -e "s#/${TS_ENV}##g" $target_cmake
    fi

    # Remove files installed by libts too. 
    rm ${D}${TS_INSTALL}/include/util.h
    rm ${D}${TS_INSTALL}/include/compiler.h
}

FILES:${PN} = "${libdir}/libpsats*.so.* ${nonarch_base_libdir}/udev/rules.d/"
FILES:${PN}-dev = "${TS_INSTALL}/lib/cmake ${TS_INSTALL}/include ${libdir}/libpsats*.so"
