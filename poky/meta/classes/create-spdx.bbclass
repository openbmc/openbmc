#
# SPDX-License-Identifier: GPL-2.0-only
#

DEPLOY_DIR_SPDX ??= "${DEPLOY_DIR}/spdx/${MACHINE}"

# The product name that the CVE database uses.  Defaults to BPN, but may need to
# be overriden per recipe (for example tiff.bb sets CVE_PRODUCT=libtiff).
CVE_PRODUCT ??= "${BPN}"
CVE_VERSION ??= "${PV}"

SPDXDIR ??= "${WORKDIR}/spdx"
SPDXDEPLOY = "${SPDXDIR}/deploy"
SPDXWORK = "${SPDXDIR}/work"

SPDX_TOOL_NAME ??= "oe-spdx-creator"
SPDX_TOOL_VERSION ??= "1.0"

SPDXRUNTIMEDEPLOY = "${SPDXDIR}/runtime-deploy"

SPDX_INCLUDE_SOURCES ??= "0"
SPDX_ARCHIVE_SOURCES ??= "0"
SPDX_ARCHIVE_PACKAGED ??= "0"

SPDX_UUID_NAMESPACE ??= "sbom.openembedded.org"
SPDX_NAMESPACE_PREFIX ??= "http://spdx.org/spdxdoc"
SPDX_PRETTY ??= "0"

SPDX_LICENSES ??= "${COREBASE}/meta/files/spdx-licenses.json"

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
    return None

def get_doc_namespace(d, doc):
    import uuid
    namespace_uuid = uuid.uuid5(uuid.NAMESPACE_DNS, d.getVar("SPDX_UUID_NAMESPACE"))
    return "%s/%s-%s" % (d.getVar("SPDX_NAMESPACE_PREFIX"), doc.name, str(uuid.uuid5(namespace_uuid, doc.name)))

