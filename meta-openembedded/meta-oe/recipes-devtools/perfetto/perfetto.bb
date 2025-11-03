LICENSE = "Apache-2.0 & BSD-3-Clause & MIT & Zlib"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f87516e0b698007e9e75a1fe1012b390 \
                    file://buildtools/libcxx/LICENSE.TXT;md5=55d89dd7eec8d3b4204b680e27da3953 \
                    file://buildtools/libcxxabi/LICENSE.TXT;md5=7b9334635b542c56868400a46b272b1e \
                    file://buildtools/libunwind/LICENSE.TXT;md5=f66970035d12f196030658b11725e1a1 \
                    file://buildtools/protobuf/LICENSE;md5=37b5762e07f0af8c74ce80a8bda4266b \
                    file://buildtools/zlib/LICENSE;md5=f09575dbfb09420642318b413159496f \
                    file://debian/copyright;md5=4e08364c82141f181de69d0a2b89d612 \
                    file://python/LICENSE;md5=c602a632c34ade9c78a976734077bce7"

SRC_URI:append = " \
           git://github.com/protocolbuffers/protobuf.git;branch=3.9.x;protocol=https;destsuffix=git/buildtools/protobuf;name=protobuf \
           git://chromium.googlesource.com/external/github.com/llvm/llvm-project/libcxx.git;protocol=https;destsuffix=git/buildtools/libcxx;branch=main;name=libcxx \
           git://chromium.googlesource.com/external/github.com/llvm/llvm-project/libcxxabi.git;protocol=https;destsuffix=git/buildtools/libcxxabi;branch=main;name=libcxxabi \
           git://chromium.googlesource.com/external/github.com/llvm/llvm-project/libunwind.git;protocol=https;destsuffix=git/buildtools/libunwind;branch=main;name=libunwind \
           git://android.googlesource.com/platform/system/libbase.git;branch=master;protocol=https;destsuffix=git/buildtools/android-libbase;name=libbase \
           git://android.googlesource.com/platform/system/unwinding.git;branch=master;protocol=https;destsuffix=git/buildtools/android-unwinding;name=unwinding \
           git://android.googlesource.com/platform/system/logging.git;branch=master;protocol=https;destsuffix=git/buildtools/android-logging;name=logging \
           git://android.googlesource.com/platform/system/libprocinfo.git;branch=master;protocol=https;destsuffix=git/buildtools/android-libprocinfo;name=libprocinfo \
           git://android.googlesource.com/platform/system/core.git;branch=master;protocol=https;destsuffix=git/buildtools/android-core;name=core \
           git://android.googlesource.com/platform/bionic.git;branch=master;protocol=https;destsuffix=git/buildtools/bionic;name=bionic \
           git://android.googlesource.com/platform/external/zlib.git;branch=master;protocol=https;destsuffix=git/buildtools/zlib;name=zlib \
           git://android.googlesource.com/platform/external/lzma.git;branch=master;protocol=https;destsuffix=git/buildtools/lzma;name=lzma \
           https://storage.googleapis.com/perfetto/gn-linux64-1968-0725d782;subdir=git/buildtools/;name=gn \
           \
           file://0001-Remove-check_build_deps-build-steps.patch \
           file://0001-Add-missing-header-cstdint-for-uintXX_t-types.patch"

SRCREV_bionic = "4b0e16bc72a82a63c699977376a7d6eadca1b206"
SRCREV_core = "9e6cef7f07d8c11b3ea820938aeb7ff2e9dbaa52"
SRCREV_lzma = "7851dce6f4ca17f5caa1c93a4e0a45686b1d56c3"
SRCREV_libprocinfo = "fd214c13ededecae97a3b15b5fccc8925a749a84"
SRCREV_logging = "7b36b566c9113fc703d68f76e8f40c0c2432481c"
SRCREV_unwinding = "d66882575ebe3700d6a6b10185f3aee28acc1051"
SRCREV_protobuf = "6a59a2ad1f61d9696092f79b6d74368b4d7970a3"
SRCREV_libbase = "78f1c2f83e625bdf66d55b48bdb3a301c20d2fb3"
SRCREV_libcxx = "f8571eaba606bde2eb8cd34b30104ca33e7c207e"
SRCREV_libcxxabi = "8dd405113a4f3694e910b79785dd7fb7535a888a"
SRCREV_libunwind = "aabcd8753678f1536e15eb6385a948470debdae4"
SRCREV_zlib = "5c85a2da4c13eda07f69d81a1579a5afddd35f59"

SRCREV_FORMAT .="_bionic_core_lzma_libprocinfo_logging_unwinding_protobuf_libbase_libcxx_libcxxabi_libunwind_zlib"

SRC_URI[gn.sha256sum] = "f706aaa0676e3e22f5fc9ca482295d7caee8535d1869f99efa2358177b64f5cd"

require perfetto.inc

DEPENDS += " ninja-native"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm).*-linux*"

CCACHE_DISABLE = "1"

