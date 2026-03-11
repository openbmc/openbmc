LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

MCMACHINE:virtclass-mcextend-musl = "qemux86-64"
MCMACHINE:virtclass-mcextend-tiny = "qemux86"
MCIMGTYPE:virtclass-mcextend-musl = "ext4"
MCIMGTYPE:virtclass-mcextend-tiny = "cpio.gz"

MC_DEPLOY_DIR_IMAGE = "${TOPDIR}/tmp-mc-${MCNAME}/deploy/images/${MCMACHINE}"
MC_DEPLOY_IMAGE_BASENAME = "core-image-minimal"

do_install[mcdepends] += "mc::${MCNAME}:core-image-minimal:do_image_complete mc::${MCNAME}:virtual/kernel:do_deploy"

do_install () {
    install -d ${D}/var/lib/machines/${MCNAME}
    install ${MC_DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME_CORE_IMAGE_MINIMAL}.${MCIMGTYPE} ${D}/var/lib/machines/${MCNAME}/${MC_DEPLOY_IMAGE_BASENAME}.${MCIMGTYPE}
    install ${MC_DEPLOY_DIR_IMAGE}/bzImage ${D}/var/lib/machines/${MCNAME}
}

# for IMAGE_LINK_NAME, IMAGE_BASENAME
inherit image-artifact-names

python () {
    mcname = d.getVar('MCNAME')
    if not mcname:
        raise bb.parse.SkipRecipe("Not a multiconfig target")
    multiconfigs = d.getVar('BBMULTICONFIG') or ""
    if mcname not in multiconfigs:
        raise bb.parse.SkipRecipe("multiconfig target %s not enabled" % mcname)

    # these will most likely start with my BPN multiconfig-image-packager, but I want them from core-image-minimal
    # as there is no good way to query core-image-minimal's context lets assume that there are no overrides
    # and that we can just replace IMAGE_BASENAME
    image_link_name = d.getVar('IMAGE_LINK_NAME')
    image_basename = d.getVar('IMAGE_BASENAME')
    machine = d.getVar('MACHINE')
    mcmachine = d.getVar('MCMACHINE')
    image_to_deploy = d.getVar('MC_DEPLOY_IMAGE_BASENAME')
    image_link_name_to_deploy = image_link_name.replace(image_basename, image_to_deploy).replace(machine, mcmachine)
    bb.warn('%s: assuming that "%s" built for "%s" has IMAGE_LINK_NAME "%s"' % (d.getVar('PN'), mcmachine, image_to_deploy, image_link_name_to_deploy))
    d.setVar('IMAGE_LINK_NAME_CORE_IMAGE_MINIMAL', image_link_name_to_deploy)
}

BBCLASSEXTEND = "mcextend:tiny mcextend:musl"
