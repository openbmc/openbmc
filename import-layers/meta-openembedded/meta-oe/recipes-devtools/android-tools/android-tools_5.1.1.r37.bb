DESCRIPTION = "Different utilities from Android"
SECTION = "console/utils"
LICENSE = "Apache-2.0 & GPL-2.0 & BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
    file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6 \
    file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=8bef8e6712b1be5aa76af1ebde9d6378 \
    file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
"
DEPENDS = "libbsd libpcre openssl zlib libcap"

ANDROID_TAG = "android-5.1.1_r37"
ANDROID_MIRROR = "android.googlesource.com"
CORE_REPO = "${ANDROID_MIRROR}/platform/system/core"
EXTRAS_REPO = "${ANDROID_MIRROR}/platform/system/extras"
LIBHARDWARE_REPO = "${ANDROID_MIRROR}/platform/hardware/libhardware"
LIBSELINUX_REPO = "${ANDROID_MIRROR}/platform/external/libselinux"
BUILD_REPO = "${ANDROID_MIRROR}/platform/build"

SRC_URI = " \
    git://${CORE_REPO};name=core;protocol=https;nobranch=1;destsuffix=git/system/core;tag=${ANDROID_TAG} \
    git://${EXTRAS_REPO};name=extras;protocol=https;nobranch=1;destsuffix=git/system/extras;tag=${ANDROID_TAG} \
    git://${LIBHARDWARE_REPO};name=libhardware;protocol=https;nobranch=1;destsuffix=git/hardware/libhardware;tag=${ANDROID_TAG} \
    git://${LIBSELINUX_REPO};name=libselinux;protocol=https;nobranch=1;destsuffix=git/external/libselinux;tag=${ANDROID_TAG} \
    git://${BUILD_REPO};name=build;protocol=https;nobranch=1;destsuffix=git/build;tag=${ANDROID_TAG} \
    file://remove-selinux-android.patch \
    file://use-capability.patch \
    file://use-local-socket.patch \
    file://preserve-ownership.patch \
    file://mkbootimg-Add-dt-parameter-to-specify-DT-image.patch \
    file://remove-bionic-android.patch \
    file://define-shell-command.patch \
    file://implicit-declaration-function-strlcat-strlcopy.patch \
    file://fix-big-endian-build.patch \
    file://android-tools-adbd.service \
    file://.gitignore;subdir=git \
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

inherit systemd

SYSTEMD_SERVICE_${PN} = "android-tools-adbd.service"

# Get rid of files uneeded to build Android tools
do_unpack_extra() {
    cd ${S}
    rm -rf \
      system/core/.git \
      system/extras/.git \
      hardware/libhardware/.git \
      external/libselinux/.git \
      build/.git
    git init
    git add .
    git commit -m \
      "Initial import - committed ${ANDROID_TAG}"
    git clean -fdx
}

addtask unpack_extra after do_unpack before do_patch

# Find libbsd headers during native builds
CC_append_class-native = " -I${STAGING_INCDIR}"
CC_append_class-nativesdk = " -I${STAGING_INCDIR}"

TOOLS = "adb fastboot ext4_utils mkbootimg adbd"

# Adb needs sys/capability.h, which is not available for native*
TOOLS_class-native = "fastboot ext4_utils mkbootimg"
TOOLS_class-nativesdk = "fastboot ext4_utils mkbootimg"

do_compile() {
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
      mips|mipsel)
        export android_arch=linux-mips
      ;;
      powerpc|powerpc64)
        export android_arch=linux-ppc
      ;;
      i586|x86_64)
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
