#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# SPDX SDK tasks

do_populate_sdk[recrdeptask] += "do_create_spdx do_create_package_spdx"
do_populate_sdk[cleandirs] += "${SPDXSDKWORK}"
do_populate_sdk[postfuncs] += "sdk_create_sbom"
do_populate_sdk[file-checksums] += "${SPDX3_LIB_DEP_FILES}"
POPULATE_SDK_POST_HOST_COMMAND:append:task-populate-sdk = " sdk_host_create_spdx"
POPULATE_SDK_POST_TARGET_COMMAND:append:task-populate-sdk = " sdk_target_create_spdx"

do_populate_sdk_ext[recrdeptask] += "do_create_spdx do_create_package_spdx"
do_populate_sdk_ext[cleandirs] += "${SPDXSDKEXTWORK}"
do_populate_sdk_ext[postfuncs] += "sdk_ext_create_sbom"
do_populate_sdk_ext[file-checksums] += "${SPDX3_LIB_DEP_FILES}"
POPULATE_SDK_POST_HOST_COMMAND:append:task-populate-sdk-ext = " sdk_ext_host_create_spdx"
POPULATE_SDK_POST_TARGET_COMMAND:append:task-populate-sdk-ext = " sdk_ext_target_create_spdx"

python sdk_host_create_spdx() {
    from pathlib import Path
    import oe.spdx30_tasks
    spdx_work_dir = Path(d.getVar('SPDXSDKWORK'))

    oe.spdx30_tasks.sdk_create_spdx(d, "host", spdx_work_dir, d.getVar("TOOLCHAIN_OUTPUTNAME"))
}

python sdk_target_create_spdx() {
    from pathlib import Path
    import oe.spdx30_tasks
    spdx_work_dir = Path(d.getVar('SPDXSDKWORK'))

    oe.spdx30_tasks.sdk_create_spdx(d, "target", spdx_work_dir, d.getVar("TOOLCHAIN_OUTPUTNAME"))
}

python sdk_ext_host_create_spdx() {
    from pathlib import Path
    import oe.spdx30_tasks
    spdx_work_dir = Path(d.getVar('SPDXSDKEXTWORK'))

    # TODO: This doesn't seem to work
    oe.spdx30_tasks.sdk_create_spdx(d, "host", spdx_work_dir, d.getVar("TOOLCHAINEXT_OUTPUTNAME"))
}

python sdk_ext_target_create_spdx() {
    from pathlib import Path
    import oe.spdx30_tasks
    spdx_work_dir = Path(d.getVar('SPDXSDKEXTWORK'))

    # TODO: This doesn't seem to work
    oe.spdx30_tasks.sdk_create_spdx(d, "target", spdx_work_dir, d.getVar("TOOLCHAINEXT_OUTPUTNAME"))
}


python sdk_create_sbom() {
    from pathlib import Path
    import oe.spdx30_tasks
    sdk_deploydir = Path(d.getVar("SDKDEPLOYDIR"))
    spdx_work_dir = Path(d.getVar('SPDXSDKWORK'))

    oe.spdx30_tasks.create_sdk_sbom(d, sdk_deploydir, spdx_work_dir, d.getVar("TOOLCHAIN_OUTPUTNAME"))
}

python sdk_ext_create_sbom() {
    from pathlib import Path
    import oe.spdx30_tasks
    sdk_deploydir = Path(d.getVar("SDKEXTDEPLOYDIR"))
    spdx_work_dir = Path(d.getVar('SPDXSDKEXTWORK'))

    oe.spdx30_tasks.create_sdk_sbom(d, sdk_deploydir, spdx_work_dir, d.getVar("TOOLCHAINEXT_OUTPUTNAME"))
}

