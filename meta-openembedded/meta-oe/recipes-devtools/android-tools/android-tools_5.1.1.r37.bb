DESCRIPTION = "Different utilities from Android"
SECTION = "console/utils"
LICENSE = "Apache-2.0 & GPL-2.0 & BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
    file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6 \
    file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=cb641bc04cda31daea161b1bc15da69f \
    file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
"
DEPENDS = "libbsd libpcre zlib libcap"
DEPENDS_append_class-target = " openssl"

ANDROID_MIRROR = "android.googlesource.com"

# matches with android-5.1.1_r37
SRCREV_core = "2314b110bdebdbfd2d94c502282f9e57c849897e"
SRCREV_extras = "3ecbe8d841df96127d7855661293e5ab6ba6c205"
SRCREV_libhardware = "be55eb1f4d840c82ffaf7c47460df17ff5bc4d9b"
SRCREV_libselinux = "07e9e1339ad1ba608acfba9dce2d0f474b252feb"
SRCREV_build = "16e987def3d7d8f7d30805eb95cef69e52a87dbc"

SRCREV_FORMAT = "core_extras_libhardware_libselinux_build"
SRC_URI = " \
    git://${ANDROID_MIRROR}/platform/system/core;name=core;protocol=https;nobranch=1;destsuffix=git/system/core \
    git://${ANDROID_MIRROR}/platform/system/extras;name=extras;protocol=https;nobranch=1;destsuffix=git/system/extras \
    git://${ANDROID_MIRROR}/platform/hardware/libhardware;name=libhardware;protocol=https;nobranch=1;destsuffix=git/hardware/libhardware \
    git://${ANDROID_MIRROR}/platform/external/libselinux;name=libselinux;protocol=https;nobranch=1;destsuffix=git/external/libselinux \
    git://${ANDROID_MIRROR}/platform/build;name=build;protocol=https;nobranch=1;destsuffix=git/build \
    file://core/0001-adb-remove-selinux-extensions.patch;patchdir=system/core \
    file://core/0002-adb-Use-local-sockets-where-appropriate.patch;patchdir=system/core \
    file://core/0003-adb-define-shell-command.patch;patchdir=system/core \
    file://core/0004-adb-Fix-build-on-big-endian-systems.patch;patchdir=system/core \
    file://core/0005-adb-add-base64-implementation.patch;patchdir=system/core \
    file://core/0006-adb-Musl-fixes.patch;patchdir=system/core \
    file://core/0007-adb-usb_linux.c-fix-build-with-glibc-2.28.patch;patchdir=system/core \
    file://core/0008-adb-Allow-adbd-to-be-ran-as-root.patch;patchdir=system/core \
    file://core/0009-mkbootimg-Add-dt-parameter-to-specify-DT-image.patch;patchdir=system/core \
    file://core/0010-Use-linux-capability.h-on-linux-systems-too.patch;patchdir=system/core \
    file://core/0011-Remove-bionic-specific-calls.patch;patchdir=system/core \
    file://core/0012-Fix-implicit-declaration-of-stlcat-strlcopy-function.patch;patchdir=system/core \
    file://core/adb_libssl_11.diff;patchdir=system/core \
    file://core/0013-adb-Support-riscv64.patch;patchdir=system/core \
    file://extras/0001-ext4_utils-remove-selinux-extensions.patch;patchdir=system/extras \
    file://extras/0002-ext4_utils-add-o-argument-to-preserve-ownership.patch;patchdir=system/extras \
    file://libselinux/0001-Remove-bionic-specific-calls.patch;patchdir=external/libselinux \
    file://libselinux/0001-libselinux-Do-not-define-gettid-if-glibc-2.30-is-use.patch;patchdir=external/libselinux \
    file://android-tools-adbd.service \
    file://build/0001-Riscv-Add-risc-v-Android-config-header.patch;patchdir=build \
    file://gitignore \
    file://adb.mk;subdir=${BPN} \
    file://adbd.mk;subdir=${BPN} \
    file://ext4_utils.mk;subdir=${BPN} \
    file://fastboot.mk;subdir=${BPN} \
    file://mkbootimg.mk;subdir=${BPN} \
"


