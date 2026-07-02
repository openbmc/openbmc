DESCRIPTION = "BoringSSL shared libraries for android-tools"
SECTION = "libs"
# This recipe is tightly coupled to android-tools: when upgrading android-tools,
# update this recipe to the BoringSSL version shipped in the corresponding Debian
# android-platform-tools source package.
# BoringSSL is a fork of OpenSSL; new files carry ISC license, OpenSSL license
# covers the forked parts.
LICENSE = "OpenSSL & ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ca501bc96ce9ed0814e2c592c3f9593"

SRC_URI = "https://deb.debian.org/debian/pool/main/a/android-platform-external-boringssl/android-platform-external-boringssl_${PV}.orig.tar.xz \
           file://boringssl-go-stub \
           file://boringssl-gtest-stub.cc \
           file://0001-cmake-add-SOVERSION-0-to-crypto-and-ssl-shared-libra.patch \
           file://0001-Avoid-redefining-constants-introduced-in-glibc-2.41.patch;patchdir=.. \
           "
SRC_URI[md5sum] = "83d24d2f3136ba6a486b5464369b91b4"
SRC_URI[sha256sum] = "f9223e8c15ad5d9e3f1cd50861f4c272658864661e2332bea5d60952aa0930cd"

# The Debian orig tarball unpacks to android-platform-external-boringssl-${PV}/
# with the actual source under a src/ subdirectory.
S = "${UNPACKDIR}/android-platform-external-boringssl-${PV}/src"

inherit cmake

# -Wno-discarded-qualifiers is a GCC spelling; clang names this warning
# -Wno-incompatible-pointer-types-discards-qualifiers. BoringSSL compiles with
# -Werror -Werror=unknown-warning-option, so an unknown -W option breaks every
# configure try_compile (including the pthread/Threads detection).
CFLAGS:append:toolchain-gcc = " -Wno-discarded-qualifiers"
CFLAGS:append:toolchain-clang = " -Wno-incompatible-pointer-types-discards-qualifiers"

OECMAKE_TARGET_COMPILE = "crypto ssl"

EXTRA_OECMAKE = " \
    -DBUILD_SHARED_LIBS=ON \
    -DBUILD_TESTING=OFF \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DGO_EXECUTABLE=${WORKDIR}/hosttools/go \
"

do_configure:prepend() {
    install -d ${WORKDIR}/hosttools
    install -m 0755 ${UNPACKDIR}/boringssl-go-stub ${WORKDIR}/hosttools/go

    # BoringSSL builds its own minimal gtest from third_party/googletest; provide
    # an empty stub .cc. We only build the crypto and ssl targets so the test
    # source files are never compiled, but cmake still needs gtest-all.cc to
    # exist to create the boringssl_gtest target.
    if [ ! -f ${S}/third_party/googletest/src/gtest-all.cc ]; then
        install -d ${S}/third_party/googletest/src
        install -m 0644 ${UNPACKDIR}/boringssl-gtest-stub.cc \
            ${S}/third_party/googletest/src/gtest-all.cc
    fi
}
do_install() {
    # Install headers under a boringssl/ subdirectory to avoid shadowing the
    # system OpenSSL headers in the sysroot.
    install -d ${D}${includedir}/boringssl
    cp -a ${S}/include/openssl ${D}${includedir}/boringssl/

    # Install shared libraries under ${libdir}/android/ to avoid conflicting
    # with the system libssl/libcrypto in the sysroot.
    install -d ${D}${libdir}/android
    install -m 0755 ${B}/crypto/libcrypto.so.0 ${D}${libdir}/android/libcrypto.so.0
    ln -sf libcrypto.so.0 ${D}${libdir}/android/libcrypto.so
    install -m 0755 ${B}/ssl/libssl.so.0 ${D}${libdir}/android/libssl.so.0
    ln -sf libssl.so.0 ${D}${libdir}/android/libssl.so

}

FILES:${PN}-dev = "${includedir}/boringssl ${libdir}/android/libcrypto.so ${libdir}/android/libssl.so"
FILES:${PN} = "${libdir}/android/libcrypto.so.0 ${libdir}/android/libssl.so.0"

BBCLASSEXTEND = "native"
