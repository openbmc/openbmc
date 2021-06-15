SUMMARY = "Provides a host firmware image"
DESCRIPTION = "Use a bbappend to add the image files"
PR = "r1"

inherit allarch
inherit deploy

HOSTFW_LICENSE ?= "Apache-2.0"
HOSTFW_LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

LICENSE = "${HOSTFW_LICENSE}"
LIC_FILES_CHKSUM = "${HOSTFW_LIC_FILES_CHKSUM}"

do_compile() {
    # The image directory can be used as the source to create a filesystem to
    # add to the BMC image.
    install -d ${B}/image

    # Install an image-hostfw file in the update directory to be included in
    # the code update tarball.
    install -d ${B}/update
}

do_deploy() {
    install -d ${DEPLOYDIR}/hostfw/image
    install -d ${DEPLOYDIR}/hostfw/update
    cp -R --no-dereference --preserve=mode,links ${B}/image/ ${DEPLOYDIR}/hostfw/
    cp -R --no-dereference --preserve=mode,links ${B}/update/ ${DEPLOYDIR}/hostfw/
}

addtask deploy before do_build after do_compile