S = "${WORKDIR}/git"
B = "${WORKDIR}/${BPN}"

# http://errors.yoctoproject.org/Errors/Details/133881/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

COMPATIBLE_HOST_powerpc = "(null)"
COMPATIBLE_HOST_powerpc64 = "(null)"
COMPATIBLE_HOST_powerpc64le = "(null)"

inherit systemd

SYSTEMD_SERVICE_${PN} = "android-tools-adbd.service"

# Find libbsd headers during native builds
CC_append_class-native = " -I${STAGING_INCDIR}"
CC_append_class-nativesdk = " -I${STAGING_INCDIR}"

TOOLS = "adb fastboot ext4_utils mkbootimg adbd"

# Adb needs sys/capability.h, which is not available for native*
TOOLS_class-native = "fastboot ext4_utils mkbootimg"
TOOLS_class-nativesdk = "fastboot ext4_utils mkbootimg"

do_compile() {
    cp ${WORKDIR}/gitignore ${S}/.gitignore

    # Setting both variables below causing our makefiles to not work with
    # implicit make rules
    unset CFLAGS
    unset CPPFLAGS

    export SRCDIR=${S}

    case "${HOST_ARCH}" in
      arm)
        export android_arch=linux-arm
      ;;
      aarch64)
        export android_arch=linux-arm64
      ;;
      riscv64)
        export android_arch=linux-riscv64
      ;;
      mips|mipsel)
        export android_arch=linux-mips
      ;;
      mips64|mips64el)
        export android_arch=linux-mips64
      ;;
      powerpc|powerpc64)
        export android_arch=linux-ppc
      ;;
      i586|i686|x86_64)
        export android_arch=linux-x86
      ;;
    esac

    for tool in ${TOOLS}; do
      mkdir -p ${B}/${tool}
      oe_runmake -f ${B}/${tool}.mk -C ${B}/${tool}
    done
}

do_install() {
    if echo ${TOOLS} | grep -q "ext4_utils" ; then
        install -D -p -m0755 ${S}/system/core/libsparse/simg_dump.py ${D}${bindir}/simg_dump
        install -D -p -m0755 ${S}/system/extras/ext4_utils/mkuserimg.sh ${D}${bindir}/mkuserimg

        install -m0755 ${B}/ext4_utils/ext2simg ${D}${bindir}
        install -m0755 ${B}/ext4_utils/ext4fixup ${D}${bindir}
        install -m0755 ${B}/ext4_utils/img2simg ${D}${bindir}
        install -m0755 ${B}/ext4_utils/make_ext4fs ${D}${bindir}
        install -m0755 ${B}/ext4_utils/simg2img ${D}${bindir}
        install -m0755 ${B}/ext4_utils/simg2simg ${D}${bindir}
    fi

    if echo ${TOOLS} | grep -q "adb " ; then
        install -d ${D}${bindir}
        install -m0755 ${B}/adb/adb ${D}${bindir}
    fi

    if echo ${TOOLS} | grep -q "adbd" ; then
        install -d ${D}${bindir}
        install -m0755 ${B}/adbd/adbd ${D}${bindir}
    fi

    # Outside the if statement to avoid errors during do_package
    install -D -p -m0644 ${WORKDIR}/android-tools-adbd.service \
      ${D}${systemd_unitdir}/system/android-tools-adbd.service

    if echo ${TOOLS} | grep -q "fastboot" ; then
        install -d ${D}${bindir}
        install -m0755 ${B}/fastboot/fastboot ${D}${bindir}
    fi

    if echo ${TOOLS} | grep -q "mkbootimg" ; then
        install -d ${D}${bindir}
        install -m0755 ${B}/mkbootimg/mkbootimg ${D}${bindir}
    fi
}

PACKAGES += "${PN}-fstools"

RDEPENDS_${BPN} = "${BPN}-conf bash"

FILES_${PN}-fstools = "\
    ${bindir}/ext2simg \
    ${bindir}/ext4fixup \
    ${bindir}/img2simg \
    ${bindir}/make_ext4fs \
    ${bindir}/simg2img \
    ${bindir}/simg2simg \
    ${bindir}/simg_dump \
    ${bindir}/mkuserimg \
"

BBCLASSEXTEND = "native"
