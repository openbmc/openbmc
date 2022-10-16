DESCRIPTION = "Trusted Services libts library for the arm-linux enviroment. \
               Used for locating and accessing services from a Linux userspace client"

TS_ENV = "arm-linux"

require trusted-services.inc

SRC_URI += "file://tee-udev.rules \
           "

OECMAKE_SOURCEPATH="${S}/deployments/libts/${TS_ENV}"

DEPENDS           += "arm-ffa-tee arm-ffa-user"
RRECOMMENDS:${PN} += "arm-ffa-tee"

# Unix group name for dev/tee* ownership.
TEE_GROUP_NAME ?= "teeclnt"

do_install:append () {
    if ${@oe.utils.conditional('VIRTUAL-RUNTIME_dev_manager', 'busybox-mdev', 'false', 'true', d)}; then
        install -d ${D}${nonarch_base_libdir}/udev/rules.d/
        install -m 755 ${WORKDIR}/tee-udev.rules ${D}${nonarch_base_libdir}/udev/rules.d/
        sed -i -e "s/teeclnt/${TEE_GROUP_NAME}/" ${D}${nonarch_base_libdir}/udev/rules.d/tee-udev.rules
    fi

    # Move the dynamic libraries into the standard place.
    # Update a cmake file to use correct paths.
    install -d ${D}${libdir}
    mv ${D}${TS_INSTALL}/lib/libts* ${D}${libdir}

    sed -i -e "s#/${TS_ENV}##g" ${D}${TS_INSTALL}/lib/cmake/libts/libtsTargets-noconfig.cmake
}

inherit ${@oe.utils.conditional('VIRTUAL-RUNTIME_dev_manager', 'busybox-mdev', '', 'useradd', d)}
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${TEE_GROUP_NAME}"

FILES:${PN} = "${libdir}/libts.so.* ${nonarch_base_libdir}/udev/rules.d/"
FILES:${PN}-dev = "${TS_INSTALL}/lib/cmake ${TS_INSTALL}/include ${libdir}/libts.so"
