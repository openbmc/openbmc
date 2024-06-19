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
SPDXDEPS = "${SPDXDIR}/deps.json"

SPDX_TOOL_NAME ??= "oe-spdx-creator"
SPDX_TOOL_VERSION ??= "1.0"

SPDXRUNTIMEDEPLOY = "${SPDXDIR}/runtime-deploy"

SPDX_INCLUDE_SOURCES ??= "0"
SPDX_ARCHIVE_SOURCES ??= "0"
SPDX_ARCHIVE_PACKAGED ??= "0"

SPDX_UUID_NAMESPACE ??= "sbom.openembedded.org"
SPDX_NAMESPACE_PREFIX ??= "http://spdx.org/spdxdocs"
SPDX_PRETTY ??= "0"

SPDX_LICENSES ??= "${COREBASE}/meta/files/spdx-licenses.json"

SPDX_CUSTOM_ANNOTATION_VARS ??= ""

SPDX_ORG ??= "OpenEmbedded ()"
SPDX_SUPPLIER ??= "Organization: ${SPDX_ORG}"
SPDX_SUPPLIER[doc] = "The SPDX PackageSupplier field for SPDX packages created from \
    this recipe. For SPDX documents create using this class during the build, this \
    is the contact information for the person or organization who is doing the \
    build."

def extract_licenses(filename):
    import re

    lic_regex = re.compile(rb'^\W*SPDX-License-Identifier:\s*([ \w\d.()+-]+?)(?:\s+\W*)?$', re.MULTILINE)

    try:
        with open(filename, 'rb') as f:
            size = min(15000, os.stat(filename).st_size)
            txt = f.read(size)
            licenses = re.findall(lic_regex, txt)
            if licenses:
                ascii_licenses = [lic.decode('ascii') for lic in licenses]
                return ascii_licenses
    except Exception as e:
        bb.warn(f"Exception reading {filename}: {e}")
    return []

def is_work_shared_spdx(d):
    return bb.data.inherits_class('kernel', d) or ('work-shared' in d.getVar('WORKDIR'))

def get_json_indent(d):
    if d.getVar("SPDX_PRETTY") == "1":
        return 2
    return None

python() {
    import json
    if d.getVar("SPDX_LICENSE_DATA"):
        return

    with open(d.getVar("SPDX_LICENSES"), "r") as f:
        data = json.load(f)
        # Transform the license array to a dictionary
        data["licenses"] = {l["licenseId"]: l for l in data["licenses"]}
        d.setVar("SPDX_LICENSE_DATA", data)
}

def process_sources(d):
    pn = d.getVar('PN')
    assume_provided = (d.getVar("ASSUME_PROVIDED") or "").split()
    if pn in assume_provided:
        for p in d.getVar("PROVIDES").split():
            if p != pn:
                pn = p
                break

    # glibc-locale: do_fetch, do_unpack and do_patch tasks have been deleted,
    # so avoid archiving source here.
    if pn.startswith('glibc-locale'):
        return False
    if d.getVar('PN') == "libtool-cross":
        return False
    if d.getVar('PN') == "libgcc-initial":
        return False
    if d.getVar('PN') == "shadow-sysroot":
        return False

    # We just archive gcc-source for all the gcc related recipes
    if d.getVar('BPN') in ['gcc', 'libgcc']:
        bb.debug(1, 'spdx: There is bug in scan of %s is, do nothing' % pn)
        return False

    return True

def collect_direct_deps(d, dep_task):
    current_task = "do_" + d.getVar("BB_CURRENTTASK")
    pn = d.getVar("PN")

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)

    for this_dep in taskdepdata.values():
        if this_dep[0] == pn and this_dep[1] == current_task:
            break
    else:
        bb.fatal(f"Unable to find this {pn}:{current_task} in taskdepdata")

    deps = set()

    for dep_name in this_dep.deps:
        dep_data = taskdepdata[dep_name]
        if dep_data.taskname == dep_task and dep_data.pn != pn:
            deps.add((dep_data.pn, dep_data.hashfn, dep_name in this_dep.taskhash_deps))

    return sorted(deps)

collect_direct_deps[vardepsexclude] += "BB_TASKDEPDATA"
collect_direct_deps[vardeps] += "DEPENDS"

