SUMMARY = "Firmware image deploying multi-config firmware"
DESCRIPTION = "Image for deploying a firmware set on platforms using multi-config"
LICENSE = "MIT"

inherit deploy nopackages

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE ?= "invalid"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

# Users of this recipe are expected to provide the list of firmware images
# that need to be deployed by setting this variable.
FIRMWARE_BINARIES ?= ""

do_deploy() {
    firmware_loc=$(echo "${TMPDIR}" | sed "s/${TCLIBC}/musl/")
    firmware_loc="${firmware_loc}_${MACHINE}/deploy/images/${MACHINE}"
    for firmware in ${FIRMWARE_BINARIES}; do
        echo "cp -av ${firmware_loc}/${firmware} ${DEPLOYDIR}/"
        cp -av "${firmware_loc}/${firmware}" ${DEPLOYDIR}/
        if [ -L "${firmware_loc}/${firmware}" ]; then
            echo "cp -av ${firmware_loc}/$(readlink ${firmware_loc}/${firmware}) ${DEPLOYDIR}/"
            cp -av "${firmware_loc}/$(readlink ${firmware_loc}/${firmware})" ${DEPLOYDIR}/
        fi
    done
}

do_deploy[umask] = "022"

addtask deploy after do_prepare_recipe_sysroot
