SUMMARY = "ODE is an Open Source Physics Engine"
SECTION = "libs"
HOMEPAGE = "http://www.ode.org"
LICENSE = "LGPLv2.1 & BSD"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=1de906ee96808d9776dd72a5f9a79a22 \
    file://LICENSE.TXT;md5=771782cb6245c7fbbe74bc0ec059beff \
"
# LICENSE-BSD.TXT is missing in 0.13 tarball, but COPYING still says it's dual licensed
# and svn repo still contains LICENSE-BSD.TXT so maybe it will return in next tarball
# file://LICENSE-BSD.TXT;md5=c74e6304a772117e059458fb9763a928


SRC_URI = "${SOURCEFORGE_MIRROR}/opende/ode-${PV}.tar.bz2 \
           file://configure.patch"
SRC_URI[md5sum] = "04b32c9645c147e18caff7a597a19f84"
SRC_URI[sha256sum] = "34ce3e236e313bf109a0cb5546d2fca462aed99f29a42e62bc1463b803c31ef9"

inherit autotools binconfig

EXTRA_OECONF = "--disable-demos --enable-soname"

FILES_${PN} = "${libdir}/lib*${SOLIBS}"

PACKAGECONFIG ?= ""
# if it isn't explicitly selected and "$build_os" == "$target_os", then configure will run
# series of AC_TRY_RUN which won't work for cross-compilation and configure fails
PACKAGECONFIG[double-precision] = "--enable-double-precision,--disable-double-precision"
