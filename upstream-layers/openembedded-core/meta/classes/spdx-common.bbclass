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

SPDXRECIPEDEPLOY = "${SPDXDIR}/recipe-deploy"
SPDXRUNTIMEDEPLOY = "${SPDXDIR}/runtime-deploy"
SPDXRECIPESBOMDEPLOY = "${SPDXDIR}/recipes-bom-deploy"

SPDX_INCLUDE_SOURCES ??= "0"
SPDX_INCLUDE_SOURCES[doc] = "If set to '1', include source code files in the \
    SPDX output. This will create File objects for all source files used during \
    the build. Note: This significantly increases SBOM size and generation time."

SPDX_INCLUDE_COMPILED_SOURCES ??= "0"
SPDX_INCLUDE_COMPILED_SOURCES[doc] = "If set to '1', include compiled source \
    files (object files, etc.) in the SPDX output. This automatically enables \
    SPDX_INCLUDE_SOURCES. Note: This significantly increases SBOM size."

SPDX_UUID_NAMESPACE ??= "sbom.openembedded.org"
SPDX_UUID_NAMESPACE[doc] = "The namespace used for generating UUIDs in SPDX \
    documents. This should be a domain name or unique identifier for your \
    organization to ensure globally unique SPDX IDs."

SPDX_NAMESPACE_PREFIX ??= "http://spdx.org/spdxdocs"
SPDX_NAMESPACE_PREFIX[doc] = "The URI prefix used for SPDX document namespaces. \
    Combined with other identifiers to create unique document URIs."

SPDX_PRETTY ??= "0"
SPDX_PRETTY[doc] = "If set to '1', generate human-readable formatted JSON output \
    with indentation and line breaks. If '0', generate compact JSON output. \
    Pretty formatting makes files larger but easier to read."

SPDX_LICENSES ??= "${COREBASE}/meta/files/spdx-licenses.json"
SPDX_LICENSES[doc] = "Path to the JSON file containing SPDX license identifier \
    mappings. This file maps common license names to official SPDX license \
    identifiers."

SPDX_CUSTOM_ANNOTATION_VARS ??= ""
SPDX_CUSTOM_ANNOTATION_VARS[doc] = "Space-separated list of variable names whose \
    values will be added as custom annotations to SPDX documents. Each variable's \
    name and value will be recorded as an annotation for traceability."

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
SPDX_MULTILIB_SSTATE_ARCHS[doc] = "The list of sstate architectures to consider \
    when collecting SPDX dependencies. This includes multilib architectures when \
    multilib is enabled. Defaults to SSTATE_ARCHS."

SPDX_FILE_EXCLUDE_PATTERNS ??= ""
SPDX_FILE_EXCLUDE_PATTERNS[doc] = "Space-separated list of Python regular \
    expressions to exclude files from SPDX output. Files whose paths match \
    any pattern (via re.search) will be filtered out. Defaults to empty \
    (no filtering). Example: \
    SPDX_FILE_EXCLUDE_PATTERNS = '\\.patch$ \\.diff$ /test/ \\.pyc$ \\.o$'"

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
            # For gcc-cross-x86_64 source code
            elif oe.spdx_common.has_task(d, "do_configure"):
                deps.append("%s:do_configure" % pn)

    return " ".join(deps)


oe.spdx_common.collect_direct_deps[vardepsexclude] += "BB_TASKDEPDATA"
oe.spdx_common.collect_direct_deps[vardeps] += "DEPENDS"
oe.spdx_common.collect_package_providers[vardepsexclude] += "BB_TASKDEPDATA"
oe.spdx_common.get_patched_src[vardepsexclude] += "STAGING_KERNEL_DIR"
