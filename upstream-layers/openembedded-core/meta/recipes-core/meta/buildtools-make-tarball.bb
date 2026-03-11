require recipes-core/meta/buildtools-tarball.bb

DESCRIPTION = "SDK type target for building a standalone make binary. The tarball can be used to run bitbake builds \
               on systems where make is broken (e.g. the 4.2.1 version on CentOS 8 based distros)."
SUMMARY = "Standalone tarball for running builds on systems with inadequate make"
LICENSE = "MIT"

# Add nativesdk equivalent of build-essentials
TOOLCHAIN_HOST_TASK = "\
    nativesdk-sdk-provides-dummy \
    nativesdk-make \
    "
TOOLCHAIN_OUTPUTNAME = "${SDK_ARCH}-buildtools-make-nativesdk-standalone-${DISTRO_VERSION}"

SDK_TITLE = "Make build tool"
