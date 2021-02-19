require recipes-core/meta/buildtools-tarball.bb

DESCRIPTION = "SDK type target for building a standalone tarball containing build-essentials, python3, chrpath, \
               make, git and tar. The tarball can be used to run bitbake builds on systems which don't meet the \
               usual version requirements and have ancient compilers."
SUMMARY = "Standalone tarball for running builds on systems with inadequate software and ancient compilers"
LICENSE = "MIT"

# Add nativesdk equivalent of build-essentials
TOOLCHAIN_HOST_TASK += "\
    nativesdk-automake \
    nativesdk-autoconf \
    nativesdk-binutils \
    nativesdk-binutils-symlinks \
    nativesdk-cpp \
    nativesdk-cpp-symlinks \
    nativesdk-gcc \
    nativesdk-gcc-symlinks \
    nativesdk-g++ \
    nativesdk-g++-symlinks \
    nativesdk-gettext \
    nativesdk-libatomic \
    nativesdk-libgcc \
    nativesdk-libgomp-dev \
    nativesdk-libstdc++ \
    nativesdk-libstdc++-dev \
    nativesdk-libstdc++-staticdev \
    nativesdk-libtool \
    nativesdk-pkgconfig \
    nativesdk-glibc-utils \
    nativesdk-glibc-gconv-ibm850 \
    nativesdk-glibc-gconv-iso8859-1 \
    nativesdk-glibc-gconv-utf-16 \
    nativesdk-glibc-gconv-cp1250 \
    nativesdk-glibc-gconv-cp1251 \
    nativesdk-glibc-gconv-cp1252 \
    nativesdk-glibc-gconv-euc-jp \
    nativesdk-glibc-gconv-libjis \
    nativesdk-libxcrypt-dev \
    nativesdk-parted \
    nativesdk-dosfstools \
    nativesdk-gptfdisk \
    "
# gconv-cp1250, cp1251 and euc-jp needed for iconv to work in vim builds
# also copied list from uninative

TOOLCHAIN_OUTPUTNAME = "${SDK_ARCH}-buildtools-extended-nativesdk-standalone-${DISTRO_VERSION}"

SDK_TITLE = "Extended Build tools"
