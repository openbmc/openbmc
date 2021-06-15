SUMMARY = "Target packages for the standalone SDK"
PR = "r8"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

RDEPENDS_${PN} = "\
    libgcc \
    libgcc-dev \
    libatomic \
    libatomic-dev \
    libstdc++ \
    libstdc++-dev \
    ${LIBC_DEPENDENCIES} \
    "

RRECOMMENDS_${PN}_mingw32 = "\
    libssp \
    libssp-dev \
    "
