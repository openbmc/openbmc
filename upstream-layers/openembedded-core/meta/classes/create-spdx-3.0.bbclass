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

SPDX_INCLUDE_KERNEL_CONFIG ??= "0"
SPDX_INCLUDE_KERNEL_CONFIG[doc] = "If set to '1', the .config file for the kernel will be parsed \
and each CONFIG_* value will be included in the Build.build_parameter list as DictionaryEntry \
items. Set to '0' to disable exporting kernel configuration to improve performance or reduce \
SPDX document size."

SPDX_INCLUDE_PACKAGECONFIG ??= "0"
SPDX_INCLUDE_PACKAGECONFIG[doc] = "If set to '1', each PACKAGECONFIG feature is recorded in the \
build_Build object's build_parameter list as a DictionaryEntry with key \
'PACKAGECONFIG:<feature>' and value 'enabled' or 'disabled'"

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

SPDX_IMAGE_SUPPLIER[doc] = "The base variable name to describe the Agent who \
    is supplying the image SBOM. The supplier will be set on all root elements \
    of the image SBOM using the suppliedBy property. If not set, no supplier \
    information will be added to the image SBOM."

SPDX_SDK_SUPPLIER[doc] = "The base variable name to describe the Agent who \
    is supplying the SDK SBOM. The supplier will be set on all root elements \
    of the SDK SBOM using the suppliedBy property. If not set, no supplier \
    information will be added to the SDK SBOM."

SPDX_PACKAGE_VERSION ??= "${PV}"
SPDX_PACKAGE_VERSION[doc] = "The version of a package, software_packageVersion \
    in software_Package"

SPDX_PACKAGE_URL ??= ""
SPDX_PACKAGE_URL[doc] = "Provides a place for the SPDX data creator to record \
the package URL string (in accordance with the Package URL specification) for \
a software Package. DEPRECATED - use SPDX_PACKAGE_URLS instead"

SPDX_PACKAGE_URLS ?= "${SPDX_PACKAGE_URL} ${@oe.purl.get_base_purl(d)}"
SPDX_PACKAGE_URLS[doc] = "A space separated list of Package URLs (purls) for \
    the software Package. The first item in this list will be listed as the \
    packageUrl property of the packages, and all purls (including the first \
    one) will be listed as external references. The default value is an auto \
    generated pkg:yocto purl based on the recipe name, version, and layer name. \
    Override this variable to replace the default, otherwise append or prepend \
    to add additional purls."

SPDX_RECIPE_SBOM_NAME ?= "${PN}-recipe-sbom"
SPDX_RECIPE_SBOM_NAME[doc] = "The name of output recipe SBoM when using \
    create_recipe_sbom"

SPDX_GIT_PURL_MAPPINGS ??= ""
SPDX_GIT_PURL_MAPPINGS[doc] = "A space separated list of domain:purl_type \
    mappings to configure PURL generation for Git source downloads. \
    For example, 'gitlab.example.com:pkg:gitlab' maps repositories hosted \
    on gitlab.example.com to the pkg:gitlab PURL type. \
    github.com is always mapped to pkg:github by default."

SPDX_DEPLOY_TASKS ?= ""
SPDX_DEPLOY_TASKS[doc] = "A space separated list of sstate tasks that produce \
    deployed output (usually written to DEPLOY_DIR_IMAGE). Tasks in this list \
    will produce SPDX documents that describe the deployed output. Items in \
    list have the format 'TASK(:FUNCTION)' where 'TASK' is the sstate task \
    (which must start with 'do_deploy'), and 'FUNCTION' is the optional \
    function to call to produce the deploy SPDX. If 'FUNCTION' is omitted \
    a default function is provided that uses SPDX_DEPLOY_ARTIFACTS and \
    SPDX_DEPLOY_ARTIFACTS_DIR to configure what deploy artifacts it describes \
    in the SPDX output.\
    \
    Dependencies of deploy tasks that produce SPDX data will be automatically \
    linked in as a build time dependency of the deploy task's SBoM. (for \
    example, if one do_deploy depends on another recipes do_deploy, this will \
    be reflected in the SPDX data)."

SPDX_DEPLOY_SBOM ??= "1"
SPDX_DEPLOY_SBOM[doc] = "If '1' (the default) a task named \
    do_create_deploy_sbom will be created to automatically collect all deploy \
    SPDX documents (from SPDX_DEPLOY_TASKS) and combine them into a SBoM \
    which will be placed in DEPLOY_DIR_IMAGE. This task is added as a \
    dependency of do_build, so it will run whenever the recipe is built \
    directly. The name of the document can be set with SPDX_DEPLOY_SBOM_NAME. \
    If the recipe already has another task to create an SBoM, this should be \
    set to '0' to prevent multiple SBoMs from being created."

