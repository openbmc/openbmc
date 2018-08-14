DUMMYARCH = "buildtools-dummy-${SDKPKGSUFFIX}"

DUMMYPROVIDES = "\
    nativesdk-perl \
    nativesdk-perl-module-file-path"

PR = "r2"

require dummy-sdk-package.inc

inherit nativesdk
