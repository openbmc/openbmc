DESCRIPTION = "Various utilities from Android"
SECTION = "console/utils"
LICENSE = "Apache-2.0 & GPL-2.0-only & BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \
    file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6 \
    file://${COMMON_LICENSE_DIR}/BSD-2-Clause;md5=cb641bc04cda31daea161b1bc15da69f \
    file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \
"

DEPENDS = " \
    android-libboringssl \
    brotli \
    libcap \
    7zip \
    protobuf \
    protobuf-native \
    libusb \
    squashfs-tools \
    zlib \
"

# The debian/ patches are copied from android-platform-tools/debian/patches and
# applied in the order defined by debian/patches/series. Listing them as
# first-class SRC_URI entries (rather than running quilt inside do_patch) makes
# the recipe work correctly with devtool.
SRC_URI = "https://deb.debian.org/debian/pool/main/a/android-platform-tools/android-platform-tools_35.0.2.orig.tar.xz;name=orig;subdir=android-platform-tools-${PV} \
           https://deb.debian.org/debian/pool/main/a/android-platform-tools/android-platform-tools_35.0.2-1~exp6.debian.tar.xz;name=debian;subdir=android-platform-tools-${PV} \
           file://android-tools-adbd.service \
           file://debian/deb-gcc-stdatomic.patch \
           file://debian/deb-gcc-workaround__builtin_available.patch \
           file://debian/deb-gcc-Nullable.patch \
           file://debian/deb-sys-unwindstack-porting.patch \
           file://debian/deb-sys-move-log-file-to-proper-dir.patch \
           file://debian/deb-sys-Added-missing-headers.patch \
           file://debian/deb-sys-libusb-header-path.patch \
           file://debian/deb-sys-throw-exception-on-unknown-os.patch \
           file://debian/deb-sys-hard-code-build-number.patch \
           file://debian/deb-sys-stub-out-fastdeploy.patch \
           file://debian/deb-sys-Implement-const_iterator-operator.patch \
           file://debian/deb-sys-Drop-gki-dependency-from-mkbootimg.patch \
           file://debian/deb-sys-add-missing-headers.patch \
           file://debian/deb-dev-typos.patch \
           file://debian/deb-adbd-adbd-allow-usb-on-linux.patch \
           file://debian/deb-adbd-adbd-allow-notifying-systemd.patch \
           file://debian/deb-adbd-adbd-usb-drop-property-monitor.patch \
           file://debian/deb-adbd-adbd-don-t-require-authorization-on-Linux.patch \
           file://0001-libbacktrace.mk-Link-against-staged-lib7z.patch \
           file://0002-debian-makefiles-yocto-compat.patch \
           file://0003-gcc-nullability-and-thread-annotation-compat.patch \
           file://0004-fastboot-super_flash_helper-include-climits.patch \
           file://0005-adb-host-usb-compat.patch \
           file://0006-adbd-enable-root-and-remount-support.patch \
           file://0007-libcutils-guard-Android-private-header-with-addition.patch \
           file://0008-adb-GCC-compatibility-fixes-for-usb_linux-and-sysdep.patch \
           file://0009-libbase-include-stdint.h-in-hex.cpp.patch \
           file://0010-adbd-make-systemd-sd_notify-conditional-on-HAVE_SYSTEMD.patch \
           file://0011-adb-drop-non-portable-is_standard_layout-assertion.patch \
           "

SRC_URI[orig.md5sum] = "352376965cdef7bd7505d8fefdd43d50"
SRC_URI[orig.sha256sum] = "ec1d317608db3328bfbddf7152c8d7f185c7c87b2175081416344434546a43da"
SRC_URI[debian.md5sum] = "1de890bd272da9e8cd35bc9579802f1f"
SRC_URI[debian.sha256sum] = "f03a89b82ea8dfbe3cb77d5326eedf0d15984f5896885e8c5df64cf421819579"

S = "${UNPACKDIR}/android-platform-tools-${PV}"
B = "${UNPACKDIR}/${BPN}"

# http://errors.yoctoproject.org/Errors/Details/1debian881/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"

COMPATIBLE_HOST:powerpc = "(null)"
COMPATIBLE_HOST:powerpc64 = "(null)"
COMPATIBLE_HOST:powerpc64le = "(null)"

inherit systemd

SYSTEMD_PACKAGES = "${PN}-adbd"
SYSTEMD_SERVICE:${PN}-adbd = "android-tools-adbd.service"

CFLAGS:append = " -fPIC -std=gnu2x"
# The bundled fmtlib 10.2.0 validates FMT_STRING() inside a consteval
# basic_format_string constructor whose parse path evaluates "it - begin()",
# which current C++ frontends reject as a non-constant subexpression. Define
# FMT_CONSTEVAL to empty so format-string checking falls back to fmt's runtime
# path instead of the broken compile-time one.
CXXFLAGS:append = " -fPIC -std=gnu++20 -D_Nonnull= -D_Nullable= -I${STAGING_INCDIR}/boringssl -DFMT_CONSTEVAL="
LDFLAGS:append = " -fPIC -L${STAGING_LIBDIR}/android"


CC:append:class-native = " -I${STAGING_INCDIR}"
CC:append:class-nativesdk = " -I${STAGING_INCDIR}"

