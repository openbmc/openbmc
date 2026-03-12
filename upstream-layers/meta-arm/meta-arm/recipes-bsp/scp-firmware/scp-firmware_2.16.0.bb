SUMMARY = "SCP and MCP Firmware"
DESCRIPTION = "Firmware for SCP and MCP software reference implementation"
HOMEPAGE = "https://gitlab.arm.com/firmware/SCP-firmware"

LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://license.md;beginline=5;md5=9db9e3d2fb8d9300a6c3d15101b19731 \
                    file://contrib/cmsis/git/LICENSE.txt;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI_SCP_FIRMWARE ?= "gitsm://git.gitlab.arm.com/firmware/SCP-firmware.git;protocol=https"
SRC_URI = "${SRC_URI_SCP_FIRMWARE};branch=${SRCBRANCH} \
           file://0001-OPTEE-Private-Includes.patch \
          "

SRCBRANCH = "main"
SRCREV  = "190e938c2da3631b4834a90448516423099c79f7"

PROVIDES += "virtual/control-processor-firmware"

SCP_DEBUG           ?= "${FIRMWARE_DEBUG_BUILD}"
CMAKE_BUILD_TYPE    ?= "${@oe.utils.vartrue('SCP_DEBUG', 'Debug', 'RelWithDebInfo', d)}"
SCP_PLATFORM        ?= "${FIRMWARE_PLATFORM}"
SCP_PRODUCT_GROUP   ?= "."
SCP_LOG_LEVEL       ?= "WARN"
SCP_PLATFORM_FEATURE_SET ?= "0"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "gcc-arm-none-eabi-native \
           cmake-native \
           ninja-native \
          "

# For now we only build with GCC, so stop meta-clang trying to get involved
TOOLCHAIN = "gcc"

inherit firmware

B = "${WORKDIR}/build"

# Allow platform specific copying of only scp or both scp & mcp, default to both
FW_TARGETS ?= "scp mcp"
FW_INSTALL ?= "ramfw romfw"

COMPATIBLE_MACHINE ?= "invalid"

export CFLAGS = "${DEBUG_PREFIX_MAP}"
export ASMFLAGS = "${DEBUG_PREFIX_MAP}"

LDFLAGS[unexport] = "1"

EXTRA_OECMAKE = "-D CMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} \
                 -D SCP_LOG_LEVEL=${SCP_LOG_LEVEL} \
                 -D SCP_PLATFORM_FEATURE_SET=${SCP_PLATFORM_FEATURE_SET} \
                 -D DISABLE_CPPCHECK=1 \
                 -D SCP_TOOLCHAIN=GNU \
                "

do_configure() {
    for FW in ${FW_TARGETS}; do
        for TYPE in ${FW_INSTALL}; do
            bbnote Configuring ${SCP_PLATFORM}/${FW}_${TYPE}...
            cmake -GNinja ${EXTRA_OECMAKE} -S ${S} -B "${B}/${TYPE}/${FW}" -D SCP_FIRMWARE_SOURCE_DIR:PATH="${SCP_PRODUCT_GROUP}/${SCP_PLATFORM}/${FW}_${TYPE}"
        done
    done
}

do_configure[cleandirs] += "${B}"

do_compile() {
    for FW in ${FW_TARGETS}; do
        for TYPE in ${FW_INSTALL}; do
            bbnote Building ${SCP_PLATFORM}/${FW}_${TYPE}...
            VERBOSE=1 cmake --build ${B}/${TYPE}/${FW} --target all
        done
    done
}

do_install() {
    install -d ${D}${FIRMWARE_DIR}
    for TYPE in ${FW_INSTALL}; do
        for FW in ${FW_TARGETS}; do
           if [ "$TYPE" = "romfw" ]; then
               if [ "$FW" = "scp" ]; then
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-bl1.bin" "${D}${FIRMWARE_DIR}/${FW}_${TYPE}.bin"
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-bl1.elf" "${D}${FIRMWARE_DIR}/${FW}_${TYPE}.elf"
               else
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-${FW}-bl1.bin" "${D}${FIRMWARE_DIR}/${FW}_${TYPE}.bin"
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-${FW}-bl1.elf" "${D}${FIRMWARE_DIR}/${FW}_${TYPE}.elf"
               fi
           elif [ "$TYPE" = "ramfw" ]; then
               if [ "$FW" = "scp" ]; then
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-bl2.bin" "${D}${FIRMWARE_DIR}/${FW}_${TYPE}.bin"
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-bl2.elf" "${D}${FIRMWARE_DIR}/${FW}_${TYPE}.elf"
               else
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-${FW}-bl2.bin" "${D}${FIRMWARE_DIR}/${FW}_${TYPE}.bin"
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-${FW}-bl2.elf" "${D}${FIRMWARE_DIR}/${FW}_${TYPE}.elf"
               fi
           fi
       done
    done
}

# These binaries are specifically for 32-bit arm
INSANE_SKIP:${PN}-dbg += "arch"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
