#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

DEPLOYDIR = "${WORKDIR}/deploy-${PN}"
SSTATETASKS += "do_deploy"
SPDX_DEPLOY_TASKS += "do_deploy"
SPDX_DEPLOY_ARTIFACTS_DIR:task-deploy = "${DEPLOYDIR}"
do_deploy[sstate-inputdirs] = "${DEPLOYDIR}"
do_deploy[sstate-outputdirs] = "${DEPLOY_DIR_IMAGE}"

python do_deploy_setscene () {
    sstate_setscene(d)
}
addtask do_deploy_setscene
do_deploy[dirs] = "${B}"
do_deploy[cleandirs] = "${DEPLOYDIR}"
do_deploy[stamp-extra-info] = "${MACHINE_ARCH}"
