DESCRIPTION = "ARM Trusted Firmware"

LICENSE = "BSD"
LIC_FILES_CHKSUM ?= "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

PV="1.3.0+git${SRCPV}"

inherit deploy

S = "${WORKDIR}/git"

SRC_URI = " \
    git://github.com/Nuvoton-Israel/arm-trusted-firmware.git;branch=nuvoton;protocol=https \
"

inherit image-artifact-names

MACHINE_SOC ?= "npcm8xx"
PLATFORM = "npcm845x"

# requires CROSS_COMPILE set by hand as there is no configure script
# Some versions of u-boot use .bin and others use .img.  By default use .bin
# but enable individual recipes to change this value.
ATF_SUFFIX ?= "bin"
ATF_IMAGE ?= "bl31-${MACHINE_SOC}-${PV}-${PR}.${ATF_SUFFIX}"
ATF_SYMLINK ?= "bl31-${MACHINE_SOC}.${ATF_SUFFIX}"

export CROSS_COMPILE="${TARGET_PREFIX}"

# Let the Makefile handle setting up the CFLAGS and LDFLAGS as it is a standalone application
CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

do_configure() {
	oe_runmake clean -C ${S} PLAT=${PLATFORM}
}

do_compile() {
	oe_runmake -C ${S} PLAT=${PLATFORM} SPD=opteed all
}

# do_install() nothing
do_install[noexec] = "1"

do_deploy() {
    # Create deploy folder
    install -d ${DEPLOYDIR}

    # Copy IPL to deploy folder
    install -m 0644 ${S}/build/${PLATFORM}/release/bl31.bin ${DEPLOYDIR}/bl31.bin
    cd ${DEPLOYDIR}
    ln -sf bl31.bin ${ATF_SYMLINK}
    ln -sf bl31.bin ${ATF_IMAGE}
}

addtask deploy before do_build after do_compile