python do_collect_spdx_deps() {
    # This task calculates the build time dependencies of the recipe, and is
    # required because while a task can deptask on itself, those dependencies
    # do not show up in BB_TASKDEPDATA. To work around that, this task does the
    # deptask on do_create_spdx and writes out the dependencies it finds, then
    # do_create_spdx reads in the found dependencies when writing the actual
    # SPDX document
    import json
    from pathlib import Path

    spdx_deps_file = Path(d.getVar("SPDXDEPS"))

    deps = collect_direct_deps(d, "do_create_spdx")

    with spdx_deps_file.open("w") as f:
        json.dump(deps, f)
}
# NOTE: depending on do_unpack is a hack that is necessary to get it's dependencies for archive the source
addtask do_collect_spdx_deps after do_unpack
do_collect_spdx_deps[depends] += "${PATCHDEPENDENCY}"
do_collect_spdx_deps[deptask] = "do_create_spdx"
do_collect_spdx_deps[dirs] = "${SPDXDIR}"

def get_spdx_deps(d):
    import json
    from pathlib import Path

    spdx_deps_file = Path(d.getVar("SPDXDEPS"))

    with spdx_deps_file.open("r") as f:
        return json.load(f)

def collect_package_providers(d):
    from pathlib import Path
    import oe.sbom
    import oe.spdx
    import json

    deploy_dir_spdx = Path(d.getVar("DEPLOY_DIR_SPDX"))

    providers = {}

    deps = collect_direct_deps(d, "do_create_spdx")
    deps.append((d.getVar("PN"), d.getVar("BB_HASHFILENAME"), True))

    for dep_pn, dep_hashfn, _ in deps:
        localdata = d
        recipe_data = oe.packagedata.read_pkgdata(dep_pn, localdata)
        if not recipe_data:
            localdata = bb.data.createCopy(d)
            localdata.setVar("PKGDATA_DIR", "${PKGDATA_DIR_SDK}")
            recipe_data = oe.packagedata.read_pkgdata(dep_pn, localdata)

        for pkg in recipe_data.get("PACKAGES", "").split():

            pkg_data = oe.packagedata.read_subpkgdata_dict(pkg, localdata)
            rprovides = set(n for n, _ in bb.utils.explode_dep_versions2(pkg_data.get("RPROVIDES", "")).items())
            rprovides.add(pkg)

            if "PKG" in pkg_data:
                pkg = pkg_data["PKG"]
                rprovides.add(pkg)

            for r in rprovides:
                providers[r] = (pkg, dep_hashfn)

    return providers

collect_package_providers[vardepsexclude] += "BB_TASKDEPDATA"

def spdx_get_src(d):
    """
    save patched source of the recipe in SPDX_WORKDIR.
    """
    import shutil
    spdx_workdir = d.getVar('SPDXWORK')
    spdx_sysroot_native = d.getVar('STAGING_DIR_NATIVE')
    pn = d.getVar('PN')

    workdir = d.getVar("WORKDIR")

    try:
        # The kernel class functions require it to be on work-shared, so we dont change WORKDIR
        if not is_work_shared_spdx(d):
            # Change the WORKDIR to make do_unpack do_patch run in another dir.
            d.setVar('WORKDIR', spdx_workdir)
            # Restore the original path to recipe's native sysroot (it's relative to WORKDIR).
            d.setVar('STAGING_DIR_NATIVE', spdx_sysroot_native)

            # The changed 'WORKDIR' also caused 'B' changed, create dir 'B' for the
            # possibly requiring of the following tasks (such as some recipes's
            # do_patch required 'B' existed).
            bb.utils.mkdirhier(d.getVar('B'))

            bb.build.exec_func('do_unpack', d)
        # Copy source of kernel to spdx_workdir
        if is_work_shared_spdx(d):
            share_src = d.getVar('WORKDIR')
            d.setVar('WORKDIR', spdx_workdir)
            d.setVar('STAGING_DIR_NATIVE', spdx_sysroot_native)
            src_dir = spdx_workdir + "/" + d.getVar('PN')+ "-" + d.getVar('PV') + "-" + d.getVar('PR')
            bb.utils.mkdirhier(src_dir)
            if bb.data.inherits_class('kernel',d):
                share_src = d.getVar('STAGING_KERNEL_DIR')
            cmd_copy_share = "cp -rf " + share_src + "/* " + src_dir + "/"
            cmd_copy_shared_res = os.popen(cmd_copy_share).read()
            bb.note("cmd_copy_shared_result = " + cmd_copy_shared_res)

            git_path = src_dir + "/.git"
            if os.path.exists(git_path):
                shutils.rmtree(git_path)

        # Make sure gcc and kernel sources are patched only once
        if not (d.getVar('SRC_URI') == "" or is_work_shared_spdx(d)):
            bb.build.exec_func('do_patch', d)

        # Some userland has no source.
        if not os.path.exists( spdx_workdir ):
            bb.utils.mkdirhier(spdx_workdir)
    finally:
        d.setVar("WORKDIR", workdir)

spdx_get_src[vardepsexclude] += "STAGING_KERNEL_DIR"

