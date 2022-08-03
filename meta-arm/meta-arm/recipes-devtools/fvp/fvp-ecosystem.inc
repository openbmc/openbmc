require fvp-common.inc

# These need to be set
MODEL ?= "unset"
MODEL_CODE ?= "unset"
PV ?= "unset"

SUMMARY = "Arm Fixed Virtual Platform - ${MODEL} Ecosystem Reference Design"
HOMEPAGE = "https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps"

SRC_URI = "https://developer.arm.com/-/media/Arm%20Developer%20Community/Downloads/OSS/FVP/${MODEL}/${MODEL_CODE}_${PV_URL}.tgz;subdir=${BP}"

UPSTREAM_CHECK_URI = "${HOMEPAGE}"
UPSTREAM_CHECK_REGEX = "${MODEL_CODE}_(?P<pver>(\d+[\.\-_]*)+).tgz"

do_install() {
    mkdir --parents ${D}${FVPDIR} ${D}${bindir}

    ${S}/${MODEL_CODE}.sh \
        --i-agree-to-the-contained-eula \
        --no-interactive \
        --destination ${D}${FVPDIR}

    fvp_link_binaries
}