PREREQUISITE_core = ""
PREREQUISITE_core:class-target = "liblog libbase libcutils libcrypto_utils libadb"
PREREQUISITE_core:class-native = "liblog libbase libcutils libziparchive libsparse libcrypto_utils libadb"

TOOLS_TO_BUILD = ""
TOOLS_TO_BUILD:class-target = "adbd"
TOOLS_TO_BUILD:class-native = "adb fastboot img2simg simg2img"

BUILD_SYSROOT = "${RECIPE_SYSROOT}"
BUILD_SYSROOT:class-native = "${RECIPE_SYSROOT_NATIVE}"

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
        cpu=riscv64
        deb_host_arch=riscv64
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
        cpu=ppc
        deb_host_arch=ppc64
        ;;
    i586|i686)
        export android_arch=linux-x86
        cpu=x86
        deb_host_arch=i386
        ;;
    x86_64)
        export android_arch=linux-x86
        cpu=x86_64
        deb_host_arch=amd64
        ;;
    *)
        bbfatal "Unsupported HOST_ARCH=${HOST_ARCH}"
        ;;
    esac

    export SRCDIR=${S}
    export DEB_HOST_ARCH=${deb_host_arch}
    export DEB_HOST_MULTIARCH=
    export SYSROOT=${BUILD_SYSROOT}

    if echo " ${TOOLS_TO_BUILD} " | grep -Eq " (adb|fastboot) "; then
        # Native host tools should use the host system's ELF interpreter, not
        # Yocto's uninative loader under /workdir.
        export LDFLAGS="$(printf '%s\n' "${LDFLAGS}" | \
            sed -E 's@-Wl,--dynamic-linker=[^[:space:]]+@@g; s@[[:space:]]+@ @g; s@^[[:space:]]+@@; s@[[:space:]]+$@@')"
    fi

    # Debian's make fragments expect generated protobuf sources to already be
    # present under packages/modules/adb/proto, but the orig tarball only ships
    # the .proto inputs.
    for proto in adb_host adb_known_hosts app_processes key_type pairing; do
        ${STAGING_BINDIR_NATIVE}/protoc \
            --proto_path=${S}/packages/modules/adb/proto \
            --cpp_out=${S}/packages/modules/adb/proto \
            ${S}/packages/modules/adb/proto/${proto}.proto
    done

    if [ -d ${S}/packages/modules/adb/fastdeploy/proto ]; then
        for proto in ${S}/packages/modules/adb/fastdeploy/proto/*.proto; do
            [ -e "$proto" ] || continue
            ${STAGING_BINDIR_NATIVE}/protoc \
                --proto_path=${S}/packages/modules/adb \
                --cpp_out=${S}/packages/modules/adb \
                "$proto"
        done
    fi

    # Debian's make fragments write archives and binaries under debian/out/*
    # but do not consistently create parent directories before invoking ar.
    install -d ${S}/debian/out/system/extras

    # Debian replaces Android's Soong build system with hand-written GNU make
    # fragments under debian/system/ and debian/external/.
    for tool in ${PREREQUISITE_core}; do
        oe_runmake -f ${S}/debian/system/${tool}.mk -C ${S}
    done

    if echo " ${TOOLS_TO_BUILD} " | grep -q " fastboot "; then
        # Debian's fastboot.mk links against the ext4_utils static archive from
        # debian/out/system/extras, so build that helper archive first.
        oe_runmake -f ${S}/debian/system/extras/libext4_utils.mk -C ${S}
    fi

    for tool in ${TOOLS_TO_BUILD}; do
        oe_runmake -f ${S}/debian/system/${tool}.mk -C ${S} \
            ${@'HAVE_SYSTEMD=1' if bb.utils.contains('PACKAGECONFIG', 'systemd', True, False, d) else ''}
    done
}

do_install() {
    for tool in ${TOOLS_TO_BUILD}; do
        install -D -p -m0755 ${S}/debian/out/system/${tool} ${D}${bindir}/${tool}
    done

    if echo " ${TOOLS_TO_BUILD} " | grep -q " adbd "; then
        install -D -p -m0644 ${UNPACKDIR}/android-tools-adbd.service \
          ${D}${systemd_unitdir}/system/android-tools-adbd.service
    fi

    install -d ${D}${libdir}/android/
    install -m0755 ${S}/debian/out/system/lib*.so.* ${D}${libdir}/android/
}

PACKAGES =+ "${PN}-adbd"
RDEPENDS:${PN}:class-target = "android-tools-conf-configfs 7zip android-libboringssl"
RDEPENDS:${PN}-adbd += "${PN}"
PRIVATE_LIBS:${PN} = "liblog.so.0 libbase.so.0 libcutils.so.0"

inherit useradd
USERADD_PACKAGES = "${PN}-adbd"
USERADD_PARAM:${PN}-adbd = "--system --no-create-home --shell /bin/false --user-group adb"

FILES:${PN} += " \
    ${libdir}/android \
    ${libdir}/android/* \
"

FILES:${PN}-adbd = " \
    ${bindir}/adbd \
    ${systemd_unitdir}/system/android-tools-adbd.service \
"

BBCLASSEXTEND = "native"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = ",,systemd"
