LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

MCMACHINE_virtclass-mcextend-musl = "qemux86-64"
MCMACHINE_virtclass-mcextend-tiny = "qemux86"
MCIMGTYPE_virtclass-mcextend-musl = "ext4"
MCIMGTYPE_virtclass-mcextend-tiny = "cpio.gz"

MC_DEPLOY_DIR_IMAGE = "${TOPDIR}/tmp-mc-${MCNAME}/deploy/images/${MCMACHINE}"

do_install[mcdepends] += "mc::${MCNAME}:core-image-minimal:do_image_complete mc::${MCNAME}:virtual/kernel:do_deploy"

do_install () {
    install -d ${D}/var/lib/machines/${MCNAME}
    install ${MC_DEPLOY_DIR_IMAGE}/core-image-minimal-${MCMACHINE}.${MCIMGTYPE} ${D}/var/lib/machines/${MCNAME}/core-image-minimal.${MCIMGTYPE}
    install ${MC_DEPLOY_DIR_IMAGE}/bzImage ${D}/var/lib/machines/${MCNAME}
}

python () {
    mcname = d.getVar('MCNAME')
    if not mcname:
        raise bb.parse.SkipRecipe("Not a multiconfig target")
    multiconfigs = d.getVar('BBMULTICONFIG') or ""
    if mcname not in multiconfigs:
        raise bb.parse.SkipRecipe("multiconfig target %s not enabled" % mcname)
}

BBCLASSEXTEND = "mcextend:tiny mcextend:musl"
