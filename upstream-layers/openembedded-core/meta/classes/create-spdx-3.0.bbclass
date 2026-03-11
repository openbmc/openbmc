#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

inherit spdx-common

SPDX_VERSION = "3.0.1"

# The list of SPDX profiles generated documents will conform to
SPDX_PROFILES ?= "core build software simpleLicensing security"

SPDX_INCLUDE_BUILD_VARIABLES ??= "0"
SPDX_INCLUDE_BUILD_VARIABLES[doc] = "If set to '1', the bitbake variables for a \
    recipe will be included in the Build object. This will most likely result \
    in non-reproducible SPDX output"

SPDX_INCLUDE_BITBAKE_PARENT_BUILD ??= "0"
SPDX_INCLUDE_BITBAKE_PARENT_BUILD[doc] = "Report the parent invocation of bitbake \
    for each Build object. This allows you to know who invoked bitbake to perform \
    a build, but will result in non-reproducible SPDX output."

SPDX_PACKAGE_ADDITIONAL_PURPOSE ?= ""
SPDX_PACKAGE_ADDITIONAL_PURPOSE[doc] = "The list of additional purposes to assign to \
    the generated packages for a recipe. The primary purpose is always `install`. \
    Packages overrides are allowed to override the additional purposes for \
    individual packages."

SPDX_IMAGE_PURPOSE ?= "filesystemImage"
SPDX_IMAGE_PURPOSE[doc] = "The list of purposes to assign to the generated images. \
    The first listed item will be the Primary Purpose and all additional items will \
    be added as additional purposes"

SPDX_SDK_PURPOSE ?= "install"
SPDX_SDK_PURPOSE[doc] = "The list of purposes to assign to the generate SDK installer. \
    The first listed item will be the Primary Purpose and all additional items will \
    be added as additional purposes"

SPDX_INCLUDE_VEX ??= "current"
SPDX_INCLUDE_VEX[doc] = "Controls what VEX information is in the output. Set to \
    'none' to disable all VEX data. Set to 'current' to only include VEX data \
    for vulnerabilities not already fixed in the upstream source code \
    (recommended). Set  to 'all' to get all known historical vulnerabilities, \
    including those already fixed upstream (warning: This can be large and \
    slow)."

SPDX_INCLUDE_TIMESTAMPS ?= "0"
SPDX_INCLUDE_TIMESTAMPS[doc] = "Include time stamps in SPDX output. This is \
    useful if you want to know when artifacts were produced and when builds \
    occurred, but will result in non-reproducible SPDX output"

SPDX_IMPORTS ??= ""
SPDX_IMPORTS[doc] = "SPDX_IMPORTS is the base variable that describes how to \
    reference external SPDX ids. Each import is defined as a key in this \
    variable with a suffix to describe to as a suffix to look up more \
    information about the import. Each key can have the following variables: \
        SPDX_IMPORTS_<key>_spdxid: The Fully qualified SPDX ID of the object \
        SPDX_IMPORTS_<key>_uri: The URI where the SPDX Document that contains \
            the external object can be found. Optional but recommended \
        SPDX_IMPORTS_<key>_hash_<hash>: The Checksum of the SPDX Document that \
            contains the External ID. <hash> must be one the valid SPDX hashing \
            algorithms, as described by the HashAlgorithm vocabulary in the\
            SPDX 3 spec. Optional but recommended"

# Agents
#   Bitbake variables can be used to describe an SPDX Agent that may be used
#   during the build. An Agent is specified using a set of variables which all
#   start with some common base name:
#
#   <BASE>_name: The name of the Agent (required)
#   <BASE>_type: The type of Agent. Must be one of "person", "organization",
#       "software", or "agent" (the default if not specified)
#   <BASE>_comment: The comment for the Agent (optional)
#   <BASE>_id_<ID>: And External Identifier for the Agent. <ID> must be a valid
#       ExternalIdentifierType from the SPDX 3 spec. Commonly, an E-mail address
#       can be specified with <BASE>_id_email
#
#   Alternatively, an Agent can be an external reference by referencing a key
#   in SPDX_IMPORTS like so:
#
#   <BASE>_import = "<key>"
#
#   Finally, the same agent described by another set of agent variables can be
#   referenced by specifying the basename of the variable that should be
#   referenced:
#
#   SPDX_PACKAGE_SUPPLIER_ref = "SPDX_AUTHORS_openembedded"

SPDX_AUTHORS ??= "openembedded"
SPDX_AUTHORS[doc] = "A space separated list of the document authors. Each item \
    is used to name a base variable like SPDX_AUTHORS_<AUTHOR> that \
    describes the author."

SPDX_AUTHORS_openembedded_name = "OpenEmbedded"
SPDX_AUTHORS_openembedded_type = "organization"

SPDX_BUILD_HOST[doc] = "The base variable name to describe the build host on \
    which a build is running. Must be an SPDX_IMPORTS key. Requires \
    SPDX_INCLUDE_BITBAKE_PARENT_BUILD. NOTE: Setting this will result in \
    non-reproducible SPDX output"

