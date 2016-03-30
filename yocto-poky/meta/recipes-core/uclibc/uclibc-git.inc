SRCREV = "ca1c74d67dd115d059a875150e10b8560a9c35a8"

PV = "0.9.33+git${SRCPV}"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/uclibc-git:"

SRC_URI = "git://uclibc.org/uClibc.git;branch=master \
        file://uClibc.machine \
        file://uClibc.distro \
        file://obstack.cfg \
        file://locale.cfg \
        file://0001-Disable-lrount_tes-function.patch \
        file://0002-Add-implementation-for-copysignl-for-ppc.patch \
        file://0003-Add-argp-implementation.patch \
        file://0004-Dont-support-localised-optimizations-this-helps-to-h.patch \
        file://0005-Always-use-O2-for-compiling-fork.c.patch \
        file://0006-ldso-limited-support-for-ORIGIN-in-rpath.patch \
        file://0007-nptl-atfork-Hide-pthread_atfork-in-shared-versions.patch \
        file://0001-gcc5-optimizes-away-the-write-only-static-functions-.patch \
        file://0001-fcntl-Add-AT_EMPTY_PATH-for-all-and-O_PATH-for-arm.patch \
        file://0001-wire-in-syncfs.patch \
        file://CVE-2016-2224.patch \
        file://CVE-2016-2225.patch \
"
S = "${WORKDIR}/git"
