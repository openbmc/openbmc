DESCRIPTION = "Various utilities from Android"
SECTION = "console/utils"
LICENSE = "Apache-2.0 & GPL-2.0-only & BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
    file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6 \
    file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=cb641bc04cda31daea161b1bc15da69f \
    file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
"
DEPENDS = "libbsd libpcre zlib libcap libusb squashfs-tools p7zip libselinux googletest"

SRCREV_core = "abfd66fafcbb691d7860df059f1df1c9b1ef29da"

SRC_URI = " \
    git://salsa.debian.org/android-tools-team/android-platform-tools;name=core;protocol=https;branch=master \
"

# Patches copied from android-platform-tools/debian/patches
# and applied in the order defined by the file debian/patches/series
SRC_URI += " \
    file://debian/external/libunwind/user_pt_regs.patch \
    file://debian/external/libunwind/legacy_built-in_sync_functions.patch \
    file://debian/external/libunwind/20150704-CVE-2015-3239_dwarf_i.h.patch \
    \
    file://debian/system/core/move-log-file-to-proper-dir.patch \
    file://debian/system/core/Added-missing-headers.patch \
    file://debian/system/core/libusb-header-path.patch \
    file://debian/system/core/stdatomic.patch \
    file://debian/system/core/Nonnull.patch \
    file://debian/system/core/Vector-cast.patch \
    file://debian/system/core/throw-exception-on-unknown-os.patch \
    file://debian/system/core/simg_dump-python3.patch \
    file://debian/system/core/fix-attribute-issue-with-gcc.patch \
    file://debian/system/core/fix-gettid-exception-declaration.patch \
    file://debian/system/core/fix-build-on-non-x86.patch \
    file://debian/system/core/add-missing-headers.patch \
    file://debian/system/core/hard-code-build-number.patch \
    file://debian/system/core/stub-out-fastdeploy.patch \
    file://debian/system/core/fix-standard-namespace-errors.patch \
    file://debian/system/core/Add-riscv64-support.patch \
    \
"

# patches which don't come from debian
SRC_URI += " \
    file://rules_yocto.mk;subdir=git \
    file://android-tools-adbd.service \
    file://adbd.mk;subdir=git/debian/system/core \
    file://remount \
    file://0001-Fixes-for-yocto-build.patch \
    file://0002-android-tools-modifications-to-make-it-build-in-yoct.patch \
    file://0003-Update-usage-of-usbdevfs_urb-to-match-new-kernel-UAP.patch \
    file://0004-adb-Fix-build-on-big-endian-systems.patch \
    file://0005-adb-Allow-adbd-to-be-run-as-root.patch \
    file://0001-liblp-fix-building-with-GCC-14.patch \
"

S = "${WORKDIR}/git"
B = "${WORKDIR}/${BPN}"

# http://errors.yoctoproject.org/Errors/Details/1debian881/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"

COMPATIBLE_HOST:powerpc = "(null)"
COMPATIBLE_HOST:powerpc64 = "(null)"
COMPATIBLE_HOST:powerpc64le = "(null)"

inherit systemd

SYSTEMD_PACKAGES = "${PN}-adbd"
SYSTEMD_SERVICE:${PN}-adbd = "android-tools-adbd.service"

# Find libbsd headers during native builds
CC:append:class-native = " -I${STAGING_INCDIR}"
CC:append:class-nativesdk = " -I${STAGING_INCDIR}"

PREREQUISITE_core = "liblog libbase libsparse liblog libcutils"
TOOLS_TO_BUILD = "libcrypto_utils libadb libziparchive fastboot adb img2simg simg2img libbacktrace"
TOOLS_TO_BUILD:append:class-target = " adbd"