SPDX_DEPLOY_SBOM_NAME ?= "${PN}-deploy-sbom"
SPDX_DEPLOY_SBOM_NAME[doc] = "The name of the output deploy SBoM when using \
    create_deploy_sbom"

SPDX_DEPLOY_ARTIFACTS = "AUTO"
SPDX_DEPLOY_ARITFACTS[doc] = "A space separated list of deployed artifacts, \
    relative to SPDX_DEPLOY_ARTIFACTS_DIR that should be included in the SBoM. \
    If 'AUTO' (the default), all files in SPDX_DEPLOY_ARITFACTS_DIR will be \
    added. A :task- override *must* be used to set this value so that it is \
    scoped to a specific task"

SPDX_DEPLOY_ARTIFACTS_DIR = ""
SPDX_DEPLOY_ARTIFACTS_DIR[doc] = "The directory recipe specific directory \
    where artifacts are deployed for staging to sstate (e.g. for do_deploy, \
    this is DEPLOY_DIR). A :task- override *must* be used to set this value so \
    that it is scoped to a specific task."

SPDX_DEPLOY_ALLOW_EMPTY = "0"
SPDX_DEPLOY_ALLOW_EMPTY[doc] = "If set to '1', SPDX deploy task is allowed to \
    have no output artifacts. A SPDX document will still be produced, but it \
    will have no output files. This must always be set with a task override, \
    e.g.: SPDX_DEPLOY_ALLOW_EMPTY:task-deploy = '1'"

IMAGE_CLASSES:append = " create-spdx-image-3.0"
SDK_CLASSES += "create-spdx-sdk-3.0"

oe.spdx30_tasks.set_timestamp_now[vardepsexclude] = "SPDX_INCLUDE_TIMESTAMPS"
oe.spdx30_tasks.get_package_sources_from_debug[vardepsexclude] += "STAGING_KERNEL_DIR"
oe.spdx30_tasks.collect_dep_objsets[vardepsexclude] = "SPDX_MULTILIB_SSTATE_ARCHS"


# SPDX library code makes heavy use of classes, which bitbake cannot easily
# parse out dependencies. As such, the library code files that make use of
# classes are explicitly added as file checksum dependencies.
SPDX3_DEP_FILES = "\
    ${COREBASE}/meta/lib/oe/sbom30.py:True \
    ${COREBASE}/meta/lib/oe/spdx30/model.py:True \
    ${SPDX_LICENSES}:True \
    "

SPDX3_VAR_DEPS = "\
    SPDX_INCLUDE_BITBAKE_PARENT_BUILD \
    SPDX_PACKAGE_ADDITIONAL_PURPOSE \
    SPDX_PROFILES \
    SPDX_NAMESPACE_PREFIX \
    SPDX_UUID_NAMESPACE \
    SPDX_INCLUDE_SOURCES \
    SPDX_INCLUDE_COMPILED_SOURCES \
    "

python do_create_recipe_spdx() {
    import oe.spdx30_tasks
    oe.spdx30_tasks.create_recipe_spdx(d)
}
addtask do_create_recipe_spdx

SSTATETASKS += "do_create_recipe_spdx"
do_create_recipe_spdx[sstate-inputdirs] = "${SPDXRECIPEDEPLOY}"
do_create_recipe_spdx[sstate-outputdirs] = "${DEPLOY_DIR_SPDX}"
do_create_recipe_spdx[file-checksums] += "${SPDX3_DEP_FILES}"
do_create_recipe_spdx[cleandirs] = "${SPDXRECIPEDEPLOY}"
do_create_recipe_spdx[deptask] += "do_create_recipe_spdx"
do_create_recipe_spdx[vardeps] += "${SPDX3_VAR_DEPS}"
do_create_recipe_spdx[file-checksums] = "${@bb.fetch.get_checksum_file_list(d)}"

python do_create_recipe_spdx_setscene () {
    sstate_setscene(d)
}
addtask do_create_recipe_spdx_setscene

python do_create_spdx() {
    import oe.spdx30_tasks
    oe.spdx30_tasks.create_spdx(d)
}
addtask do_create_spdx after \
    do_unpack \
    do_patch \
    do_create_recipe_spdx \
    do_deploy_source_date_epoch \
    do_populate_sysroot do_package do_packagedata \
    before do_populate_sdk do_populate_sdk_ext do_build do_rm_work

SSTATETASKS += "do_create_spdx"
do_create_spdx[sstate-inputdirs] = "${SPDXDEPLOY}"
do_create_spdx[sstate-outputdirs] = "${DEPLOY_DIR_SPDX}"
do_create_spdx[file-checksums] += "${SPDX3_DEP_FILES}"
do_create_spdx[deptask] += "do_create_spdx"
do_create_spdx[dirs] = "${SPDXWORK}"
do_create_spdx[cleandirs] = "${SPDXDEPLOY} ${SPDXWORK}"
do_create_spdx[depends] += " \
    ${PATCHDEPENDENCY} \
    ${@create_spdx_source_deps(d)} \
"
do_create_spdx[vardeps] += "${SPDX3_VAR_DEPS}"

