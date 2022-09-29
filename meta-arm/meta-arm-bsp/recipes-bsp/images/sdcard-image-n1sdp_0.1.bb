SUMMARY = "Firmware image recipe for generating SD-Card artifacts."

inherit deploy nopackages

DEPENDS = "trusted-firmware-a \
           virtual/control-processor-firmware \
           n1sdp-board-firmware"

LICENSE = "MIT"
PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "n1sdp"
RM_WORK_EXCLUDE += "${PN}"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

FIRMWARE_DIR = "n1sdp-board-firmware_source"
PRIMARY_DIR = "${WORKDIR}/n1sdp-board-firmware_primary"
SECONDARY_DIR = "${WORKDIR}/n1sdp-board-firmware_secondary"

SOC_BINARIES = "mcp_fw.bin scp_fw.bin mcp_rom.bin scp_rom.bin"

prepare_package() {
    cd ${WORKDIR}

    # Master/Primary
    cp -av ${RECIPE_SYSROOT}/${FIRMWARE_DIR}/* ${PRIMARY_DIR}
    mkdir -p ${PRIMARY_DIR}/SOFTWARE/

    # Copy FIP binary
    cp -v ${RECIPE_SYSROOT}/firmware/fip.bin ${PRIMARY_DIR}/SOFTWARE/

    # Copy SOC binaries
    for f in ${SOC_BINARIES}; do
        cp -v ${RECIPE_SYSROOT}/firmware/${f} ${PRIMARY_DIR}/SOFTWARE/
    done

    sed -i -e 's|^C2C_ENABLE.*|C2C_ENABLE: TRUE            ;C2C enable TRUE/FALSE|' \
        ${PRIMARY_DIR}/MB/HBI0316A/io_v123f.txt
    sed -i -e 's|^C2C_SIDE.*|C2C_SIDE: MASTER            ;C2C side SLAVE/MASTER|' \
        ${PRIMARY_DIR}/MB/HBI0316A/io_v123f.txt
    sed -i -e 's|.*SOCCON: 0x1170.*PLATFORM_CTRL.*|SOCCON: 0x1170 0x00000100   ;SoC SCC PLATFORM_CTRL|' \
        ${PRIMARY_DIR}/MB/HBI0316A/io_v123f.txt

    # Update load address for trusted boot
    sed -i -e '/^IMAGE4ADDRESS:/ s|0x60200000|0x64200000|' ${PRIMARY_DIR}/MB/HBI0316A/images.txt
    sed -i -e '/^IMAGE4UPDATE:/ s|FORCE   |SCP_AUTO|' ${PRIMARY_DIR}/MB/HBI0316A/images.txt
    sed -i -e '/^IMAGE4FILE: \\SOFTWARE\\/s|uefi.bin|fip.bin |' ${PRIMARY_DIR}/MB/HBI0316A/images.txt

    # Slave/Secondary
    cp -av ${RECIPE_SYSROOT}/${FIRMWARE_DIR}/* ${SECONDARY_DIR}
    mkdir -p ${SECONDARY_DIR}/SOFTWARE/

    # Copy SOC binaries
    for f in ${SOC_BINARIES}; do
        cp -v ${RECIPE_SYSROOT}/firmware/${f} ${SECONDARY_DIR}/SOFTWARE/
    done

    sed -i -e 's|^C2C_ENABLE.*|C2C_ENABLE: TRUE            ;C2C enable TRUE/FALSE|' \
        ${SECONDARY_DIR}/MB/HBI0316A/io_v123f.txt
    sed -i -e 's|^C2C_SIDE.*|C2C_SIDE: SLAVE             ;C2C side SLAVE/MASTER|' \
        ${SECONDARY_DIR}/MB/HBI0316A/io_v123f.txt
    sed -i -e 's|.*SOCCON: 0x1170.*PLATFORM_CTRL.*|SOCCON: 0x1170 0x00000101   ;SoC SCC PLATFORM_CTRL|' \
        ${SECONDARY_DIR}/MB/HBI0316A/io_v123f.txt
    sed -i -e '/^TOTALIMAGES:/ s|5|4|' ${SECONDARY_DIR}/MB/HBI0316A/images.txt
    sed -i -e 's|^IMAGE4|;&|' ${SECONDARY_DIR}/MB/HBI0316A/images.txt
}

do_deploy() {
    # prepare Master & Slave packages
    prepare_package

    for dir in ${PRIMARY_DIR} ${SECONDARY_DIR}; do
        dir_name=$(basename ${dir})
        mkdir -p ${D}/${dir_name}
        cp -av ${dir} ${D}

        # Compress the files
        tar -C ${D}/${dir_name} -zcvf ${DEPLOYDIR}/${dir_name}.tar.gz ./
    done
}
do_deploy[dirs] += "${PRIMARY_DIR} ${SECONDARY_DIR}"
do_deploy[cleandirs] += "${PRIMARY_DIR} ${SECONDARY_DIR}"
do_deploy[umask] = "022"
addtask deploy after do_prepare_recipe_sysroot
