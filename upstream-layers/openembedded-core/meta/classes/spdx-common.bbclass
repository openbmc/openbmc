#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

SPDX_VERSION ??= ""
DEPLOY_DIR_SPDX ??= "${DEPLOY_DIR}/spdx/${SPDX_VERSION}"

# The product name that the CVE database uses.  Defaults to BPN, but may need to
# be overriden per recipe (for example tiff.bb sets CVE_PRODUCT=libtiff).
CVE_PRODUCT ??= "${BPN}"
CVE_VERSION ??= "${PV}"

SPDXDIR ??= "${WORKDIR}/spdx/${SPDX_VERSION}"
SPDXDEPLOY = "${SPDXDIR}/deploy"
SPDXWORK = "${SPDXDIR}/work"
SPDXIMAGEWORK = "${SPDXDIR}/image-work"
SPDXSDKWORK = "${SPDXDIR}/sdk-work"
SPDXSDKEXTWORK = "${SPDXDIR}/sdk-ext-work"
SPDXDEPS = "${SPDXDIR}/deps.json"

SPDX_TOOL_NAME ??= "oe-spdx-creator"
SPDX_TOOL_VERSION ??= "1.0"

SPDXRUNTIMEDEPLOY = "${SPDXDIR}/runtime-deploy"

SPDX_INCLUDE_SOURCES ??= "0"
SPDX_INCLUDE_COMPILED_SOURCES ??= "0"

SPDX_UUID_NAMESPACE ??= "sbom.openembedded.org"
SPDX_NAMESPACE_PREFIX ??= "http://spdx.org/spdxdocs"
SPDX_PRETTY ??= "0"

SPDX_LICENSES ??= "${COREBASE}/meta/files/spdx-licenses.json"

SPDX_CUSTOM_ANNOTATION_VARS ??= ""

SPDX_CONCLUDED_LICENSE ??= ""
SPDX_CONCLUDED_LICENSE[doc] = "The license concluded by manual or external \
    license analysis. This should only be set when explicit license analysis \
    (manual review or external scanning tools) has been performed and a license \
    conclusion has been reached. When unset or empty, no concluded license is \
    included in the SBOM, indicating that no license analysis was performed. \
    When differences from the declared LICENSE are found, the preferred approach \
    is to correct the LICENSE field in the recipe and contribute the fix upstream \
    to OpenEmbedded. Use this variable locally only when upstream contribution is \
    not immediately possible or when the license conclusion is environment-specific. \
    Supports package-specific overrides via SPDX_CONCLUDED_LICENSE:${PN}. \
    This allows tracking license analysis results in SBOM while maintaining recipe \
    LICENSE field for build compatibility. \
    Example: SPDX_CONCLUDED_LICENSE = 'MIT & Apache-2.0' or \
    SPDX_CONCLUDED_LICENSE:${PN} = 'MIT & Apache-2.0'"

SPDX_MULTILIB_SSTATE_ARCHS ??= "${SSTATE_ARCHS}"

python () {
    from oe.cve_check import extend_cve_status
    extend_cve_status(d)
    if d.getVar("SPDX_INCLUDE_COMPILED_SOURCES") == "1":
        d.setVar("SPDX_INCLUDE_SOURCES", "1")
}

def create_spdx_source_deps(d):
    import oe.spdx_common

    deps = []
    if d.getVar("SPDX_INCLUDE_SOURCES") == "1":
        pn = d.getVar('PN')
        # do_unpack is a hack for now; we only need it to get the
        # dependencies do_unpack already has so we can extract the source
        # ourselves
        if oe.spdx_common.has_task(d, "do_unpack"):
            deps.append("%s:do_unpack" % pn)

        if oe.spdx_common.is_work_shared_spdx(d) and \
           oe.spdx_common.process_sources(d):
            # For kernel source code
            if oe.spdx_common.has_task(d, "do_shared_workdir"):
                deps.append("%s:do_shared_workdir" % pn)
            elif d.getVar('S') == d.getVar('STAGING_KERNEL_DIR'):
                deps.append("virtual/kernel:do_shared_workdir")

            # For gcc-source-${PV} source code
            if oe.spdx_common.has_task(d, "do_preconfigure"):
                deps.append("%s:do_preconfigure" % pn)
            elif oe.spdx_common.has_task(d, "do_patch"):
                deps.append("%s:do_patch" % pn)
            # For gcc-cross-x86_64 source code
            elif oe.spdx_common.has_task(d, "do_configure"):
                deps.append("%s:do_configure" % pn)

    return " ".join(deps)


python do_collect_spdx_deps() {
    # This task calculates the build time dependencies of the recipe, and is
    # required because while a task can deptask on itself, those dependencies
    # do not show up in BB_TASKDEPDATA. To work around that, this task does the
    # deptask on do_create_spdx and writes out the dependencies it finds, then
    # do_create_spdx reads in the found dependencies when writing the actual
    # SPDX document
    import json
    import oe.spdx_common
    from pathlib import Path

    spdx_deps_file = Path(d.getVar("SPDXDEPS"))

    deps = oe.spdx_common.collect_direct_deps(d, "do_create_spdx")

    with spdx_deps_file.open("w") as f:
        json.dump(deps, f)
}
# NOTE: depending on do_unpack is a hack that is necessary to get it's dependencies for archive the source
addtask do_collect_spdx_deps after do_unpack
do_collect_spdx_deps[depends] += "${PATCHDEPENDENCY}"
do_collect_spdx_deps[deptask] = "do_create_spdx"
do_collect_spdx_deps[dirs] = "${SPDXDIR}"

oe.spdx_common.collect_direct_deps[vardepsexclude] += "BB_TASKDEPDATA"
oe.spdx_common.collect_direct_deps[vardeps] += "DEPENDS"
oe.spdx_common.collect_package_providers[vardepsexclude] += "BB_TASKDEPDATA"
oe.spdx_common.get_patched_src[vardepsexclude] += "STAGING_KERNEL_DIR"