SPDX_INVOKED_BY[doc] = "The base variable name to describe the Agent that \
    invoked the build, which builds will link to if specified. Requires \
    SPDX_INCLUDE_BITBAKE_PARENT_BUILD. NOTE: Setting this will likely result in \
    non-reproducible SPDX output"

SPDX_ON_BEHALF_OF[doc] = "The base variable name to describe the Agent on who's \
    behalf the invoking Agent (SPDX_INVOKED_BY) is running the build. Requires \
    SPDX_INCLUDE_BITBAKE_PARENT_BUILD. NOTE: Setting this will likely result in \
    non-reproducible SPDX output"

SPDX_PACKAGE_SUPPLIER[doc] = "The base variable name to describe the Agent who \
    is supplying artifacts produced by the build"

SPDX_PACKAGE_VERSION ??= "${PV}"
SPDX_PACKAGE_VERSION[doc] = "The version of a package, software_packageVersion \
    in software_Package"

SPDX_PACKAGE_URL ??= ""
SPDX_PACKAGE_URL[doc] = "Provides a place for the SPDX data creator to record \
the package URL string (in accordance with the Package URL specification) for \
a software Package."

IMAGE_CLASSES:append = " create-spdx-image-3.0"
SDK_CLASSES += "create-spdx-sdk-3.0"

oe.spdx30_tasks.set_timestamp_now[vardepsexclude] = "SPDX_INCLUDE_TIMESTAMPS"
oe.spdx30_tasks.get_package_sources_from_debug[vardepsexclude] += "STAGING_KERNEL_DIR"
oe.spdx30_tasks.collect_dep_objsets[vardepsexclude] = "SPDX_MULTILIB_SSTATE_ARCHS"


# SPDX library code makes heavy use of classes, which bitbake cannot easily
# parse out dependencies. As such, the library code files that make use of
# classes are explicitly added as file checksum dependencies.
SPDX3_LIB_DEP_FILES = "\
    ${COREBASE}/meta/lib/oe/sbom30.py:True \
    ${COREBASE}/meta/lib/oe/spdx30.py:True \
    "

python do_create_spdx() {
    import oe.spdx30_tasks
    oe.spdx30_tasks.create_spdx(d)
}
do_create_spdx[vardeps] += "\
    SPDX_INCLUDE_BITBAKE_PARENT_BUILD \
    SPDX_PACKAGE_ADDITIONAL_PURPOSE \
    SPDX_PROFILES \
    SPDX_NAMESPACE_PREFIX \
    SPDX_UUID_NAMESPACE \
    "

addtask do_create_spdx after \
    do_collect_spdx_deps \
    do_deploy_source_date_epoch \
    do_populate_sysroot do_package do_packagedata \
    before do_populate_sdk do_populate_sdk_ext do_build do_rm_work

SSTATETASKS += "do_create_spdx"
do_create_spdx[sstate-inputdirs] = "${SPDXDEPLOY}"
do_create_spdx[sstate-outputdirs] = "${DEPLOY_DIR_SPDX}"
do_create_spdx[file-checksums] += "${SPDX3_LIB_DEP_FILES}"

python do_create_spdx_setscene () {
    sstate_setscene(d)
}
addtask do_create_spdx_setscene

do_create_spdx[dirs] = "${SPDXWORK}"
do_create_spdx[cleandirs] = "${SPDXDEPLOY} ${SPDXWORK}"
do_create_spdx[depends] += " \
    ${PATCHDEPENDENCY} \
    ${@create_spdx_source_deps(d)} \
"

python do_create_package_spdx() {
    import oe.spdx30_tasks
    oe.spdx30_tasks.create_package_spdx(d)
}
oe.spdx30_tasks.create_package_spdx[vardepsexclude] = "OVERRIDES"

addtask do_create_package_spdx after do_create_spdx before do_build do_rm_work
SSTATETASKS += "do_create_package_spdx"
do_create_package_spdx[sstate-inputdirs] = "${SPDXRUNTIMEDEPLOY}"
do_create_package_spdx[sstate-outputdirs] = "${DEPLOY_DIR_SPDX}"
do_create_package_spdx[file-checksums] += "${SPDX3_LIB_DEP_FILES}"

python do_create_package_spdx_setscene () {
    sstate_setscene(d)
}
addtask do_create_package_spdx_setscene

do_create_package_spdx[dirs] = "${SPDXRUNTIMEDEPLOY}"
do_create_package_spdx[cleandirs] = "${SPDXRUNTIMEDEPLOY}"
do_create_package_spdx[rdeptask] = "do_create_spdx"

python spdx30_build_started_handler () {
    import oe.spdx30_tasks
    d = e.data.createCopy()
    oe.spdx30_tasks.write_bitbake_spdx(d)
}

addhandler spdx30_build_started_handler
spdx30_build_started_handler[eventmask] = "bb.event.BuildStarted"

