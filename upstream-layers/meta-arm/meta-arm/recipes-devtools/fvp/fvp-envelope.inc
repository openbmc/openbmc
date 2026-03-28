require fvp-common.inc

HOMEPAGE = "https://developer.arm.com/Tools%20and%20Software/Fixed%20Virtual%20Platforms/Arm%20Architecture%20FVPs"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Architecture/${PV_URL_SHORT}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tar.gz;subdir=${BP};name=fvp-${HOST_ARCH}"

UPSTREAM_CHECK_URI = "${HOMEPAGE}"
UPSTREAM_CHECK_REGEX = "${MODEL_CODE}_(?P<pver>(\d+[\.\-_]*)+).tar.gz"

FVP_ARCH:aarch64 = "Linux_armv8"
FVP_ARCH:x86-64 = "Linux_x86"

# This is needed because all of the files are now included in the first
# dir in the tarball, instead of having the license files split out.
S = "${UNPACKDIR}/${BP}/${MODEL_CODE}_${VERSION}_${BUILD}"

# Keeping this separate from do_install below in case it can be moved
# out when other FVPs bump to newer versions and this because needed more commonly
fvp_link_binaries() {
    DIR="${D}${FVPDIR}/bin/"

    stat $DIR/FVP_* >/dev/null 2>&1 || bbfatal Cannot find FVP binaries in $DIR

    for FVP in $DIR/FVP_*; do
        ln -rs $FVP ${D}${bindir}/$(basename $FVP)
    done
    # But not the .so files too
    rm -f ${D}${bindir}/*.so
}

# This is needed because the FVP Base doesn't have an install script like others
do_install() {
    mkdir --parents ${D}${FVPDIR} ${D}${bindir}

    cp --archive --no-preserve=ownership ${S}/* ${D}${FVPDIR}/

    fvp_link_binaries
}