python do_create_spdx_setscene () {
    sstate_setscene(d)
}
addtask do_create_spdx_setscene

python do_create_package_spdx() {
    import oe.spdx30_tasks
    oe.spdx30_tasks.create_package_spdx(d)
}
oe.spdx30_tasks.create_package_spdx[vardepsexclude] = "OVERRIDES"

addtask do_create_package_spdx after do_create_spdx before do_build do_rm_work
SSTATETASKS += "do_create_package_spdx"
do_create_package_spdx[sstate-inputdirs] = "${SPDXRUNTIMEDEPLOY}"
do_create_package_spdx[sstate-outputdirs] = "${DEPLOY_DIR_SPDX}"
do_create_package_spdx[file-checksums] += "${SPDX3_DEP_FILES}"
do_create_package_spdx[dirs] = "${SPDXRUNTIMEDEPLOY}"
do_create_package_spdx[cleandirs] = "${SPDXRUNTIMEDEPLOY}"
do_create_package_spdx[deptask] = "do_create_spdx"
do_create_package_spdx[rdeptask] = "do_create_spdx"
do_create_package_spdx[vardeps] += "${SPDX3_VAR_DEPS}"

python do_create_package_spdx_setscene () {
    sstate_setscene(d)
}
addtask do_create_package_spdx_setscene

addtask do_create_recipe_sbom after create_recipe_spdx
python do_create_recipe_sbom() {
    import oe.spdx30_tasks
    from pathlib import Path
    deploydir = Path(d.getVar("SPDXRECIPESBOMDEPLOY"))
    oe.spdx30_tasks.create_recipe_sbom(d, deploydir)
}

SSTATETASKS += "do_create_recipe_sbom"
do_create_recipe_sbom[recrdeptask] = "do_create_recipe_spdx"
do_create_recipe_sbom[nostamp] = "1"
do_create_recipe_sbom[sstate-inputdirs] = "${SPDXRECIPESBOMDEPLOY}"
do_create_recipe_sbom[sstate-outputdirs] = "${DEPLOY_DIR_IMAGE}"
do_create_recipe_sbom[file-checksums] += "${SPDX3_DEP_FILES}"
do_create_recipe_sbom[cleandirs] = "${SPDXRECIPESBOMDEPLOY}"
do_create_recipe_sbom[vardeps] += "${SPDX3_VAR_DEPS}"

python do_create_recipe_sbom_setscene () {
    sstate_setscene(d)
}
addtask do_create_recipe_sbom_setscene

python spdx30_build_started_handler () {
    import oe.spdx30_tasks
    d = e.data.createCopy()
    oe.spdx30_tasks.write_bitbake_spdx(d)
}

addhandler spdx30_build_started_handler
spdx30_build_started_handler[eventmask] = "bb.event.BuildStarted"

python create_deploy_spdx() {
    import oe.spdx30_tasks
    from pathlib import Path
    current_task = "do_" + d.getVar("BB_CURRENTTASK")

    spdxdeploydir = Path(d.getVar("SPDXDIR") + "/deploy-" + current_task)

    artifactsdir = d.getVar("SPDX_DEPLOY_ARTIFACTS_DIR")
    if not artifactsdir:
        bb.fatal(f"{pn}: spdx-artifactsdir must be set for task {current_task}")
        return

    artifacts = d.getVar("SPDX_DEPLOY_ARTIFACTS")

    oe.spdx30_tasks.create_deploy_spdx(d, spdxdeploydir, artifactsdir, artifacts)
}
oe.spdx30_tasks.find_build_dep_objsets[vardepsexclude] += "BB_TASKDEPDATA"

