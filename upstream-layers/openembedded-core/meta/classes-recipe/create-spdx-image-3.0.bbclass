#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# SPDX image tasks

SPDX_ROOTFS_PACKAGES = "${SPDXDIR}/rootfs-packages.json"
SPDXIMAGEDEPLOYDIR = "${SPDXDIR}/image-deploy"
SPDXROOTFSDEPLOY = "${SPDXDIR}/rootfs-deploy"

python spdx_collect_rootfs_packages() {
    import json
    from pathlib import Path
    from oe.rootfs import image_list_installed_packages

    root_packages_file = Path(d.getVar("SPDX_ROOTFS_PACKAGES"))

    packages = image_list_installed_packages(d)
    if not packages:
        packages = {}

    root_packages_file.parent.mkdir(parents=True, exist_ok=True)
    with root_packages_file.open("w") as f:
        json.dump(packages, f)
}
ROOTFS_POSTUNINSTALL_COMMAND =+ "spdx_collect_rootfs_packages"

python do_create_rootfs_spdx() {
    import oe.spdx30_tasks
    oe.spdx30_tasks.create_rootfs_spdx(d)
}
addtask do_create_rootfs_spdx after do_rootfs before do_image
SSTATETASKS += "do_create_rootfs_spdx"
do_create_rootfs_spdx[sstate-inputdirs] = "${SPDXROOTFSDEPLOY}"
do_create_rootfs_spdx[sstate-outputdirs] = "${DEPLOY_DIR_SPDX}"
do_create_rootfs_spdx[recrdeptask] += "do_create_spdx do_create_package_spdx"
do_create_rootfs_spdx[cleandirs] += "${SPDXROOTFSDEPLOY}"
do_create_rootfs_spdx[file-checksums] += "${SPDX3_LIB_DEP_FILES}"

python do_create_rootfs_spdx_setscene() {
    sstate_setscene(d)
}
addtask do_create_rootfs_spdx_setscene

python do_create_image_spdx() {
    import oe.spdx30_tasks
    oe.spdx30_tasks.create_image_spdx(d)
}
addtask do_create_image_spdx after do_image_complete do_create_rootfs_spdx before do_build
SSTATETASKS += "do_create_image_spdx"
SSTATE_SKIP_CREATION:task-create-image-spdx = "1"
do_create_image_spdx[sstate-inputdirs] = "${SPDXIMAGEWORK}"
do_create_image_spdx[sstate-outputdirs] = "${DEPLOY_DIR_SPDX}"
do_create_image_spdx[cleandirs] = "${SPDXIMAGEWORK}"
do_create_image_spdx[dirs] = "${SPDXIMAGEWORK}"
do_create_image_spdx[file-checksums] += "${SPDX3_LIB_DEP_FILES}"
do_create_image_spdx[vardeps] += "\
    SPDX_IMAGE_PURPOSE \
    "

python do_create_image_spdx_setscene() {
    sstate_setscene(d)
}
addtask do_create_image_spdx_setscene


python do_create_image_sbom_spdx() {
    import oe.spdx30_tasks
    oe.spdx30_tasks.create_image_sbom_spdx(d)
}
addtask do_create_image_sbom_spdx after do_create_rootfs_spdx do_create_image_spdx before do_build
SSTATETASKS += "do_create_image_sbom_spdx"
SSTATE_SKIP_CREATION:task-create-image-sbom = "1"
do_create_image_sbom_spdx[sstate-inputdirs] = "${SPDXIMAGEDEPLOYDIR}"
do_create_image_sbom_spdx[sstate-outputdirs] = "${DEPLOY_DIR_IMAGE}"
do_create_image_sbom_spdx[stamp-extra-info] = "${MACHINE_ARCH}"
do_create_image_sbom_spdx[cleandirs] = "${SPDXIMAGEDEPLOYDIR}"
do_create_image_sbom_spdx[recrdeptask] += "do_create_spdx do_create_package_spdx"
do_create_image_sbom_spdx[file-checksums] += "${SPDX3_LIB_DEP_FILES}"

python do_create_image_sbom_spdx_setscene() {
    sstate_setscene(d)
}
addtask do_create_image_sbom_spdx_setscene
