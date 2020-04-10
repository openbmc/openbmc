SUMMARY = "eCMD"
DESCRIPTION = "eCMD is a hardware access API for POWER Systems"
LICENSE= "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/NOTICE;md5=fee220301a2af3faf8f211524b4248ea"

SRC_URI = "git://github.com/open-power/eCMD.git;branch=ecmd15;protocol=git"
SRCREV = "15e382180d49f7ea4117ccc341ca91e361721fd4"

inherit python3native
DEPENDS = "zlib"

S = "${WORKDIR}/git"

export LD="${CXX}"
export SLDFLAGS="${LDFLAGS}"

# use native config.py to build required ecmd extensions
do_configure() {
   ${S}/config.py --without-swig --without-python --without-python3 --without-perl \
       --without-pyecmd --install-path ${D}${prefix} --output-root ${B} --target ${TARGET_ARCH} \
       --extensions "cmd cip"
}

do_compile() {
    oe_runmake all
}

do_install() {
    oe_runmake install

    # we don't need the target scripts or ecmd setup
    rm ${D}${bindir}/target.* ${D}${bindir}/ecmdsetup.pl

    # ecmd installs to atypical places in the filesystem.
    # move all the installed files to more conventional directories.
    install -d ${D}${includedir} ${D}${datadir}/${BPN}/help ${D}${bindir} ${D}${libdir}
    mv ${D}${prefix}/help/**  ${D}${datadir}/${BPN}/help
    mv ${D}${prefix}/${TARGET_ARCH}/bin/**  ${D}${bindir}
    mv ${D}${prefix}/${TARGET_ARCH}/lib/**  ${D}${libdir}

    rm ${D}${bindir}/ecmdVersion

    rmdir  ${D}${prefix}/help \
        ${D}${prefix}/${TARGET_ARCH}/lib \
        ${D}${prefix}/${TARGET_ARCH}/bin \
        ${D}${prefix}/${TARGET_ARCH}/perl \
        ${D}${prefix}/${TARGET_ARCH}
}

# ecmd makefile assumes that dependencies are built from left to right.
PARALLEL_MAKE = ""

# ecmd doesn't have proper library versioning
FILES_${PN}-dev_remove = "${libdir}/lib*.so"
FILES_${PN} += "${libdir}/lib*.so"

RDEPENDS_${PN}-bin = "libecmd"

# This allows someone to easily use ecmd bins, even if you don’t want them.
PACKAGE_BEFORE_PN = "libecmd-bin"
FILES_${PN}-bin += "${bindir}"
