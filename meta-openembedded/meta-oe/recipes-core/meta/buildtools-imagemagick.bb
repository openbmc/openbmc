require recipes-core/meta/buildtools-tarball.bb

SUMMARY = "Standalone tarball of imagemagick binaries"
LICENSE = "MIT"

# Add nativesdk equivalent of build-essentials
TOOLCHAIN_HOST_TASK = "nativesdk-imagemagick nativesdk-sdk-provides-dummy"
TOOLCHAIN_OUTPUTNAME = "${SDK_ARCH}-buildtools-imagemagick-nativesdk-standalone-${DISTRO_VERSION}"

SDK_TITLE = "Imagemagick tools"
