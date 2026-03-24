# Sour out /dev/tee* device creation and access control
SRC_URI += "file://tee-udev.rules \
           "

# Unix group name for dev/tee* ownership.
TEE_GROUP_NAME ?= "tee"

do_install:append () {
    if ${@oe.utils.conditional('VIRTUAL-RUNTIME_dev_manager', 'busybox-mdev', 'false', 'true', d)}; then
        install -d ${D}${nonarch_base_libdir}/udev/rules.d/
        install -m 755 ${UNPACKDIR}/tee-udev.rules ${D}${nonarch_base_libdir}/udev/rules.d/
        sed -i -e "s/teeclnt/${TEE_GROUP_NAME}/" ${D}${nonarch_base_libdir}/udev/rules.d/tee-udev.rules
    fi
}


inherit ${@oe.utils.conditional('VIRTUAL-RUNTIME_dev_manager', 'busybox-mdev', '', 'useradd', d)}
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${TEE_GROUP_NAME}"

FILES:${PN} += " ${nonarch_base_libdir}/udev/rules.d/"
