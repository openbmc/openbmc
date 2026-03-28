DUMMYARCH = "sdk-provides-dummy-${SDKPKGSUFFIX}"

DUMMYSDK_PKGDATA_VARNAME = "PKGDATA_DIR_SDK"
DUMMYSDK_EXTRASTAMP_VARNAME = "SDK_SYS"

DUMMYPROVIDES_PACKAGES = "\
    pkgconfig \
"

DUMMYPROVIDES = "\
    /bin/sh \
    /bin/bash \
    /usr/bin/env \
    libGL.so()(64bit) \
    libGL.so \
"

require dummy-sdk-package.inc

inherit nativesdk
