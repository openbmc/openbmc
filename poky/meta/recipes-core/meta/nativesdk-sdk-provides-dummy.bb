DUMMYARCH = "sdk-provides-dummy-${SDKPKGSUFFIX}"

DUMMYPROVIDES_PACKAGES = "\
    pkgconfig \
"

# Add /bin/sh?
DUMMYPROVIDES = "\
    /bin/bash \
    /usr/bin/env \
    libGL.so()(64bit) \
    libGL.so \
"

require dummy-sdk-package.inc

inherit nativesdk
