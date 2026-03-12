LICENSE = "Apache-2.0 & BSD-3-Clause & MIT & Zlib"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c0c9924c5c63b4834b8b1959816c8e3b \
                    file://buildtools/libcxx/LICENSE.TXT;md5=55d89dd7eec8d3b4204b680e27da3953 \
                    file://buildtools/libcxxabi/LICENSE.TXT;md5=7b9334635b542c56868400a46b272b1e \
                    file://buildtools/libunwind/LICENSE.TXT;md5=f66970035d12f196030658b11725e1a1 \
                    file://buildtools/protobuf/LICENSE;md5=37b5762e07f0af8c74ce80a8bda4266b \
                    file://buildtools/zlib/LICENSE;md5=8c75f2b4df47a77f9445315a9500cd1c \
                    file://debian/copyright;md5=55b18749ff89714316c007d06f305c4a \
                    file://python/LICENSE;md5=c602a632c34ade9c78a976734077bce7"

# Dependencies from perfetto/tools/install-build-deps
SRC_URI:append = " \
           git://github.com/protocolbuffers/protobuf.git;branch=main;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/protobuf;name=protobuf \
           git://chromium.googlesource.com/external/github.com/llvm/llvm-project/libcxx.git;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/libcxx;branch=main;name=libcxx \
           git://chromium.googlesource.com/external/github.com/llvm/llvm-project/libcxxabi.git;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/libcxxabi;branch=main;name=libcxxabi \
           git://chromium.googlesource.com/external/github.com/llvm/llvm-project/libunwind.git;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/libunwind;branch=main;name=libunwind \
           git://android.googlesource.com/platform/system/libbase.git;branch=master;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/android-libbase;name=libbase \
           git://android.googlesource.com/platform/system/unwinding.git;branch=master;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/android-unwinding;name=unwinding \
           git://android.googlesource.com/platform/system/logging.git;branch=master;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/android-logging;name=logging \
           git://android.googlesource.com/platform/system/libprocinfo.git;branch=master;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/android-libprocinfo;name=libprocinfo \
           git://android.googlesource.com/platform/system/core.git;branch=master;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/android-core;name=core \
           git://android.googlesource.com/platform/bionic.git;branch=master;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/bionic;name=bionic \
           git://chromium.googlesource.com/chromium/src/third_party/zlib.git;nobranch=1;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/zlib;name=zlib \
           git://android.googlesource.com/platform/external/lzma.git;branch=master;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/lzma;name=lzma \
           git://android.googlesource.com/platform/external/zstd.git;branch=master;protocol=https;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/zstd;name=zstd \
           https://storage.googleapis.com/perfetto/gn-linux64-1968-0725d782;subdir=${BB_GIT_DEFAULT_DESTSUFFIX}/buildtools/;name=gn \
           \
           file://0001-Remove-check_build_deps-build-steps.patch \
           "

SRCREV_bionic = "a0d0355105cb9d4a4b5384897448676133d7b8e2"
SRCREV_core = "9e6cef7f07d8c11b3ea820938aeb7ff2e9dbaa52"
SRCREV_lzma = "7851dce6f4ca17f5caa1c93a4e0a45686b1d56c3"
SRCREV_libprocinfo = "fd214c13ededecae97a3b15b5fccc8925a749a84"
SRCREV_logging = "7b36b566c9113fc703d68f76e8f40c0c2432481c"
SRCREV_unwinding = "4b59ea8471e89d01300481a92de3230b79b6d7c7"
SRCREV_protobuf = "f0dc78d7e6e331b8c6bb2d5283e06aa26883ca7c"
SRCREV_libbase = "78f1c2f83e625bdf66d55b48bdb3a301c20d2fb3"
SRCREV_libcxx = "852bc6746f45add53fec19f3a29280e69e358d44"
SRCREV_libcxxabi = "a37a3aa431f132b02a58656f13984d51098330a2"
SRCREV_libunwind = "419b03c0b8f20d6da9ddcb0d661a94a97cdd7dad"
SRCREV_zlib = "6f9b4e61924021237d474569027cfb8ac7933ee6"
SRCREV_zstd = "77211fcc5e08c781734a386402ada93d0d18d093"

SRCREV_FORMAT .= "_bionic_core_lzma_libprocinfo_logging_unwinding_protobuf_libbase_libcxx_libcxxabi_libunwind_zlib_zstd"

SRC_URI[gn.sha256sum] = "f706aaa0676e3e22f5fc9ca482295d7caee8535d1869f99efa2358177b64f5cd"

require perfetto.inc

DEPENDS += " ninja-native gn-native"

# Use clang in order to enable traced_perf ( see https://github.com/google/perfetto/blob/092d0ceace6fa516fac1bd4e715c226eaaebe26e/gn/perfetto.gni#L177 ,
# enable_perfetto_traced_perf depends on "is_clang")
TOOLCHAIN = "clang"
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

    mkdir linux64 && cp ${RECIPE_SYSROOT_NATIVE}${bindir}/gn linux64/gn
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
    ARGS=$ARGS" target_cxx=\"$CXX_BIN ${TUNE_CCARGS} ${DEBUG_PREFIX_MAP}\""
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
