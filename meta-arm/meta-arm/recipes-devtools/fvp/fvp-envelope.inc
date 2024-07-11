require fvp-common.inc

HOMEPAGE = "https://developer.arm.com/Tools%20and%20Software/Fixed%20Virtual%20Platforms"

SRC_URI = "https://developer.arm.com/-/media/Files/downloads/ecosystem-models/${PV_URL_SHORT}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"

UPSTREAM_CHECK_URI = "${HOMEPAGE}"
UPSTREAM_CHECK_REGEX = "${MODEL_CODE}_(?P<pver>(\d+[\.\-_]*)+).tgz"

do_install() {
    mkdir --parents ${D}${FVPDIR} ${D}${bindir}

    cp --archive --no-preserve=ownership ${S}/*_pkg/* ${D}${FVPDIR}/

    fvp_link_binaries
}