# Some musl hacks gets through compiling it for musl
# Nullifying -DTEMP_FAILURE_RETRY might be grossest of them
TUNE_CCARGS:append:libc-musl = " -D_LIBCPP_HAS_MUSL_LIBC -Dgetprogname\(\)=program_invocation_name -DTEMP_FAILURE_RETRY="
FILES:${PN}:append = " \
  ${bindir}/tracebox \
  "

B = "${WORKDIR}/build"

# Run the GN (Generate Ninja) script, and replace the compiler flags where applicable
do_configure () {
    # Configuration needs to be done from the source directory
    cd ${S}
    # Rename a few build tools if they have not been renamed
    cd buildtools
    x="gn-linux64-1968-0725d782"
    [ -f $x ] && mkdir linux64 && mv $x linux64/gn
    chmod +x linux64/gn
    cd ..

    CC_BIN=`echo $CC | awk '{print $1}'`
    CXX_BIN=`echo $CXX | awk '{print $1}'`
    STRIP_BIN=`echo $STRIP | awk '{print $1}'`

    ARGS="is_debug=false "  # Tell gn to use release mode

    if [ -z `echo ${TOOLCHAIN} | grep clang` ]; then
        ARGS=$ARGS" is_clang=false"
    else
        ARGS=$ARGS" is_clang=true"
    fi
    
    # Architecture parameter accepted by Perfetto
    arch=${TARGET_ARCH}
    if [ $arch = "i686" ]; then
        arch="x86"
    elif [ $arch = "x86_64" ]; then
        arch="x64"
    elif [ $arch = "aarch64" ]; then
        arch="arm64"
    fi

    ARGS=$ARGS" target_os=\"linux\""
    ARGS=$ARGS" target_cpu=\"$arch\""
    ARGS=$ARGS" target_cc=\"$CC_BIN ${TUNE_CCARGS} ${DEBUG_PREFIX_MAP}\""
    ARGS=$ARGS" target_cxx=\"$CXX_BIN -std=c++11 ${TUNE_CCARGS} ${DEBUG_PREFIX_MAP}\""
    ARGS=$ARGS" target_strip=\"$STRIP_BIN\"" #
    ARGS=$ARGS" target_sysroot=\"${RECIPE_SYSROOT}\""
    ARGS=$ARGS" target_linker=\"$CC_BIN ${TUNE_CCARGS} ${LDFLAGS}\""
    ARGS=$ARGS" target_ar=\"$AR\""
    ARGS="'$ARGS'"
    cmd="tools/gn gen --args=$ARGS ${B}"

    echo $cmd
    # Use eval, not just call $cmd, due to escaping of single quotation marks
    eval $cmd

    cd ${B}
    # Eliminate a few incompatible build flags
    REPLACES="s/-Wl,--icf=all//g"
    REPLACES=$REPLACES";s/-Werror//g"
    REPLACES=$REPLACES";s/-fcolor-diagnostics//g"
    REPLACES=$REPLACES";s/=format-security//g"
    REPLACES=$REPLACES";s/-fdiagnostics-show-template-tree//g"
    REPLACES=$REPLACES";s/-D_FORTIFY_SOURCE=2//g"
    REPLACES=$REPLACES";s/-fuse-ld=\S*//g"

    find . -name "*.ninja" | xargs sed $REPLACES -i

    # If using the clang toolchain: use the clang host-side binaries built by Bitbake
    if [ "${TOOLCHAIN}" = "clang" ]; then
        BB_CLANGXX="${BUILD_CXX} ${BUILD_LDFLAGS}"
        BB_CLANG="${BUILD_CC}"
        BB_LLVM_OBJCOPY="${RECIPE_SYSROOT_NATIVE}/usr/bin/llvm-objcopy"
        
        HOST_CLANGXX="${STAGING_DIR_NATIVE}/usr/bin/clang++ -stdlib=libc++ -rtlib=libgcc -unwindlib=libgcc"
        HOST_CLANG="${STAGING_DIR_NATIVE}/usr/bin/clang"
        HOST_LLVM_OBJCOPY="${STAGING_DIR_NATIVE}/usr/bin/llvm-objcopy"

        cd gcc_like_host
        REPLACES="s:\S*clang++ :$HOST_CLANGXX :g"
        REPLACES=$REPLACES";s:\S*clang :$HOST_CLANG :g"
        REPLACES=$REPLACES";s:\S*llvm-objcopy :$HOST_LLVM_OBJCOPY :g"
        find . -name "*.ninja" | xargs sed "$REPLACES" -i
        cd ..
    fi
    # Done processing the Ninja files
}

# Perfetto generates a few different binaries, such as traced and traced_probes and perfetto.
# The "tracebox" is a busybox that combines the 3 above and provides a single stop for trace capture, so we only build "tracebox" here.
do_compile () {
    cd ${B}
    ninja -C . tracebox
}

do_install () {
    BIN_DIR=${D}${bindir}

    install -d -m0755 $BIN_DIR
    install ${B}/tracebox $BIN_DIR/tracebox
}
