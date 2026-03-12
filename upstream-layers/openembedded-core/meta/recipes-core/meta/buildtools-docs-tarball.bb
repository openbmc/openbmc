require recipes-core/meta/buildtools-tarball.bb

DESCRIPTION = "SDK type target for building a standalone tarball containing the tools needed to build the project docs."
SUMMARY = "SDK type target for building a standalone tarball containing the tools needed to build the project docs."
LICENSE = "MIT"

# Add nativesdk equivalent of build-essentials
TOOLCHAIN_HOST_TASK += "\
    nativesdk-python3-sphinx \
    nativesdk-python3-sphinx-argparse \
    nativesdk-python3-sphinx-copybutton \
    nativesdk-python3-sphinx-rtd-theme \
    nativesdk-python3-sphinxcontrib-svg2pdfconverter \
    nativesdk-python3-pyyaml \
    nativesdk-rsvg \
    "

TOOLCHAIN_OUTPUTNAME = "${SDK_ARCH}-buildtools-docs-nativesdk-standalone-${DISTRO_VERSION}"

SDK_TITLE = "Docs Build tools tarball"

# Directory that contains testcases
TESTSDK_CASE_DIRS = "buildtools-docs"