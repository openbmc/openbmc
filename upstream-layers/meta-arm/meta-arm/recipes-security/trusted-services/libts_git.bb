DESCRIPTION = "Trusted Services libts library for the arm-linux enviroment. \
               Used for locating and accessing services from a Linux userspace client"

TS_ENV = "arm-linux"

require trusted-services.inc

# If optee-client is not included, take care of udev and related configuration.
require ${@bb.utils.contains('IMAGE_INSTALL', 'optee-client', '', 'libts-udev.inc', d)}

OECMAKE_SOURCEPATH = "${S}/deployments/libts/${TS_ENV}"

DEPENDS           += "arm-ffa-user"

do_install:append () {
    # Move the dynamic libraries into the standard place.
    install -d ${D}${libdir}
    mv ${D}${TS_INSTALL}/lib/libts* ${D}${libdir}

    # Update generated cmake file to use correct paths.
    target_cmake=$(find ${D}${TS_INSTALL}/lib/cmake/libts -type f -iname "libtsTargets-*.cmake")
    if [ ! -z "$target_cmake" ]; then
        sed -i -e "s#/${TS_ENV}##g" $target_cmake
    fi
}

FILES:${PN} += " ${libdir}/libts*.so.*"
FILES:${PN}-dev += " ${TS_INSTALL}/lib/cmake ${TS_INSTALL}/include ${libdir}/libts*.so"
