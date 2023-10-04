SUMMARY = "Target packages for the standalone SDK"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

RDEPENDS:${PN} = "\
    libgcc \
    libgcc-dev \
    libatomic \
    libatomic-dev \
    libstdc++ \
    libstdc++-dev \
    ${LIBC_DEPENDENCIES} \
    "

RRECOMMENDS:${PN}:mingw32 = "\
    libssp \
    libssp-dev \
    "