def create_annotation(d, comment):
    from datetime import datetime, timezone

    creation_time = datetime.now(tz=timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
    annotation = oe.spdx.SPDXAnnotation()
    annotation.annotationDate = creation_time
    annotation.annotationType = "OTHER"
    annotation.annotator = "Tool: %s - %s" % (d.getVar("SPDX_TOOL_NAME"), d.getVar("SPDX_TOOL_VERSION"))
    annotation.comment = comment
    return annotation

def recipe_spdx_is_native(d, recipe):
    return any(a.annotationType == "OTHER" and
      a.annotator == "Tool: %s - %s" % (d.getVar("SPDX_TOOL_NAME"), d.getVar("SPDX_TOOL_VERSION")) and
      a.comment == "isNative" for a in recipe.annotations)

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

def convert_license_to_spdx(lic, document, d, existing={}):
    from pathlib import Path
    import oe.spdx

    license_data = d.getVar("SPDX_LICENSE_DATA")
    extracted = {}

    def add_extracted_license(ident, name):
        nonlocal document

        if name in extracted:
            return

        extracted_info = oe.spdx.SPDXExtractedLicensingInfo()
        extracted_info.name = name
        extracted_info.licenseId = ident
        extracted_info.extractedText = None

        if name == "PD":
            # Special-case this.
            extracted_info.extractedText = "Software released to the public domain"
        else:
            # Seach for the license in COMMON_LICENSE_DIR and LICENSE_PATH
            for directory in [d.getVar('COMMON_LICENSE_DIR')] + (d.getVar('LICENSE_PATH') or '').split():
                try:
                    with (Path(directory) / name).open(errors="replace") as f:
                        extracted_info.extractedText = f.read()
                        break
                except FileNotFoundError:
                    pass
            if extracted_info.extractedText is None:
                # If it's not SPDX or PD, then NO_GENERIC_LICENSE must be set
                filename = d.getVarFlag('NO_GENERIC_LICENSE', name)
                if filename:
                    filename = d.expand("${S}/" + filename)
                    with open(filename, errors="replace") as f:
                        extracted_info.extractedText = f.read()
                else:
                    bb.error("Cannot find any text for license %s" % name)

        extracted[name] = extracted_info
        document.hasExtractedLicensingInfos.append(extracted_info)

    def convert(l):
        if l == "(" or l == ")":
            return l

        if l == "&":
            return "AND"

        if l == "|":
            return "OR"

        if l == "CLOSED":
            return "NONE"

        spdx_license = d.getVarFlag("SPDXLICENSEMAP", l) or l
        if spdx_license in license_data["licenses"]:
            return spdx_license

        try:
            spdx_license = existing[l]
        except KeyError:
            spdx_license = "LicenseRef-" + l
            add_extracted_license(spdx_license, l)

        return spdx_license

    lic_split = lic.replace("(", " ( ").replace(")", " ) ").split()

    return ' '.join(convert(l) for l in lic_split)

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


def add_package_files(d, doc, spdx_pkg, topdir, get_spdxid, get_types, *, archive=None, ignore_dirs=[], ignore_top_level_dirs=[]):
    from pathlib import Path
    import oe.spdx
    import hashlib

    source_date_epoch = d.getVar("SOURCE_DATE_EPOCH")
    if source_date_epoch:
        source_date_epoch = int(source_date_epoch)

    sha1s = []
    spdx_files = []

    file_counter = 1
    for subdir, dirs, files in os.walk(topdir):
        dirs[:] = [d for d in dirs if d not in ignore_dirs]
        if subdir == str(topdir):
            dirs[:] = [d for d in dirs if d not in ignore_top_level_dirs]

        for file in files:
            filepath = Path(subdir) / file
            filename = str(filepath.relative_to(topdir))

            if not filepath.is_symlink() and filepath.is_file():
                spdx_file = oe.spdx.SPDXFile()
                spdx_file.SPDXID = get_spdxid(file_counter)
                for t in get_types(filepath):
                    spdx_file.fileTypes.append(t)
                spdx_file.fileName = filename

                if archive is not None:
                    with filepath.open("rb") as f:
                        info = archive.gettarinfo(fileobj=f)
                        info.name = filename
                        info.uid = 0
                        info.gid = 0
                        info.uname = "root"
                        info.gname = "root"

                        if source_date_epoch is not None and info.mtime > source_date_epoch:
                            info.mtime = source_date_epoch

                        archive.addfile(info, f)

                sha1 = bb.utils.sha1_file(filepath)
                sha1s.append(sha1)
                spdx_file.checksums.append(oe.spdx.SPDXChecksum(
                        algorithm="SHA1",
                        checksumValue=sha1,
                    ))
                spdx_file.checksums.append(oe.spdx.SPDXChecksum(
                        algorithm="SHA256",
                        checksumValue=bb.utils.sha256_file(filepath),
                    ))

                if "SOURCE" in spdx_file.fileTypes:
                    extracted_lics = extract_licenses(filepath)
                    if extracted_lics:
                        spdx_file.licenseInfoInFiles = extracted_lics

                doc.files.append(spdx_file)
                doc.add_relationship(spdx_pkg, "CONTAINS", spdx_file)
                spdx_pkg.hasFiles.append(spdx_file.SPDXID)

                spdx_files.append(spdx_file)

                file_counter += 1

    sha1s.sort()
    verifier = hashlib.sha1()
    for v in sha1s:
        verifier.update(v.encode("utf-8"))
    spdx_pkg.packageVerificationCode.packageVerificationCodeValue = verifier.hexdigest()

    return spdx_files


def add_package_sources_from_debug(d, package_doc, spdx_package, package, package_files, sources):
    from pathlib import Path
    import hashlib
    import oe.packagedata
    import oe.spdx

    debug_search_paths = [
        Path(d.getVar('PKGD')),
        Path(d.getVar('STAGING_DIR_TARGET')),
        Path(d.getVar('STAGING_DIR_NATIVE')),
        Path(d.getVar('STAGING_KERNEL_DIR')),
    ]

    pkg_data = oe.packagedata.read_subpkgdata_extended(package, d)

    if pkg_data is None:
        return

    for file_path, file_data in pkg_data["files_info"].items():
        if not "debugsrc" in file_data:
            continue

        for pkg_file in package_files:
            if file_path.lstrip("/") == pkg_file.fileName.lstrip("/"):
                break
        else:
            bb.fatal("No package file found for %s" % str(file_path))
            continue

        for debugsrc in file_data["debugsrc"]:
            ref_id = "NOASSERTION"
            for search in debug_search_paths:
                if debugsrc.startswith("/usr/src/kernel"):
                    debugsrc_path = search / debugsrc.replace('/usr/src/kernel/', '')
                else:
                    debugsrc_path = search / debugsrc.lstrip("/")
                if not debugsrc_path.exists():
                    continue

                file_sha256 = bb.utils.sha256_file(debugsrc_path)

                if file_sha256 in sources:
                    source_file = sources[file_sha256]

                    doc_ref = package_doc.find_external_document_ref(source_file.doc.documentNamespace)
                    if doc_ref is None:
                        doc_ref = oe.spdx.SPDXExternalDocumentRef()
                        doc_ref.externalDocumentId = "DocumentRef-dependency-" + source_file.doc.name
                        doc_ref.spdxDocument = source_file.doc.documentNamespace
                        doc_ref.checksum.algorithm = "SHA1"
                        doc_ref.checksum.checksumValue = source_file.doc_sha1
                        package_doc.externalDocumentRefs.append(doc_ref)

                    ref_id = "%s:%s" % (doc_ref.externalDocumentId, source_file.file.SPDXID)
                else:
                    bb.debug(1, "Debug source %s with SHA256 %s not found in any dependency" % (str(debugsrc_path), file_sha256))
                break
            else:
                bb.debug(1, "Debug source %s not found" % debugsrc)

            package_doc.add_relationship(pkg_file, "GENERATED_FROM", ref_id, comment=debugsrc)

def collect_dep_recipes(d, doc, spdx_recipe):
    from pathlib import Path
    import oe.sbom
    import oe.spdx

    deploy_dir_spdx = Path(d.getVar("DEPLOY_DIR_SPDX"))

    dep_recipes = []
    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    deps = sorted(set(
        dep[0] for dep in taskdepdata.values() if
            dep[1] == "do_create_spdx" and dep[0] != d.getVar("PN")
    ))
    for dep_pn in deps:
        dep_recipe_path = deploy_dir_spdx / "recipes" / ("recipe-%s.spdx.json" % dep_pn)

        spdx_dep_doc, spdx_dep_sha1 = oe.sbom.read_doc(dep_recipe_path)

        for pkg in spdx_dep_doc.packages:
            if pkg.name == dep_pn:
                spdx_dep_recipe = pkg
                break
        else:
            continue

        dep_recipes.append(oe.sbom.DepRecipe(spdx_dep_doc, spdx_dep_sha1, spdx_dep_recipe))

        dep_recipe_ref = oe.spdx.SPDXExternalDocumentRef()
        dep_recipe_ref.externalDocumentId = "DocumentRef-dependency-" + spdx_dep_doc.name
        dep_recipe_ref.spdxDocument = spdx_dep_doc.documentNamespace
        dep_recipe_ref.checksum.algorithm = "SHA1"
        dep_recipe_ref.checksum.checksumValue = spdx_dep_sha1

        doc.externalDocumentRefs.append(dep_recipe_ref)

        doc.add_relationship(
            "%s:%s" % (dep_recipe_ref.externalDocumentId, spdx_dep_recipe.SPDXID),
            "BUILD_DEPENDENCY_OF",
            spdx_recipe
        )

    return dep_recipes

collect_dep_recipes[vardepsexclude] += "BB_TASKDEPDATA"


def collect_dep_sources(d, dep_recipes):
    import oe.sbom

    sources = {}
    for dep in dep_recipes:
        # Don't collect sources from native recipes as they
        # match non-native sources also.
        if recipe_spdx_is_native(d, dep.recipe):
            continue
        recipe_files = set(dep.recipe.hasFiles)

        for spdx_file in dep.doc.files:
            if spdx_file.SPDXID not in recipe_files:
                continue

            if "SOURCE" in spdx_file.fileTypes:
                for checksum in spdx_file.checksums:
                    if checksum.algorithm == "SHA256":
                        sources[checksum.checksumValue] = oe.sbom.DepSource(dep.doc, dep.doc_sha1, dep.recipe, spdx_file)
                        break

    return sources


python do_create_spdx() {
    from datetime import datetime, timezone
    import oe.sbom
    import oe.spdx
    import uuid
    from pathlib import Path
    from contextlib import contextmanager
    import oe.cve_check

    @contextmanager
    def optional_tarfile(name, guard, mode="w"):
        import tarfile
        import bb.compress.zstd

        num_threads = int(d.getVar("BB_NUMBER_THREADS"))

        if guard:
            name.parent.mkdir(parents=True, exist_ok=True)
            with bb.compress.zstd.open(name, mode=mode + "b", num_threads=num_threads) as f:
                with tarfile.open(fileobj=f, mode=mode + "|") as tf:
                    yield tf
        else:
            yield None


    deploy_dir_spdx = Path(d.getVar("DEPLOY_DIR_SPDX"))
    spdx_workdir = Path(d.getVar("SPDXWORK"))
    include_sources = d.getVar("SPDX_INCLUDE_SOURCES") == "1"
    archive_sources = d.getVar("SPDX_ARCHIVE_SOURCES") == "1"
    archive_packaged = d.getVar("SPDX_ARCHIVE_PACKAGED") == "1"

    creation_time = datetime.now(tz=timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")

    doc = oe.spdx.SPDXDocument()

    doc.name = "recipe-" + d.getVar("PN")
    doc.documentNamespace = get_doc_namespace(d, doc)
    doc.creationInfo.created = creation_time
    doc.creationInfo.comment = "This document was created by analyzing recipe files during the build."
    doc.creationInfo.licenseListVersion = d.getVar("SPDX_LICENSE_DATA")["licenseListVersion"]
    doc.creationInfo.creators.append("Tool: OpenEmbedded Core create-spdx.bbclass")
    doc.creationInfo.creators.append("Organization: %s" % d.getVar("SPDX_ORG"))
    doc.creationInfo.creators.append("Person: N/A ()")

    recipe = oe.spdx.SPDXPackage()
    recipe.name = d.getVar("PN")
    recipe.versionInfo = d.getVar("PV")
    recipe.SPDXID = oe.sbom.get_recipe_spdxid(d)
    recipe.supplier = d.getVar("SPDX_SUPPLIER")
    if bb.data.inherits_class("native", d) or bb.data.inherits_class("cross", d):
        recipe.annotations.append(create_annotation(d, "isNative"))

    for s in d.getVar('SRC_URI').split():
        if not s.startswith("file://"):
            s = s.split(';')[0]
            recipe.downloadLocation = s
            break
    else:
        recipe.downloadLocation = "NOASSERTION"

    homepage = d.getVar("HOMEPAGE")
    if homepage:
        recipe.homepage = homepage

    license = d.getVar("LICENSE")
    if license:
        recipe.licenseDeclared = convert_license_to_spdx(license, doc, d)

    summary = d.getVar("SUMMARY")
    if summary:
        recipe.summary = summary

    description = d.getVar("DESCRIPTION")
    if description:
        recipe.description = description

    # Some CVEs may be patched during the build process without incrementing the version number,
    # so querying for CVEs based on the CPE id can lead to false positives. To account for this,
    # save the CVEs fixed by patches to source information field in the SPDX.
    patched_cves = oe.cve_check.get_patched_cves(d)
    patched_cves = list(patched_cves)
    patched_cves = ' '.join(patched_cves)
    if patched_cves:
        recipe.sourceInfo = "CVEs fixed: " + patched_cves

    cpe_ids = oe.cve_check.get_cpe_ids(d.getVar("CVE_PRODUCT"), d.getVar("CVE_VERSION"))
    if cpe_ids:
        for cpe_id in cpe_ids:
            cpe = oe.spdx.SPDXExternalReference()
            cpe.referenceCategory = "SECURITY"
            cpe.referenceType = "http://spdx.org/rdf/references/cpe23Type"
            cpe.referenceLocator = cpe_id
            recipe.externalRefs.append(cpe)

    doc.packages.append(recipe)
    doc.add_relationship(doc, "DESCRIBES", recipe)

    if process_sources(d) and include_sources:
        recipe_archive = deploy_dir_spdx / "recipes" / (doc.name + ".tar.zst")
        with optional_tarfile(recipe_archive, archive_sources) as archive:
            spdx_get_src(d)

            add_package_files(
                d,
                doc,
                recipe,
                spdx_workdir,
                lambda file_counter: "SPDXRef-SourceFile-%s-%d" % (d.getVar("PN"), file_counter),
                lambda filepath: ["SOURCE"],
                ignore_dirs=[".git"],
                ignore_top_level_dirs=["temp"],
                archive=archive,
            )

            if archive is not None:
                recipe.packageFileName = str(recipe_archive.name)

    dep_recipes = collect_dep_recipes(d, doc, recipe)

    doc_sha1 = oe.sbom.write_doc(d, doc, "recipes", indent=get_json_indent(d))
    dep_recipes.append(oe.sbom.DepRecipe(doc, doc_sha1, recipe))

    recipe_ref = oe.spdx.SPDXExternalDocumentRef()
    recipe_ref.externalDocumentId = "DocumentRef-recipe-" + recipe.name
    recipe_ref.spdxDocument = doc.documentNamespace
    recipe_ref.checksum.algorithm = "SHA1"
    recipe_ref.checksum.checksumValue = doc_sha1

    sources = collect_dep_sources(d, dep_recipes)
    found_licenses = {license.name:recipe_ref.externalDocumentId + ":" + license.licenseId for license in doc.hasExtractedLicensingInfos}

    if not recipe_spdx_is_native(d, recipe):
        bb.build.exec_func("read_subpackage_metadata", d)

        pkgdest = Path(d.getVar("PKGDEST"))
        for package in d.getVar("PACKAGES").split():
            if not oe.packagedata.packaged(package, d):
                continue

            package_doc = oe.spdx.SPDXDocument()
            pkg_name = d.getVar("PKG:%s" % package) or package
            package_doc.name = pkg_name
            package_doc.documentNamespace = get_doc_namespace(d, package_doc)
            package_doc.creationInfo.created = creation_time
            package_doc.creationInfo.comment = "This document was created by analyzing packages created during the build."
            package_doc.creationInfo.licenseListVersion = d.getVar("SPDX_LICENSE_DATA")["licenseListVersion"]
            package_doc.creationInfo.creators.append("Tool: OpenEmbedded Core create-spdx.bbclass")
            package_doc.creationInfo.creators.append("Organization: %s" % d.getVar("SPDX_ORG"))
            package_doc.creationInfo.creators.append("Person: N/A ()")
            package_doc.externalDocumentRefs.append(recipe_ref)

            package_license = d.getVar("LICENSE:%s" % package) or d.getVar("LICENSE")

            spdx_package = oe.spdx.SPDXPackage()

            spdx_package.SPDXID = oe.sbom.get_package_spdxid(pkg_name)
            spdx_package.name = pkg_name
            spdx_package.versionInfo = d.getVar("PV")
            spdx_package.licenseDeclared = convert_license_to_spdx(package_license, package_doc, d, found_licenses)
            spdx_package.supplier = d.getVar("SPDX_SUPPLIER")

            package_doc.packages.append(spdx_package)

            package_doc.add_relationship(spdx_package, "GENERATED_FROM", "%s:%s" % (recipe_ref.externalDocumentId, recipe.SPDXID))
            package_doc.add_relationship(package_doc, "DESCRIBES", spdx_package)

            package_archive = deploy_dir_spdx / "packages" / (package_doc.name + ".tar.zst")
            with optional_tarfile(package_archive, archive_packaged) as archive:
                package_files = add_package_files(
                    d,
                    package_doc,
                    spdx_package,
                    pkgdest / package,
                    lambda file_counter: oe.sbom.get_packaged_file_spdxid(pkg_name, file_counter),
                    lambda filepath: ["BINARY"],
                    ignore_top_level_dirs=['CONTROL', 'DEBIAN'],
                    archive=archive,
                )

                if archive is not None:
                    spdx_package.packageFileName = str(package_archive.name)

            add_package_sources_from_debug(d, package_doc, spdx_package, package, package_files, sources)

            oe.sbom.write_doc(d, package_doc, "packages", indent=get_json_indent(d))
}
# NOTE: depending on do_unpack is a hack that is necessary to get it's dependencies for archive the source
addtask do_create_spdx after do_package do_packagedata do_unpack before do_populate_sdk do_build do_rm_work

SSTATETASKS += "do_create_spdx"
do_create_spdx[sstate-inputdirs] = "${SPDXDEPLOY}"
do_create_spdx[sstate-outputdirs] = "${DEPLOY_DIR_SPDX}"

python do_create_spdx_setscene () {
    sstate_setscene(d)
}
addtask do_create_spdx_setscene

do_create_spdx[dirs] = "${SPDXWORK}"
do_create_spdx[cleandirs] = "${SPDXDEPLOY} ${SPDXWORK}"
do_create_spdx[depends] += "${PATCHDEPENDENCY}"
do_create_spdx[deptask] = "do_create_spdx"

def collect_package_providers(d):
    from pathlib import Path
    import oe.sbom
    import oe.spdx
    import json

    deploy_dir_spdx = Path(d.getVar("DEPLOY_DIR_SPDX"))

    providers = {}

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    deps = sorted(set(
        dep[0] for dep in taskdepdata.values() if dep[0] != d.getVar("PN")
    ))
    deps.append(d.getVar("PN"))

    for dep_pn in deps:
        recipe_data = oe.packagedata.read_pkgdata(dep_pn, d)

        for pkg in recipe_data.get("PACKAGES", "").split():

            pkg_data = oe.packagedata.read_subpkgdata_dict(pkg, d)
            rprovides = set(n for n, _ in bb.utils.explode_dep_versions2(pkg_data.get("RPROVIDES", "")).items())
            rprovides.add(pkg)

            for r in rprovides:
                providers[r] = pkg

    return providers

collect_package_providers[vardepsexclude] += "BB_TASKDEPDATA"

python do_create_runtime_spdx() {
    from datetime import datetime, timezone
    import oe.sbom
    import oe.spdx
    import oe.packagedata
    from pathlib import Path

    deploy_dir_spdx = Path(d.getVar("DEPLOY_DIR_SPDX"))
    spdx_deploy = Path(d.getVar("SPDXRUNTIMEDEPLOY"))
    is_native = bb.data.inherits_class("native", d) or bb.data.inherits_class("cross", d)

    creation_time = datetime.now(tz=timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")

    providers = collect_package_providers(d)

    if not is_native:
        bb.build.exec_func("read_subpackage_metadata", d)

        dep_package_cache = {}

        pkgdest = Path(d.getVar("PKGDEST"))
        for package in d.getVar("PACKAGES").split():
            localdata = bb.data.createCopy(d)
            pkg_name = d.getVar("PKG:%s" % package) or package
            localdata.setVar("PKG", pkg_name)
            localdata.setVar('OVERRIDES', d.getVar("OVERRIDES", False) + ":" + package)

            if not oe.packagedata.packaged(package, localdata):
                continue

            pkg_spdx_path = deploy_dir_spdx / "packages" / (pkg_name + ".spdx.json")

            package_doc, package_doc_sha1 = oe.sbom.read_doc(pkg_spdx_path)

            for p in package_doc.packages:
                if p.name == pkg_name:
                    spdx_package = p
                    break
            else:
                bb.fatal("Package '%s' not found in %s" % (pkg_name, pkg_spdx_path))

            runtime_doc = oe.spdx.SPDXDocument()
            runtime_doc.name = "runtime-" + pkg_name
            runtime_doc.documentNamespace = get_doc_namespace(localdata, runtime_doc)
            runtime_doc.creationInfo.created = creation_time
            runtime_doc.creationInfo.comment = "This document was created by analyzing package runtime dependencies."
            runtime_doc.creationInfo.licenseListVersion = d.getVar("SPDX_LICENSE_DATA")["licenseListVersion"]
            runtime_doc.creationInfo.creators.append("Tool: OpenEmbedded Core create-spdx.bbclass")
            runtime_doc.creationInfo.creators.append("Organization: %s" % d.getVar("SPDX_ORG"))
            runtime_doc.creationInfo.creators.append("Person: N/A ()")

            package_ref = oe.spdx.SPDXExternalDocumentRef()
            package_ref.externalDocumentId = "DocumentRef-package-" + package
            package_ref.spdxDocument = package_doc.documentNamespace
            package_ref.checksum.algorithm = "SHA1"
            package_ref.checksum.checksumValue = package_doc_sha1

            runtime_doc.externalDocumentRefs.append(package_ref)

            runtime_doc.add_relationship(
                runtime_doc.SPDXID,
                "AMENDS",
                "%s:%s" % (package_ref.externalDocumentId, package_doc.SPDXID)
            )

            deps = bb.utils.explode_dep_versions2(localdata.getVar("RDEPENDS") or "")
            seen_deps = set()
            for dep, _ in deps.items():
                if dep in seen_deps:
                    continue

                if dep not in providers:
                    continue

                dep = providers[dep]

                if not oe.packagedata.packaged(dep, localdata):
                    continue

                dep_pkg_data = oe.packagedata.read_subpkgdata_dict(dep, d)
                dep_pkg = dep_pkg_data["PKG"]

                if dep in dep_package_cache:
                    (dep_spdx_package, dep_package_ref) = dep_package_cache[dep]
                else:
                    dep_path = deploy_dir_spdx / "packages" / ("%s.spdx.json" % dep_pkg)

                    spdx_dep_doc, spdx_dep_sha1 = oe.sbom.read_doc(dep_path)

                    for pkg in spdx_dep_doc.packages:
                        if pkg.name == dep_pkg:
                            dep_spdx_package = pkg
                            break
                    else:
                        bb.fatal("Package '%s' not found in %s" % (dep_pkg, dep_path))

                    dep_package_ref = oe.spdx.SPDXExternalDocumentRef()
                    dep_package_ref.externalDocumentId = "DocumentRef-runtime-dependency-" + spdx_dep_doc.name
                    dep_package_ref.spdxDocument = spdx_dep_doc.documentNamespace
                    dep_package_ref.checksum.algorithm = "SHA1"
                    dep_package_ref.checksum.checksumValue = spdx_dep_sha1

                    dep_package_cache[dep] = (dep_spdx_package, dep_package_ref)

                runtime_doc.externalDocumentRefs.append(dep_package_ref)

                runtime_doc.add_relationship(
                    "%s:%s" % (dep_package_ref.externalDocumentId, dep_spdx_package.SPDXID),
                    "RUNTIME_DEPENDENCY_OF",
                    "%s:%s" % (package_ref.externalDocumentId, spdx_package.SPDXID)
                )
                seen_deps.add(dep)

            oe.sbom.write_doc(d, runtime_doc, "runtime", spdx_deploy, indent=get_json_indent(d))
}

addtask do_create_runtime_spdx after do_create_spdx before do_build do_rm_work
SSTATETASKS += "do_create_runtime_spdx"
do_create_runtime_spdx[sstate-inputdirs] = "${SPDXRUNTIMEDEPLOY}"
do_create_runtime_spdx[sstate-outputdirs] = "${DEPLOY_DIR_SPDX}"

python do_create_runtime_spdx_setscene () {
    sstate_setscene(d)
}
addtask do_create_runtime_spdx_setscene

do_create_runtime_spdx[dirs] = "${SPDXRUNTIMEDEPLOY}"
do_create_runtime_spdx[cleandirs] = "${SPDXRUNTIMEDEPLOY}"
do_create_runtime_spdx[rdeptask] = "do_create_spdx"

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

do_rootfs[recrdeptask] += "do_create_spdx do_create_runtime_spdx"

ROOTFS_POSTUNINSTALL_COMMAND =+ "image_combine_spdx ; "

do_populate_sdk[recrdeptask] += "do_create_spdx do_create_runtime_spdx"
POPULATE_SDK_POST_HOST_COMMAND:append:task-populate-sdk = " sdk_host_combine_spdx; "
POPULATE_SDK_POST_TARGET_COMMAND:append:task-populate-sdk = " sdk_target_combine_spdx; "

python image_combine_spdx() {
    import os
    import oe.sbom
    from pathlib import Path
    from oe.rootfs import image_list_installed_packages

    image_name = d.getVar("IMAGE_NAME")
    image_link_name = d.getVar("IMAGE_LINK_NAME")
    imgdeploydir = Path(d.getVar("IMGDEPLOYDIR"))
    img_spdxid = oe.sbom.get_image_spdxid(image_name)
    packages = image_list_installed_packages(d)

    combine_spdx(d, image_name, imgdeploydir, img_spdxid, packages)

    def make_image_link(target_path, suffix):
        if image_link_name:
            link = imgdeploydir / (image_link_name + suffix)
            if link != target_path:
                link.symlink_to(os.path.relpath(target_path, link.parent))

    image_spdx_path = imgdeploydir / (image_name + ".spdx.json")
    make_image_link(image_spdx_path, ".spdx.json")
    spdx_tar_path = imgdeploydir / (image_name + ".spdx.tar.zst")
    make_image_link(spdx_tar_path, ".spdx.tar.zst")
    spdx_index_path = imgdeploydir / (image_name + ".spdx.index.json")
    make_image_link(spdx_index_path, ".spdx.index.json")
}

python sdk_host_combine_spdx() {
    sdk_combine_spdx(d, "host")
}

python sdk_target_combine_spdx() {
    sdk_combine_spdx(d, "target")
}

def sdk_combine_spdx(d, sdk_type):
    import oe.sbom
    from pathlib import Path
    from oe.sdk import sdk_list_installed_packages

    sdk_name = d.getVar("SDK_NAME") + "-" + sdk_type
    sdk_deploydir = Path(d.getVar("SDKDEPLOYDIR"))
    sdk_spdxid = oe.sbom.get_sdk_spdxid(sdk_name)
    sdk_packages = sdk_list_installed_packages(d, sdk_type == "target")
    combine_spdx(d, sdk_name, sdk_deploydir, sdk_spdxid, sdk_packages)

def combine_spdx(d, rootfs_name, rootfs_deploydir, rootfs_spdxid, packages):
    import os
    import oe.spdx
    import oe.sbom
    import io
    import json
    from datetime import timezone, datetime
    from pathlib import Path
    import tarfile
    import bb.compress.zstd

    creation_time = datetime.now(tz=timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
    deploy_dir_spdx = Path(d.getVar("DEPLOY_DIR_SPDX"))
    source_date_epoch = d.getVar("SOURCE_DATE_EPOCH")

    doc = oe.spdx.SPDXDocument()
    doc.name = rootfs_name
    doc.documentNamespace = get_doc_namespace(d, doc)
    doc.creationInfo.created = creation_time
    doc.creationInfo.comment = "This document was created by analyzing the source of the Yocto recipe during the build."
    doc.creationInfo.licenseListVersion = d.getVar("SPDX_LICENSE_DATA")["licenseListVersion"]
    doc.creationInfo.creators.append("Tool: OpenEmbedded Core create-spdx.bbclass")
    doc.creationInfo.creators.append("Organization: %s" % d.getVar("SPDX_ORG"))
    doc.creationInfo.creators.append("Person: N/A ()")

    image = oe.spdx.SPDXPackage()
    image.name = d.getVar("PN")
    image.versionInfo = d.getVar("PV")
    image.SPDXID = rootfs_spdxid
    image.supplier = d.getVar("SPDX_SUPPLIER")

    doc.packages.append(image)

    for name in sorted(packages.keys()):
        pkg_spdx_path = deploy_dir_spdx / "packages" / (name + ".spdx.json")
        pkg_doc, pkg_doc_sha1 = oe.sbom.read_doc(pkg_spdx_path)

        for p in pkg_doc.packages:
            if p.name == name:
                pkg_ref = oe.spdx.SPDXExternalDocumentRef()
                pkg_ref.externalDocumentId = "DocumentRef-%s" % pkg_doc.name
                pkg_ref.spdxDocument = pkg_doc.documentNamespace
                pkg_ref.checksum.algorithm = "SHA1"
                pkg_ref.checksum.checksumValue = pkg_doc_sha1

                doc.externalDocumentRefs.append(pkg_ref)
                doc.add_relationship(image, "CONTAINS", "%s:%s" % (pkg_ref.externalDocumentId, p.SPDXID))
                break
        else:
            bb.fatal("Unable to find package with name '%s' in SPDX file %s" % (name, pkg_spdx_path))

        runtime_spdx_path = deploy_dir_spdx / "runtime" / ("runtime-" + name + ".spdx.json")
        runtime_doc, runtime_doc_sha1 = oe.sbom.read_doc(runtime_spdx_path)

        runtime_ref = oe.spdx.SPDXExternalDocumentRef()
        runtime_ref.externalDocumentId = "DocumentRef-%s" % runtime_doc.name
        runtime_ref.spdxDocument = runtime_doc.documentNamespace
        runtime_ref.checksum.algorithm = "SHA1"
        runtime_ref.checksum.checksumValue = runtime_doc_sha1

        # "OTHER" isn't ideal here, but I can't find a relationship that makes sense
        doc.externalDocumentRefs.append(runtime_ref)
        doc.add_relationship(
            image,
            "OTHER",
            "%s:%s" % (runtime_ref.externalDocumentId, runtime_doc.SPDXID),
            comment="Runtime dependencies for %s" % name
        )

    image_spdx_path = rootfs_deploydir / (rootfs_name + ".spdx.json")

    with image_spdx_path.open("wb") as f:
        doc.to_json(f, sort_keys=True, indent=get_json_indent(d))

    num_threads = int(d.getVar("BB_NUMBER_THREADS"))

    visited_docs = set()

    index = {"documents": []}

    spdx_tar_path = rootfs_deploydir / (rootfs_name + ".spdx.tar.zst")
    with bb.compress.zstd.open(spdx_tar_path, "w", num_threads=num_threads) as f:
        with tarfile.open(fileobj=f, mode="w|") as tar:
            def collect_spdx_document(path):
                nonlocal tar
                nonlocal deploy_dir_spdx
                nonlocal source_date_epoch
                nonlocal index

                if path in visited_docs:
                    return

                visited_docs.add(path)

                with path.open("rb") as f:
                    doc, sha1 = oe.sbom.read_doc(f)
                    f.seek(0)

                    if doc.documentNamespace in visited_docs:
                        return

                    bb.note("Adding SPDX document %s" % path)
                    visited_docs.add(doc.documentNamespace)
                    info = tar.gettarinfo(fileobj=f)

                    info.name = doc.name + ".spdx.json"
                    info.uid = 0
                    info.gid = 0
                    info.uname = "root"
                    info.gname = "root"

                    if source_date_epoch is not None and info.mtime > int(source_date_epoch):
                        info.mtime = int(source_date_epoch)

                    tar.addfile(info, f)

                    index["documents"].append({
                        "filename": info.name,
                        "documentNamespace": doc.documentNamespace,
                        "sha1": sha1,
                    })

                for ref in doc.externalDocumentRefs:
                    ref_path = deploy_dir_spdx / "by-namespace" / ref.spdxDocument.replace("/", "_")
                    collect_spdx_document(ref_path)

            collect_spdx_document(image_spdx_path)

            index["documents"].sort(key=lambda x: x["filename"])

            index_str = io.BytesIO(json.dumps(
                index,
                sort_keys=True,
                indent=get_json_indent(d),
            ).encode("utf-8"))

            info = tarfile.TarInfo()
            info.name = "index.json"
            info.size = len(index_str.getvalue())
            info.uid = 0
            info.gid = 0
            info.uname = "root"
            info.gname = "root"

            tar.addfile(info, fileobj=index_str)

    spdx_index_path = rootfs_deploydir / (rootfs_name + ".spdx.index.json")
    with spdx_index_path.open("w") as f:
        json.dump(index, f, sort_keys=True, indent=get_json_indent(d))