do_compile() {

    case "${HOST_ARCH}" in
      arm)
        export android_arch=linux-arm
        cpu=arm
        deb_host_arch=arm
      ;;
      aarch64)
        export android_arch=linux-arm64
        cpu=arm64
        deb_host_arch=arm64
      ;;
      riscv64)
        export android_arch=linux-riscv64
      ;;
      mips|mipsel)
        export android_arch=linux-mips
        cpu=mips
        deb_host_arch=mips
      ;;
      mips64|mips64el)
        export android_arch=linux-mips64
        cpu=mips64
        deb_host_arch=mips64
      ;;
      powerpc|powerpc64)
        export android_arch=linux-ppc
      ;;
      i586|i686|x86_64)
        export android_arch=linux-x86
        cpu=x86_64
        deb_host_arch=amd64
      ;;
    esac

    export SRCDIR=${S}

    oe_runmake -f ${S}/debian/external/boringssl/libcrypto.mk -C ${S}
    oe_runmake -f ${S}/debian/external/libunwind/libunwind.mk -C ${S} CPU=${cpu}

    for tool in ${PREREQUISITE_core}; do
      oe_runmake -f ${S}/debian/system/core/${tool}.mk -C ${S}
    done

    for i in `find ${S}/debian/system/extras/ -name "*.mk"`; do
        oe_runmake -f $i -C ${S}
    done

    for tool in ${TOOLS_TO_BUILD}; do
        if [ "$tool" = "libbacktrace" ]; then
            oe_runmake -f ${S}/debian/system/core/${tool}.mk -C ${S} DEB_HOST_ARCH=${deb_host_arch}
        else
            oe_runmake -f ${S}/debian/system/core/${tool}.mk -C ${S}
        fi
    done

}

do_install() {
    install -d ${D}${base_sbindir}
    install -m 0755 ${UNPACKDIR}/remount -D ${D}${base_sbindir}/remount

    for tool in img2simg simg2img fastboot adbd; do
        if echo ${TOOLS_TO_BUILD} | grep -q "$tool" ; then
            install -D -p -m0755 ${S}/debian/out/system/core/$tool ${D}${bindir}/$tool
        fi
    done

    # grep adb also matches adbd, so handle adb separately from other tools
    if echo ${TOOLS_TO_BUILD} | grep -q "adb " ; then
        install -d ${D}${bindir}
        install -m0755 ${S}/debian/out/system/core/adb ${D}${bindir}
    fi

    # Outside the if statement to avoid errors during do_package
    install -D -p -m0644 ${UNPACKDIR}/android-tools-adbd.service \
      ${D}${systemd_unitdir}/system/android-tools-adbd.service

    install -d  ${D}${libdir}/android/
    install -m0755 ${S}/debian/out/system/core/*.so.* ${D}${libdir}/android/
    if echo ${TOOLS_TO_BUILD} | grep -q "mkbootimg" ; then
        install -d ${D}${bindir}
        install -m0755 ${B}/mkbootimg/mkbootimg ${D}${bindir}
    fi
}

PACKAGES =+ "${PN}-fstools ${PN}-adbd"

RDEPENDS:${BPN} = "${BPN}-conf p7zip"

FILES:${PN}-adbd = "\
    ${bindir}/adbd \
    ${systemd_unitdir}/system/android-tools-adbd.service \
"

FILES:${PN}-fstools = "\
    ${bindir}/ext2simg \
    ${bindir}/ext4fixup \
    ${bindir}/img2simg \
    ${bindir}/make_ext4fs \
    ${bindir}/simg2img \
    ${bindir}/simg2simg \
    ${bindir}/simg_dump \
    ${bindir}/mkuserimg \
"
FILES:${PN} += "${libdir}/android ${libdir}/android/*"

BBCLASSEXTEND = "native"

android_tools_enable_devmode() {
    touch ${IMAGE_ROOTFS}/etc/usb-debugging-enabled
}

ROOTFS_POSTPROCESS_COMMAND_${PN}-adbd += "${@bb.utils.contains("USB_DEBUGGING_ENABLED", "1", "android_tools_enable_devmode;", "", d)}"
