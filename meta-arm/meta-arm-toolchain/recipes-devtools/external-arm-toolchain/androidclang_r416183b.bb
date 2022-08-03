# SPDX-License-Identifier: MIT
#
# Copyright (c) 2021 Arm Limited
#

SUMMARY = "Android Clang compiler"
DESCRIPTION = "Android Clang compiler, version r416183b. This is based on Clang 12.0.5 \
Intended usage is to build kernel images that match the output of the Android (hermetic) \
build system"

LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://MODULE_LICENSE_MIT;md5=d41d8cd98f00b204e9800998ecf8427e"

ANDROID_CLANG_VERSION = "clang-r416183b"
ANDROID_CLANG_HASH = "bd96dfe349c962681f0e5388af874c771ef96670"

COMPATIBLE_HOST = "x86_64.*-linux"

SRC_URI = "https://android.googlesource.com/platform/prebuilts/clang/host/linux-x86/+archive/${ANDROID_CLANG_HASH}/${ANDROID_CLANG_VERSION}.tar.gz;subdir=${ANDROID_CLANG_VERSION}"

# We need to set the checksum to "ignore" because the tarball is dynamically generated and has a new checksum every time
# (the contents are the same, but the time stamp differs)
BB_STRICT_CHECKSUM = "ignore"

S = "${WORKDIR}/${ANDROID_CLANG_VERSION}"

FILES:${PN} = "${libexecdir} ${bindir}"

do_install() {
    install -d ${D}${libexecdir}/${ANDROID_CLANG_VERSION}/

    cp --no-preserve=ownership -r ${S}/. ${D}${libexecdir}/${ANDROID_CLANG_VERSION}/
    # Strip bad RPATHs in the embedded python3
    chrpath -d ${D}${libexecdir}/${ANDROID_CLANG_VERSION}/python3/lib/python*/lib-dynload/*.so

    install -d ${D}${bindir}
    # Symlink all executables into bindir
    for f in ${D}${libexecdir}/${ANDROID_CLANG_VERSION}/bin/*; do
        ln -rs $f ${D}${bindir}/$(basename $f)
    done
}

INHIBIT_DEFAULT_DEPS = "1"

INSANE_SKIP:${PN} = "already-stripped libdir staticdev file-rdeps arch dev-so"

INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

BBCLASSEXTEND = "native nativesdk"