python () {
    # Most recipes generate SPDX output in a distinct task from the task that
    # actually is the relevant dependency. As such, we need to map the task
    # that we care about to the task that generates the corresponding SPDX
    # output so that we can rely on the SPDX output being present when the time
    # comes to use it downstream.
    #
    # The down side of this is that only the first level of dependencies (e.g
    # tasks listed in SPDX_DEPLOY_TASKS) will have the mapping done and thus
    # find the dependencies. Transitive dependencies will not be mapped and
    # thus the SPDX data will not be linked in.
    #
    # Ideally, this will be able to go away once more tasks directly generate
    # SPDX files for their output instead of combining it into monolithic
    # functions; tasks listed in this map are the best candidates to have this
    # done first.
    TASK_MAP = {
        # If a task requires the RSS be extended, depend on the SPDX build task
        # for the recipe, at least until it's possible for do_populate_sysroot
        # to describe it's own output.
        "do_populate_sysroot": "do_create_spdx",
    }

    def map_task_deps(task, flag):
        task_flags= (d.getVarFlag(task, flag) or "").split()
        for t in task_flags:
            if t in TASK_MAP and TASK_MAP[t] not in task_flags:
                d.appendVarFlag(task, flag, f" {TASK_MAP[t]}")

    def before_postfunc(f):
        return f == "sstate_task_postfunc" or "buildhistory" in f

    if bb.data.inherits_class("nospdx", d):
        return

    sstate_tasks = set((d.getVar("SSTATETASKS") or "").split())
    deploy_sbom_tasks = []
    for task in (d.getVar("SPDX_DEPLOY_TASKS") or "").split():
        if ":" in task:
            task, func = task.split(":")
        else:
            func = "create_deploy_spdx"

        if not task.startswith("do_deploy") and not task == "do_image_complete":
            bb.fatal(f"Task {task} is not allowed to deploy SPDX data. Must start with 'do_deploy'")

        deploy_sbom_tasks.append(task)

        if task not in sstate_tasks:
            bb.fatal(f"{task} is not an sstate task")

        spdx_deploy = "${SPDXDIR}/deploy-" + task

        # Ensure function is sorted properly. It should be right before
        # sstate_task_postfunc
        postfuncs = (d.getVarFlag(task, "postfuncs") or "").split()

        new_postfuncs = []
        while True:
            if not postfuncs or before_postfunc(postfuncs[0]):
                break
            new_postfuncs.append(postfuncs.pop(0))
        new_postfuncs.append(func)
        new_postfuncs.extend(postfuncs)

        d.setVarFlag(task, "postfuncs", " ".join(new_postfuncs))
        d.prependVarFlag(task, "sstate-inputdirs", f"{spdx_deploy} ")
        d.prependVarFlag(task, "sstate-outputdirs", "${DEPLOY_DIR_SPDX} ")
        d.prependVarFlag(task, "file-checksums", "${SPDX3_DEP_FILES} ")
        d.prependVarFlag(task, "dirs", f"{spdx_deploy} ")
        d.prependVarFlag(task, "cleandirs", f"{spdx_deploy} ")
        d.prependVarFlag(task, "vardeps", "SPDX_DEPLOY_ALLOW_EMPTY")

        deps = (d.getVarFlag(task, "depends") or "").split()
        extra_deps = ["${PN}:do_create_recipe_spdx", "${PN}:do_create_spdx"]
        for dep in deps:
            _, fn, taskname = bb.runqueue.split_tid(dep)
            if taskname in TASK_MAP:
                extra_deps.append(f"{fn}:{TASK_MAP[taskname]}")

        d.prependVarFlag(task, "depends", " ".join(extra_deps) + " ")

        map_task_deps(task, "deptask")
        map_task_deps(task, "rdeptask")
        map_task_deps(task, "recrdeptask")

    # For now, if a recipe is directly built, deploy all of it's deploy tasks
    # into a single SBoM. We may need an option in the future to have tasks
    # that don't do this (e.g. because they do not deploy to a location that is
    # intended to be consumed by the user)
    if deploy_sbom_tasks and (d.getVar("SPDX_DEPLOY_SBOM") or "") == "1":
        bb.build.addtask("do_create_deploy_sbom", "do_build", " ".join(deploy_sbom_tasks), d)
}

python do_create_deploy_sbom() {
    import oe.spdx30_tasks
    from pathlib import Path
    deploydir = Path(d.getVar("SPDXDEPLOYSBOMDEPLOY"))
    deploy_tasks = []
    for task in (d.getVar("SPDX_DEPLOY_TASKS") or "").split():
        if ":" in task:
            task, _ = task.split(":")
        deploy_tasks.append(task)

    oe.spdx30_tasks.create_deploy_sbom(d, deploydir, deploy_tasks)
}
do_create_deploy_sbom[sstate-inputdirs] = "${SPDXDEPLOYSBOMDEPLOY}"
do_create_deploy_sbom[sstate-outputdirs] = "${DEPLOY_DIR_IMAGE}"
do_create_deploy_sbom[recrdeptask] += "do_create_recipe_spdx do_create_spdx"
do_create_deploy_sbom[cleandirs] += "${SPDXDEPLOYSBOMDEPLOY}"
do_create_deploy_sbom[file-checksums] += "${SPDX3_DEP_FILES}"

SSTATETASKS += "do_create_deploy_sbom"
python do_create_deploy_sbom_setscene() {
    sstate_setscene(d)
}
addtask do_create_deploy_sbom_setscene
SSTATE_SKIP_CREATION:task-create-deploy-sbom = "1"
