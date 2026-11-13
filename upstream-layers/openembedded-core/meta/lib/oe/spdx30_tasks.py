#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import json
import oe.cve_check
import oe.license
import oe.packagedata
import oe.patch
import oe.reproducible
import oe.sbom30
import oe.sdk
import oe.spdx30
import oe.spdx_common
import oe.spdx_license
import os
import re
import urllib.parse

from contextlib import contextmanager
from datetime import datetime, timezone
from pathlib import Path


def walk_error(err):
    bb.error(f"ERROR walking {err.filename}: {err}")


def set_timestamp_now(d, o, prop):
    if d.getVar("SPDX_INCLUDE_TIMESTAMPS") == "1":
        setattr(o, prop, datetime.now(timezone.utc))
    else:
        # Doing this helps to validated that the property actually exists, and
        # also that it is not mandatory
        delattr(o, prop)


def get_release_date(d):
    """Resolve the release date to record in a package's releaseTime property.

    Reads SDE_FILE via oe.reproducible.epochfile_read() directly, rather than
    SOURCE_DATE_EPOCH, since a recipe may set SOURCE_DATE_EPOCH itself to
    override the computed value, and that override is not a release date.

    Omits the fallback value since it is not a meaningful release date.

    Returns a datetime, or None if no release date should be recorded.
    """
    source_date_epoch = oe.reproducible.epochfile_read(d.getVar("SDE_FILE"), d)
    if not source_date_epoch or source_date_epoch == d.getVar(
        "SOURCE_DATE_EPOCH_FALLBACK"
    ):
        return None

    return datetime.fromtimestamp(int(source_date_epoch), tz=timezone.utc)


def add_license_expression(
    d, objset, license_expression, license_data, search_objsets=[]
):
    license_text_map = {}
    pn = d.getVar("PN")

    def walk_license(node):
        nonlocal license_text_map

        if isinstance(node, oe.spdx_license.LicenseRef):
            if node.ident not in license_text_map:
                lic = None
                for o in [objset] + search_objsets:
                    lic = o.find_filter(
                        oe.spdx30.simplelicensing_SimpleLicensingText,
                        name=node.name,
                    )
                    if lic:
                        break

                if lic is None:
                    p, _ = oe.license.get_license_path(d, node)
                    if p is None:
                        bb.fatal(
                            f"{pn}: No generic license file exists for: {node.ident} in any provider"
                        )
                        return

                    with open(p, errors="replace") as f:
                        lic_text = f.read()

                    lic = objset.add(
                        oe.spdx30.simplelicensing_SimpleLicensingText(
                            _id=objset.new_spdxid("license-text", node.name),
                            creationInfo=objset.doc.creationInfo,
                            name=node.name,
                            simplelicensing_licenseText=lic_text,
                        )
                    )
                    objset.set_element_alias(lic)

                license_text_map[node.ident] = lic

        for child in node.children:
            walk_license(child)

    s = oe.license.parse_legacy_license(d, license_expression)
    if s is None:
        return None

    walk_license(s)

    o = objset.new_license_expression(s.to_string(), license_data, license_text_map)
    objset.set_element_alias(o)
    return o


def add_package_files(
    d,
    objset,
    topdir,
    get_spdxid,
    get_purposes,
    license_data=None,
    *,
    archive=None,
    ignore_dirs=[],
    ignore_top_level_dirs=[],
):
    source_date_epoch = d.getVar("SOURCE_DATE_EPOCH")
    if source_date_epoch:
        source_date_epoch = int(source_date_epoch)

    spdx_files = set()

    file_counter = 1
    if not os.path.exists(topdir):
        bb.note(f"Skip {topdir}")
        return spdx_files, set()

    check_compiled_sources = d.getVar("SPDX_INCLUDE_COMPILED_SOURCES") == "1"
    if check_compiled_sources:
        compiled_sources, types = oe.spdx_common.get_compiled_sources(d)
        bb.debug(1, f"Total compiled files: {len(compiled_sources)}")

    exclude_patterns = [
        re.compile(pattern)
        for pattern in (d.getVar("SPDX_FILE_EXCLUDE_PATTERNS") or "").split()
    ]
    excluded_files = set()

    for subdir, dirs, files in os.walk(topdir, onerror=walk_error):
        dirs[:] = [directory for directory in dirs if directory not in ignore_dirs]
        if subdir == str(topdir):
            dirs[:] = [
                directory
                for directory in dirs
                if directory not in ignore_top_level_dirs
            ]

        dirs.sort()
        files.sort()
        for file in files:
            filepath = Path(subdir) / file
            if filepath.is_symlink() or not filepath.is_file():
                continue

            filename = str(filepath.relative_to(topdir))

            if exclude_patterns and any(
                pattern.search(filename) for pattern in exclude_patterns
            ):
                excluded_files.add(filename)
                continue

            file_purposes = get_purposes(filepath)

            if check_compiled_sources and not oe.spdx_common.is_compiled_source(
                filename, compiled_sources, types
            ):
                continue

            spdx_file = objset.new_file(
                get_spdxid(file_counter),
                filename,
                filepath,
                purposes=file_purposes,
            )
            spdx_files.add(spdx_file)

            if (
                oe.spdx30.software_SoftwarePurpose.source in file_purposes
                and license_data is not None
            ):
                objset.scan_declared_licenses(spdx_file, filepath, license_data)

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

            file_counter += 1

    bb.debug(1, "Added %d files to %s" % (len(spdx_files), objset.doc._id))

    return spdx_files, excluded_files


def get_package_sources_from_debug(
    d, package, package_files, sources, source_hash_cache, excluded_files=None
):
    if excluded_files is None:
        excluded_files = set()

    def file_path_match(file_path, pkg_file):
        if file_path.lstrip("/") == pkg_file.name.lstrip("/"):
            return True

        for e in pkg_file.extension:
            if isinstance(e, oe.sbom30.OEFileNameAliasExtension):
                for a in e.aliases:
                    if file_path.lstrip("/") == a.lstrip("/"):
                        return True

        return False

    debug_search_paths = [
        Path(d.getVar("SPDXWORK")),
        Path(d.getVar("PKGD")),
        Path(d.getVar("STAGING_DIR_TARGET")),
        Path(d.getVar("STAGING_DIR_NATIVE")),
        Path(d.getVar("STAGING_KERNEL_DIR")),
    ]

    pkg_data = oe.packagedata.read_subpkgdata_extended(package, d)

    dep_source_files = set()
    if pkg_data is None:
        return dep_source_files

    for file_path, file_data in pkg_data["files_info"].items():
        if not "debugsrc" in file_data:
            continue

        if not any(file_path_match(file_path, pkg_file) for pkg_file in package_files):
            if file_path.lstrip("/") in excluded_files:
                bb.debug(
                    1,
                    f"Skipping debug source lookup for excluded file {file_path} in {package}",
                )
                continue
            bb.fatal(
                "No package file found for %s in %s; SPDX found: %s"
                % (str(file_path), package, " ".join(p.name for p in package_files))
            )
            continue

        for debugsrc in file_data["debugsrc"]:
            for search in debug_search_paths:
                if debugsrc.startswith("/usr/src/kernel"):
                    debugsrc_path = search / debugsrc.replace("/usr/src/kernel/", "")
                else:
                    debugsrc_path = search / debugsrc.lstrip("/")

                if debugsrc_path in source_hash_cache:
                    file_sha256 = source_hash_cache[debugsrc_path]
                    if file_sha256 is None:
                        continue
                else:
                    # We can only hash files below, skip directories, links, etc.
                    if not debugsrc_path.is_file():
                        source_hash_cache[debugsrc_path] = None
                        continue

                    file_sha256 = bb.utils.sha256_file(debugsrc_path)
                    source_hash_cache[debugsrc_path] = file_sha256

                if file_sha256 in sources:
                    source_file = sources[file_sha256]
                    dep_source_files.add(source_file)
                else:
                    bb.debug(
                        1,
                        "Debug source %s with SHA256 %s not found in any dependency"
                        % (str(debugsrc_path), file_sha256),
                    )
                break
            else:
                bb.debug(1, "Debug source %s not found" % debugsrc)

    return dep_source_files


def collect_dep_objsets(d, direct_deps, subdir, fn_prefix, obj_type, **attr_filter):
    dep_objsets = []
    dep_objs = set()

    for dep in direct_deps:
        bb.debug(1, "Fetching SPDX for dependency %s" % (dep.pn))
        dep_obj, dep_objset = oe.sbom30.find_root_obj_in_jsonld(
            d, subdir, fn_prefix + dep.pn, obj_type, **attr_filter
        )
        # If the dependency is not part of the task hash, do not include it
        # since the dependency may not be present in subsequent runs, and may
        # not rebuild if it changes
        if not dep.in_taskhash:
            bb.debug(1, f"Skipping dependency {dep.pn} (not in taskhash)")
            continue

        dep_objsets.append(dep_objset)
        dep_objs.add(dep_obj)

    return dep_objsets, dep_objs


def index_sources_by_hash(sources, dest):
    for s in sources:
        if not isinstance(s, oe.spdx30.software_File):
            continue

        if s.software_primaryPurpose != oe.spdx30.software_SoftwarePurpose.source:
            continue

        for v in s.verifiedUsing:
            if v.algorithm == oe.spdx30.HashAlgorithm.sha256:
                if not v.hashValue in dest:
                    dest[v.hashValue] = s
                break
        else:
            bb.fatal(f"No SHA256 found for {s.name}")


def collect_dep_sources(dep_objsets, dest):
    for objset in dep_objsets:
        # Don't collect sources from native recipes as they
        # match non-native sources also.
        if objset.is_native():
            continue

        bb.debug(1, "Fetching Sources for dependency %s" % (objset.doc.name))

        dep_build = objset.find_root(oe.spdx30.build_Build)
        if not dep_build:
            bb.fatal("Unable to find a build")

        for e in objset.foreach_type(oe.spdx30.Relationship):
            if dep_build is not e.from_:
                continue

            if e.relationshipType != oe.spdx30.RelationshipType.hasInput:
                continue

            index_sources_by_hash(e.to, dest)


def generate_git_purl(d, download_location, srcrev):
    """Generate a Package URL for a Git source from its download location.

    Parses the Git URL to identify the hosting service and generates the
    appropriate PURL type. Supports github.com by default and custom
    mappings via SPDX_GIT_PURL_MAPPINGS.

    Returns the PURL string or None if no mapping matches.
    """
    if not download_location or not download_location.startswith("git+"):
        return None

    git_url = download_location[4:]  # Remove 'git+' prefix

    # Default handler: github.com
    git_purl_handlers = {
        "github.com": "pkg:github",
    }

    # Custom PURL mappings from SPDX_GIT_PURL_MAPPINGS
    # Format: "domain1:purl_type1 domain2:purl_type2"
    custom_mappings = d.getVar("SPDX_GIT_PURL_MAPPINGS")
    if custom_mappings:
        for mapping in custom_mappings.split():
            parts = mapping.split(":", 1)
            if len(parts) == 2:
                git_purl_handlers[parts[0]] = parts[1]
                bb.debug(2, f"Added custom Git PURL mapping: {parts[0]} -> {parts[1]}")
            else:
                bb.warn(
                    f"Invalid SPDX_GIT_PURL_MAPPINGS entry: {mapping} (expected format: domain:purl_type)"
                )

    try:
        parsed = urllib.parse.urlparse(git_url)
    except Exception:
        return None

    hostname = parsed.hostname
    if not hostname:
        return None

    for domain, purl_type in git_purl_handlers.items():
        if hostname == domain:
            # oe.spdx_common.fetch_data_to_uri() appends "@<rev>" to the
            # download location, which must not become part of the
            # repository name
            path = parsed.path.strip("/").partition("@")[0]
            path_parts = path.split("/")
            if len(path_parts) >= 2:
                owner = path_parts[0]
                repo = path_parts[1].removesuffix(".git")
                return f"{purl_type}/{owner}/{repo}@{srcrev}"
            break

    return None


def enrich_source_package(d, dl, fd, file_name, primary_purpose):
    """Enrich a Git source download package with version, PURL, and external refs.

    For Git sources, extracts the full SHA-1 from SRCREV as the version,
    generates PURLs for known hosting services, and adds VCS external
    references.
    """
    version = None
    purl = None

    if fd.type in ("git", "gitsm"):
        # Use full SHA-1 from fd.revision
        srcrev = getattr(fd, "revision", None)
        if srcrev and srcrev not in {"${AUTOREV}", "AUTOINC", "INVALID"}:
            version = srcrev

        # Generate PURL for Git hosting services
        download_location = getattr(dl, "software_downloadLocation", None)
        if version and download_location:
            purl = generate_git_purl(d, download_location, version)

    if version:
        dl.software_packageVersion = version

    if purl:
        dl.software_packageUrl = purl

    # Add VCS external reference for Git repositories
    download_location = getattr(dl, "software_downloadLocation", None)
    if download_location and isinstance(download_location, str):
        if download_location.startswith("git+"):
            git_url = download_location[4:]
            if "@" in git_url:
                git_url = git_url.split("@")[0]

            dl.externalRef = dl.externalRef or []
            dl.externalRef.append(
                oe.spdx30.ExternalRef(
                    externalRefType=oe.spdx30.ExternalRefType.vcs,
                    locator=[git_url],
                )
            )


def add_download_files(d, objset):
    inputs = set()
    release_date = get_release_date(d)

    urls = d.getVar("SRC_URI").split()
    fetch = bb.fetch.Fetch(urls, d)

    for download_idx, src_uri in enumerate(urls):
        fd = fetch.ud[src_uri]

        file_name = os.path.basename(fetch.localpath(src_uri))
        if oe.patch.patch_path(src_uri, fetch, "", expand=False):
            primary_purpose = oe.spdx30.software_SoftwarePurpose.patch
        else:
            primary_purpose = oe.spdx30.software_SoftwarePurpose.source

        if fd.type == "file":
            if os.path.isdir(fd.localpath):
                walk_idx = 1
                for root, dirs, files in os.walk(fd.localpath, onerror=walk_error):
                    dirs.sort()
                    files.sort()
                    for f in files:
                        f_path = os.path.join(root, f)
                        if os.path.islink(f_path):
                            # TODO: SPDX doesn't support symlinks yet
                            continue

                        file = objset.new_file(
                            objset.new_spdxid(
                                "source", str(download_idx + 1), str(walk_idx)
                            ),
                            os.path.join(
                                file_name, os.path.relpath(f_path, fd.localpath)
                            ),
                            f_path,
                            purposes=[primary_purpose],
                        )

                        inputs.add(file)
                        walk_idx += 1

            else:
                file = objset.new_file(
                    objset.new_spdxid("source", str(download_idx + 1)),
                    file_name,
                    fd.localpath,
                    purposes=[primary_purpose],
                )
                inputs.add(file)

        else:
            dl = objset.add(
                oe.spdx30.software_Package(
                    _id=objset.new_spdxid("source", str(download_idx + 1)),
                    creationInfo=objset.doc.creationInfo,
                    name=file_name,
                    software_primaryPurpose=primary_purpose,
                    software_downloadLocation=oe.spdx_common.fetch_data_to_uri(
                        fd, fd.name
                    ),
                )
            )

            enrich_source_package(d, dl, fd, file_name, primary_purpose)

            if release_date is not None:
                dl.releaseTime = release_date

            if fd.method.supports_checksum(fd):
                for checksum_id in bb.fetch.CHECKSUM_LIST:
                    if checksum_id not in oe.spdx30.HashAlgorithm.NAMED_INDIVIDUALS:
                        continue

                    expected_checksum = getattr(fd, "%s_expected" % checksum_id, None)
                    if expected_checksum is None:
                        continue

                    dl.verifiedUsing.append(
                        oe.spdx30.Hash(
                            algorithm=getattr(oe.spdx30.HashAlgorithm, checksum_id),
                            hashValue=expected_checksum,
                        )
                    )

            inputs.add(dl)

    return inputs


def set_purposes(d, element, *var_names, force_purposes=[]):
    purposes = force_purposes[:]

    for var_name in var_names:
        val = d.getVar(var_name)
        if val:
            purposes.extend(val.split())
            break

    if not purposes:
        bb.warn("No SPDX purposes found in %s" % " ".join(var_names))
        return

    element.software_primaryPurpose = getattr(
        oe.spdx30.software_SoftwarePurpose, purposes[0]
    )
    element.software_additionalPurpose = [
        getattr(oe.spdx30.software_SoftwarePurpose, p) for p in purposes[1:]
    ]


def add_custom_annotations(d, objset, obj):
    for var in (d.getVar("SPDX_CUSTOM_ANNOTATION_VARS") or "").split():
        objset.new_annotation(
            obj,
            "%s=%s" % (var, d.getVar(var)),
            oe.spdx30.AnnotationType.other,
        )


def set_purls(spdx_package, purls):
    if purls:
        spdx_package.software_packageUrl = purls[0]

    for p in sorted(set(purls)):
        spdx_package.externalIdentifier.append(
            oe.spdx30.ExternalIdentifier(
                externalIdentifierType=oe.spdx30.ExternalIdentifierType.packageUrl,
                identifier=p,
            )
        )


def get_is_native(d):
    return bb.data.inherits_class("native", d) or bb.data.inherits_class("cross", d)


def set_var_field(d, var, obj, name, package=None):
    val = None
    if package:
        val = d.getVar("%s:%s" % (var, package))

    if not val:
        val = d.getVar(var)

    if val:
        setattr(obj, name, val)


def find_build_dep_objsets(d, start_task):
    def find_deps(d, taskdepdata, current_dep, start_dep, visited, depth=0):
        key = f"{current_dep.pn}:{current_dep.taskname}"

        dep_objsets = []

        if key not in visited:
            visited.add(key)

            for n in current_dep.deps:
                dep = taskdepdata[n]
                dep_name = f"{dep.pn}:{dep.taskname}"

                dep_objset, dep_path = oe.sbom30.find_jsonld(d, "by-task", dep_name)
                if dep_objset:
                    dep_objsets.append(dep_objset)

                elif dep.pn == start_dep.pn:
                    # If this task is still part of the same recipe, continue
                    # searching up the dependency tree until a valid dependency
                    # is found. This detects transitive dependencies that may
                    # have been pulled in by previous tasks in the same recipe.
                    dep_objsets.extend(
                        find_deps(d, taskdepdata, dep, start_dep, visited, depth + 1)
                    )

        return dep_objsets

    pn = d.getVar("PN")
    taskdepdata = d.getVar("BB_TASKDEPDATA", False)
    for dep in taskdepdata.values():
        if dep.pn == pn and dep.taskname == start_task:
            start_dep = dep
            break
    else:
        bb.fatal(f"Unable to find {pn}:{start_task} in taskdepdata")

    return find_deps(d, taskdepdata, start_dep, start_dep, set())


def create_deploy_package(d, objset, build, spdxid, name, start_task, files, **attrs):
    recipe, _ = load_recipe_spdx(d)

    deploy_package = objset.add_root(
        oe.spdx30.software_Package(
            _id=spdxid,
            creationInfo=objset.doc.creationInfo,
            name=name,
            software_packageVersion=d.getVar("PV"),
        )
    )

    objset.new_scoped_relationship(
        [oe.sbom30.get_element_link_id(recipe)],
        oe.spdx30.RelationshipType.generates,
        oe.spdx30.LifecycleScopeType.build,
        [deploy_package],
    )

    set_var_field(d, "HOMEPAGE", deploy_package, "software_homePage")
    set_var_field(d, "SUMMARY", deploy_package, "summary")
    set_var_field(d, "DESCRIPTION", deploy_package, "description")

    set_purls(deploy_package, (d.getVar("SPDX_PACKAGE_URLS") or "").split())

    set_timestamp_now(d, deploy_package, "builtTime")

    supplier = objset.new_agent("SPDX_PACKAGE_SUPPLIER")
    if supplier is not None:
        deploy_package.suppliedBy = (
            supplier if isinstance(supplier, str) else supplier._id
        )

    if files:
        objset.new_relationship(
            [deploy_package],
            oe.spdx30.RelationshipType.contains,
            sorted(list(files)),
        )

    objset.new_scoped_relationship(
        [build],
        oe.spdx30.RelationshipType.hasOutput,
        oe.spdx30.LifecycleScopeType.build,
        sorted(list(files) + [deploy_package]),
    )

    # Collect dependencies
    if start_task is not None:
        dep_builds = set()
        dep_packages = set()
        for o in find_build_dep_objsets(d, start_task):
            if obj := o.find_root(oe.spdx30.software_Package):
                dep_packages.add(oe.sbom30.get_element_link_id(obj))

            if obj := o.find_root(oe.spdx30.build_Build):
                dep_builds.add(oe.sbom30.get_element_link_id(obj))

        if dep_packages:
            objset.new_scoped_relationship(
                [deploy_package],
                oe.spdx30.RelationshipType.dependsOn,
                oe.spdx30.LifecycleScopeType.build,
                sorted(list(dep_packages)),
            )

        if dep_builds:
            objset.new_scoped_relationship(
                [build],
                oe.spdx30.RelationshipType.dependsOn,
                oe.spdx30.LifecycleScopeType.build,
                sorted(list(dep_builds)),
            )

    return deploy_package


def create_recipe_spdx(d):
    deploydir = Path(d.getVar("SPDXRECIPEDEPLOY"))
    pn = d.getVar("PN")

    license_data = oe.spdx_common.load_spdx_license_data(d)

    include_vex = d.getVar("SPDX_INCLUDE_VEX")
    if not include_vex in ("none", "current", "all"):
        bb.fatal("SPDX_INCLUDE_VEX must be one of 'none', 'current', 'all'")

    recipe_objset = oe.sbom30.ObjectSet.new_objset(d, "static-" + pn)

    recipe = recipe_objset.add_root(
        oe.spdx30.software_Package(
            _id=recipe_objset.new_spdxid("recipe", pn),
            creationInfo=recipe_objset.doc.creationInfo,
            name=d.getVar("PN"),
            software_packageVersion=d.getVar("PV"),
            software_primaryPurpose=oe.spdx30.software_SoftwarePurpose.specification,
            software_sourceInfo=json.dumps(
                {
                    "FILENAME": os.path.basename(d.getVar("FILE")),
                    "FILE_LAYERNAME": d.getVar("FILE_LAYERNAME"),
                },
                separators=(",", ":"),
            ),
        )
    )

    if get_is_native(d):
        ext = oe.sbom30.OERecipeExtension()
        ext.is_native = True
        recipe.extension.append(ext)

    add_custom_annotations(d, recipe_objset, recipe)

    set_purls(recipe, (d.getVar("SPDX_PACKAGE_URLS") or "").split())

    # TODO: This doesn't work before do_unpack because the license text has to
    # be available for recipes with NO_GENERIC_LICENSE
    # recipe_spdx_license = add_license_expression(
    #    d,
    #    recipe_objset,
    #    d.getVar("LICENSE"),
    #    license_data,
    # )
    # recipe_objset.new_relationship(
    #    [recipe],
    #    oe.spdx30.RelationshipType.hasDeclaredLicense,
    #    [oe.sbom30.get_element_link_id(recipe_spdx_license)],
    # )

    if val := d.getVar("HOMEPAGE"):
        recipe.software_homePage = val

    if val := d.getVar("SUMMARY"):
        recipe.summary = val

    if val := d.getVar("DESCRIPTION"):
        recipe.description = val

    for cpe_id in oe.cve_check.get_cpe_ids(
        d.getVar("CVE_PRODUCT"), d.getVar("CVE_VERSION")
    ):
        recipe.externalIdentifier.append(
            oe.spdx30.ExternalIdentifier(
                externalIdentifierType=oe.spdx30.ExternalIdentifierType.cpe23,
                identifier=cpe_id,
            )
        )

    direct_deps = oe.spdx_common.collect_direct_deps(d, "do_create_recipe_spdx")

    dep_objsets, dep_recipes = collect_dep_objsets(
        d, direct_deps, "static", "static-", oe.spdx30.software_Package
    )

    if dep_recipes:
        recipe_objset.new_scoped_relationship(
            [recipe],
            oe.spdx30.RelationshipType.dependsOn,
            oe.spdx30.LifecycleScopeType.build,
            sorted(oe.sbom30.get_element_link_id(dep) for dep in dep_recipes),
        )

    # Add CVEs
    cve_by_status = {}
    if include_vex != "none":
        patched_cves = oe.cve_check.get_patched_cves(d)
        for cve, patched_cve in patched_cves.items():
            mapping = patched_cve["abbrev-status"]
            detail = patched_cve["status"]
            description = patched_cve.get("justification", None)
            resources = patched_cve.get("resource", [])

            # If this CVE is fixed upstream, skip it unless all CVEs are
            # specified.
            if include_vex != "all" and detail in (
                "fixed-version",
                "cpe-stable-backport",
            ):
                bb.debug(1, "Skipping %s since it is already fixed upstream" % cve)
                continue

            spdx_cve = recipe_objset.new_cve_vuln(cve)

            cve_by_status.setdefault(mapping, {})[cve] = (
                spdx_cve,
                detail,
                description,
                resources,
            )

    all_cves = set()
    for status, cves in cve_by_status.items():
        for cve, items in cves.items():
            spdx_cve, detail, description, resources = items
            spdx_cve_id = oe.sbom30.get_element_link_id(spdx_cve)

            all_cves.add(spdx_cve)

            if status == "Patched":
                spdx_vex = recipe_objset.new_vex_patched_relationship(
                    [spdx_cve_id],
                    [recipe],
                    notes=": ".join(v for v in (detail, description) if v),
                )
                patches = []
                for idx, filepath in enumerate(resources):
                    patches.append(
                        recipe_objset.new_file(
                            recipe_objset.new_spdxid(
                                "patch", str(idx), os.path.basename(filepath)
                            ),
                            os.path.basename(filepath),
                            filepath,
                            purposes=[oe.spdx30.software_SoftwarePurpose.patch],
                            hashfile=os.path.isfile(filepath),
                        )
                    )

                if patches:
                    recipe_objset.new_scoped_relationship(
                        spdx_vex,
                        oe.spdx30.RelationshipType.patchedBy,
                        oe.spdx30.LifecycleScopeType.build,
                        patches,
                    )

            elif status == "Unpatched":
                recipe_objset.new_vex_unpatched_relationship(
                    [spdx_cve_id],
                    [recipe],
                    notes=": ".join(v for v in (detail, description) if v),
                )
            elif status == "Ignored":
                spdx_vex = recipe_objset.new_vex_ignored_relationship(
                    [spdx_cve_id],
                    [recipe],
                    impact_statement=description,
                    notes=detail,
                )

                vex_just_type = d.getVarFlag("CVE_CHECK_VEX_JUSTIFICATION", detail)
                if vex_just_type:
                    if (
                        vex_just_type
                        not in oe.spdx30.security_VexJustificationType.NAMED_INDIVIDUALS
                    ):
                        bb.fatal(
                            f"Unknown vex justification '{vex_just_type}', detail '{detail}', for ignored {cve}"
                        )

                    for v in spdx_vex:
                        v.security_justificationType = (
                            oe.spdx30.security_VexJustificationType.NAMED_INDIVIDUALS[
                                vex_just_type
                            ]
                        )

            elif status == "Unknown":
                bb.note(f"Skipping {cve} with status 'Unknown'")
            else:
                bb.fatal(f"Unknown {cve} status '{status}'")

    if all_cves:
        recipe_objset.new_relationship(
            [recipe],
            oe.spdx30.RelationshipType.hasAssociatedVulnerability,
            sorted(list(all_cves)),
        )

    oe.sbom30.write_recipe_jsonld_doc(
        d, recipe_objset, "static", deploydir, create_task_link=True
    )


def load_recipe_spdx(d):

    return oe.sbom30.find_root_obj_in_jsonld(
        d,
        "static",
        "static-" + d.getVar("PN"),
        oe.spdx30.software_Package,
    )


def create_spdx(d):
    license_data = oe.spdx_common.load_spdx_license_data(d)

    pn = d.getVar("PN")
    deploydir = Path(d.getVar("SPDXDEPLOY"))
    spdx_workdir = Path(d.getVar("SPDXWORK"))
    include_sources = d.getVar("SPDX_INCLUDE_SOURCES") == "1"
    is_native = get_is_native(d)

    recipe, recipe_objset = load_recipe_spdx(d)

    build_objset = oe.sbom30.ObjectSet.new_objset(d, "build-" + pn)

    build = build_objset.new_task_build("recipe", "recipe")
    build_objset.set_element_alias(build)

    build_objset.doc.rootElement.append(build)

    build_objset.set_is_native(is_native)

    add_custom_annotations(d, build_objset, build)

    build_inputs = set()

    cpe_ids = oe.cve_check.get_cpe_ids(d.getVar("CVE_PRODUCT"), d.getVar("CVE_VERSION"))

    source_files = add_download_files(d, build_objset)
    build_inputs |= source_files

    recipe_spdx_license = add_license_expression(
        d, build_objset, d.getVar("LICENSE"), license_data, [recipe_objset]
    )
    if recipe_spdx_license:
        build_objset.new_relationship(
            source_files,
            oe.spdx30.RelationshipType.hasDeclaredLicense,
            [oe.sbom30.get_element_link_id(recipe_spdx_license)],
        )
    else:
        build_objset.new_relationship(
            source_files,
            oe.spdx30.RelationshipType.hasDeclaredLicense,
            None,
        )

    dep_sources = {}
    if oe.spdx_common.process_sources(d) and include_sources:
        bb.debug(1, "Adding source files to SPDX")
        oe.spdx_common.get_patched_src(d)

        files, _ = add_package_files(
            d,
            build_objset,
            spdx_workdir,
            lambda file_counter: build_objset.new_spdxid(
                "sourcefile", str(file_counter)
            ),
            lambda filepath: [oe.spdx30.software_SoftwarePurpose.source],
            license_data,
            ignore_dirs=[".git"],
            archive=None,
        )
        build_inputs |= files
        index_sources_by_hash(files, dep_sources)

    direct_deps = oe.spdx_common.collect_direct_deps(d, "do_create_spdx")

    dep_objsets, dep_builds = collect_dep_objsets(
        d, direct_deps, "builds", "build-", oe.spdx30.build_Build
    )

    if dep_builds:
        build_objset.new_scoped_relationship(
            [build],
            oe.spdx30.RelationshipType.dependsOn,
            oe.spdx30.LifecycleScopeType.build,
            sorted(oe.sbom30.get_element_link_id(b) for b in dep_builds),
        )

    debug_sources = set()
    source_hash_cache = {}

    # Write out the package SPDX data now. It is not complete as we cannot
    # write the runtime data, so write it to a staging area and a later task
    # will write out the final collection

    # TODO: Handle native recipe output
    if not is_native:
        bb.debug(1, "Collecting Dependency sources files")
        collect_dep_sources(dep_objsets, dep_sources)

        bb.build.exec_func("read_subpackage_metadata", d)

        pkgdest = Path(d.getVar("PKGDEST"))
        for package in d.getVar("PACKAGES").split():
            if not oe.packagedata.packaged(package, d):
                continue

            pkg_name = d.getVar("PKG:%s" % package) or package

            bb.debug(1, "Creating SPDX for package %s" % pkg_name)

            pkg_objset = oe.sbom30.ObjectSet.new_objset(d, "package-" + pkg_name)

            spdx_package = pkg_objset.add_root(
                oe.spdx30.software_Package(
                    _id=pkg_objset.new_spdxid("package", pkg_name),
                    creationInfo=pkg_objset.doc.creationInfo,
                    name=pkg_name,
                    software_packageVersion=d.getVar("SPDX_PACKAGE_VERSION"),
                )
            )
            set_timestamp_now(d, spdx_package, "builtTime")

            set_purposes(
                d,
                spdx_package,
                "SPDX_PACKAGE_ADDITIONAL_PURPOSE:%s" % package,
                "SPDX_PACKAGE_ADDITIONAL_PURPOSE",
                force_purposes=["install"],
            )

            supplier = build_objset.new_agent("SPDX_PACKAGE_SUPPLIER")
            if supplier is not None:
                spdx_package.suppliedBy = (
                    supplier if isinstance(supplier, str) else supplier._id
                )

            set_var_field(
                d, "HOMEPAGE", spdx_package, "software_homePage", package=package
            )
            set_var_field(d, "SUMMARY", spdx_package, "summary", package=package)
            set_var_field(
                d, "DESCRIPTION", spdx_package, "description", package=package
            )

            purls = (
                d.getVar("SPDX_PACKAGE_URLS:%s" % package)
                or d.getVar("SPDX_PACKAGE_URLS")
                or ""
            ).split()

            set_purls(spdx_package, purls)

            pkg_objset.new_scoped_relationship(
                [oe.sbom30.get_element_link_id(build)],
                oe.spdx30.RelationshipType.hasOutput,
                oe.spdx30.LifecycleScopeType.build,
                [spdx_package],
            )

            pkg_objset.new_scoped_relationship(
                [oe.sbom30.get_element_link_id(recipe)],
                oe.spdx30.RelationshipType.generates,
                oe.spdx30.LifecycleScopeType.build,
                [spdx_package],
            )

            for cpe_id in cpe_ids:
                spdx_package.externalIdentifier.append(
                    oe.spdx30.ExternalIdentifier(
                        externalIdentifierType=oe.spdx30.ExternalIdentifierType.cpe23,
                        identifier=cpe_id,
                    )
                )

            # TODO: Generate a file for each actual IPK/DEB/RPM/TGZ file
            # generated and link it to the package
            # spdx_package_file = pkg_objset.add(oe.spdx30.software_File(
            #    _id=pkg_objset.new_spdxid("distribution", pkg_name),
            #    creationInfo=pkg_objset.doc.creationInfo,
            #    name=pkg_name,
            #    software_primaryPurpose=spdx_package.software_primaryPurpose,
            #    software_additionalPurpose=spdx_package.software_additionalPurpose,
            # ))
            # set_timestamp_now(d, spdx_package_file, "builtTime")

            ## TODO add hashes
            # pkg_objset.new_relationship(
            #    [spdx_package],
            #    oe.spdx30.RelationshipType.hasDistributionArtifact,
            #    [spdx_package_file],
            # )

            # NOTE: licenses live in the recipe collection and are referenced
            # by ID in the package collection(s). This helps reduce duplication
            # (since a lot of packages will have the same license), and also
            # prevents duplicate license SPDX IDs in the packages
            package_license = d.getVar("LICENSE:%s" % package)
            if package_license and package_license != d.getVar("LICENSE"):
                package_spdx_license = add_license_expression(
                    d,
                    build_objset,
                    package_license,
                    license_data,
                    [recipe_objset],
                )
            else:
                package_spdx_license = recipe_spdx_license

            if package_spdx_license:
                pkg_objset.new_relationship(
                    [spdx_package],
                    oe.spdx30.RelationshipType.hasDeclaredLicense,
                    [oe.sbom30.get_element_link_id(package_spdx_license)],
                )
            else:
                pkg_objset.new_relationship(
                    [spdx_package],
                    oe.spdx30.RelationshipType.hasDeclaredLicense,
                    None,
                )

            # Add concluded license relationship if manually set
            # Only add when license analysis has been explicitly performed
            concluded_license_str = d.getVar(
                "SPDX_CONCLUDED_LICENSE:%s" % package
            ) or d.getVar("SPDX_CONCLUDED_LICENSE")
            if concluded_license_str:
                concluded_spdx_license = add_license_expression(
                    d, build_objset, concluded_license_str, license_data
                )
                if concluded_spdx_license:
                    pkg_objset.new_relationship(
                        [spdx_package],
                        oe.spdx30.RelationshipType.hasConcludedLicense,
                        [oe.sbom30.get_element_link_id(concluded_spdx_license)],
                    )

            bb.debug(1, "Adding package files to SPDX for package %s" % pkg_name)
            package_files, excluded_files = add_package_files(
                d,
                pkg_objset,
                pkgdest / package,
                lambda file_counter: pkg_objset.new_spdxid(
                    "package", pkg_name, "file", str(file_counter)
                ),
                # TODO: Can we know the purpose here?
                lambda filepath: [],
                license_data,
                ignore_top_level_dirs=["CONTROL", "DEBIAN"],
                archive=None,
            )

            if package_files:
                pkg_objset.new_relationship(
                    [spdx_package],
                    oe.spdx30.RelationshipType.contains,
                    sorted(list(package_files)),
                )

            if include_sources:
                debug_sources |= get_package_sources_from_debug(
                    d,
                    package,
                    package_files,
                    dep_sources,
                    source_hash_cache,
                    excluded_files=excluded_files,
                )

            oe.sbom30.write_recipe_jsonld_doc(
                d, pkg_objset, "packages-staging", deploydir, create_spdx_id_links=False
            )

    if include_sources:
        bb.debug(1, "Adding sysroot files to SPDX")
        sysroot_files, _ = add_package_files(
            d,
            build_objset,
            d.expand("${COMPONENTS_DIR}/${PACKAGE_ARCH}/${PN}"),
            lambda file_counter: build_objset.new_spdxid("sysroot", str(file_counter)),
            lambda filepath: [],
            license_data,
            archive=None,
        )

        if sysroot_files:
            build_objset.new_scoped_relationship(
                [build],
                oe.spdx30.RelationshipType.hasOutput,
                oe.spdx30.LifecycleScopeType.build,
                sorted(list(sysroot_files)),
            )

    if build_inputs or debug_sources:
        debug_source_ids = sorted(
            oe.sbom30.get_element_link_id(d)
            for d in debug_sources.difference(build_inputs)
        )

        build_objset.new_scoped_relationship(
            [build],
            oe.spdx30.RelationshipType.hasInput,
            oe.spdx30.LifecycleScopeType.build,
            sorted(build_inputs) + debug_source_ids,
        )

    if d.getVar("SPDX_INCLUDE_PACKAGECONFIG", True) != "0":
        packageconfig = (d.getVar("PACKAGECONFIG") or "").split()
        all_features = set((d.getVarFlags("PACKAGECONFIG") or {}).keys())
        blacklisted_features = {"doc"}

        if all_features:
            enabled = set(packageconfig)
            disabled = all_features - enabled
            all_features -= disabled & blacklisted_features
            disabled -= blacklisted_features

            for feature in sorted(all_features):
                status = "enabled" if feature in enabled else "disabled"
                build.build_parameter.append(
                    oe.spdx30.DictionaryEntry(
                        key=f"PACKAGECONFIG:{feature}", value=status
                    )
                )

            bb.note(
                f"Added PACKAGECONFIG entries: {len(enabled)} enabled, {len(disabled)} disabled"
            )

    oe.sbom30.write_recipe_jsonld_doc(
        d, build_objset, "builds", deploydir, create_task_link=True
    )


def create_package_spdx(d):
    deploydir = Path(d.getVar("SPDXRUNTIMEDEPLOY"))
    pkgdatadir = Path(d.getVar('PKGDATA_DIR'))

    direct_deps = oe.spdx_common.collect_direct_deps(d, "do_create_spdx")

    # providers[package_name] = pkg_name, where:
    # - package_name is the name in the recipe in PACKAGES/RDEPENDS, and
    # - pkg_name is the final name given by the package manager.
    providers = oe.spdx_common.collect_package_providers(d, direct_deps)
    pkg_arch = d.getVar("SSTATE_PKGARCH")

    if get_is_native(d):
        return

    bb.build.exec_func("read_subpackage_metadata", d)

    dep_package_cache = {}

    # Any element common to all packages that need to be referenced by ID
    # should be written into this objset set
    common_objset = oe.sbom30.ObjectSet.new_objset(
        d, "%s-package-common" % d.getVar("PN")
    )

    for package in d.getVar("PACKAGES").split():
        localdata = bb.data.createCopy(d)
        pkg_name = d.getVar("PKG:%s" % package) or package
        localdata.setVar("PKG", pkg_name)
        localdata.setVar("OVERRIDES", d.getVar("OVERRIDES", False) + ":" + package)

        if not oe.packagedata.packaged(package, localdata):
            continue

        spdx_package, pkg_objset = oe.sbom30.load_obj_in_jsonld(
            d,
            pkg_arch,
            "packages-staging",
            "package-" + pkg_name,
            oe.spdx30.software_Package,
            software_primaryPurpose=oe.spdx30.software_SoftwarePurpose.install,
        )

        # We will write out a new collection, so link it to the new
        # creation info in the common package data. The old creation info
        # should still exist and be referenced by all the existing elements
        # in the package
        pkg_objset.creationInfo = pkg_objset.copy_creation_info(
            common_objset.doc.creationInfo
        )

        runtime_spdx_deps = set()

        deps = bb.utils.explode_dep_versions2(localdata.getVar("RDEPENDS") or "")
        seen_deps = set()
        for dep, _ in deps.items():
            if dep in seen_deps:
                continue

            if dep not in providers:
                continue

            dep_pkg_name, _ = providers[dep]

            if not os.path.exists(pkgdatadir / "runtime-reverse" / dep_pkg_name):
                bb.debug(1, f"pkg_name {dep_pkg_name}, package {dep} missing for {package}")
                continue

            if dep_pkg_name in dep_package_cache:
                dep_spdx_package = dep_package_cache[dep_pkg_name]
            else:
                bb.debug(1, "Searching for %s" % dep_pkg_name)
                dep_spdx_package, _ = oe.sbom30.find_root_obj_in_jsonld(
                    d,
                    "packages-staging",
                    "package-" + dep_pkg_name,
                    oe.spdx30.software_Package,
                    software_primaryPurpose=oe.spdx30.software_SoftwarePurpose.install,
                )
                dep_package_cache[dep_pkg_name] = dep_spdx_package

            runtime_spdx_deps.add(dep_spdx_package)
            seen_deps.add(dep)

        if runtime_spdx_deps:
            pkg_objset.new_scoped_relationship(
                [spdx_package],
                oe.spdx30.RelationshipType.dependsOn,
                oe.spdx30.LifecycleScopeType.runtime,
                [oe.sbom30.get_element_link_id(dep) for dep in runtime_spdx_deps],
            )

        oe.sbom30.write_recipe_jsonld_doc(d, pkg_objset, "packages", deploydir)

    oe.sbom30.write_recipe_jsonld_doc(d, common_objset, "common-package", deploydir)


def write_bitbake_spdx(d):
    # Set PN to "bitbake" so that SPDX IDs can be generated
    d.setVar("PN", "bitbake")
    d.setVar("BB_TASKHASH", "bitbake")
    oe.spdx_common.load_spdx_license_data(d)

    deploy_dir_spdx = Path(d.getVar("DEPLOY_DIR_SPDX"))

    objset = oe.sbom30.ObjectSet.new_objset(d, "bitbake", False)

    host_import_key = d.getVar("SPDX_BUILD_HOST")
    invoked_by = objset.new_agent("SPDX_INVOKED_BY", add=False)
    on_behalf_of = objset.new_agent("SPDX_ON_BEHALF_OF", add=False)

    if d.getVar("SPDX_INCLUDE_BITBAKE_PARENT_BUILD") == "1":
        # Since the Build objects are unique, we may as well set the creation
        # time to the current time instead of the fallback SDE
        objset.doc.creationInfo.created = datetime.now(timezone.utc)

        # Each invocation of bitbake should have a unique ID since it is a
        # unique build
        nonce = os.urandom(16).hex()

        build = objset.add_root(
            oe.spdx30.build_Build(
                _id=objset.new_spdxid(nonce, include_unihash=False),
                creationInfo=objset.doc.creationInfo,
                build_buildType=oe.sbom30.SPDX_BUILD_TYPE,
            )
        )
        set_timestamp_now(d, build, "build_buildStartTime")

        if host_import_key:
            objset.new_scoped_relationship(
                [build],
                oe.spdx30.RelationshipType.hasHost,
                oe.spdx30.LifecycleScopeType.build,
                [objset.new_import(host_import_key)],
            )

        if invoked_by:
            objset.add(invoked_by)
            invoked_by_spdx = objset.new_scoped_relationship(
                [build],
                oe.spdx30.RelationshipType.invokedBy,
                oe.spdx30.LifecycleScopeType.build,
                [invoked_by],
            )

            if on_behalf_of:
                objset.add(on_behalf_of)
                objset.new_scoped_relationship(
                    [on_behalf_of],
                    oe.spdx30.RelationshipType.delegatedTo,
                    oe.spdx30.LifecycleScopeType.build,
                    invoked_by_spdx,
                )

        elif on_behalf_of:
            bb.warn("SPDX_ON_BEHALF_OF has no effect if SPDX_INVOKED_BY is not set")

    else:
        if host_import_key:
            bb.warn(
                "SPDX_BUILD_HOST has no effect if SPDX_INCLUDE_BITBAKE_PARENT_BUILD is not set"
            )

        if invoked_by:
            bb.warn(
                "SPDX_INVOKED_BY has no effect if SPDX_INCLUDE_BITBAKE_PARENT_BUILD is not set"
            )

        if on_behalf_of:
            bb.warn(
                "SPDX_ON_BEHALF_OF has no effect if SPDX_INCLUDE_BITBAKE_PARENT_BUILD is not set"
            )

    for obj in objset.foreach_type(oe.spdx30.Element):
        obj.extension.append(oe.sbom30.OEIdAliasExtension())

    oe.sbom30.write_jsonld_doc(d, objset, deploy_dir_spdx / "bitbake.spdx.json")


def collect_build_package_inputs(d, objset, build, packages, files_by_hash=None):
    import oe.sbom30

    direct_deps = oe.spdx_common.collect_direct_deps(d, "do_create_spdx")

    providers = oe.spdx_common.collect_package_providers(d, direct_deps)

    build_deps = set()

    for name in sorted(packages.keys()):
        if name not in providers:
            bb.note(f"Unable to find SPDX provider for '{name}'")
            continue

        pkg_name, pkg_hashfn = providers[name]

        # Copy all of the package SPDX files into the Sbom elements
        pkg_spdx, pkg_objset = oe.sbom30.find_root_obj_in_jsonld(
            d,
            "packages",
            "package-" + pkg_name,
            oe.spdx30.software_Package,
            software_primaryPurpose=oe.spdx30.software_SoftwarePurpose.install,
        )
        build_deps.add(oe.sbom30.get_element_link_id(pkg_spdx))

        if files_by_hash is not None:
            for h, f in pkg_objset.by_sha256_hash.items():
                files_by_hash.setdefault(h, set()).update(f)

    if build_deps:
        objset.new_scoped_relationship(
            [build],
            oe.spdx30.RelationshipType.hasInput,
            oe.spdx30.LifecycleScopeType.build,
            sorted(list(build_deps)),
        )


def create_rootfs_spdx(d):
    deploydir = Path(d.getVar("SPDXROOTFSDEPLOY"))
    root_packages_file = Path(d.getVar("SPDX_ROOTFS_PACKAGES"))
    image_basename = d.getVar("IMAGE_BASENAME")
    image_rootfs = d.getVar("IMAGE_ROOTFS")
    machine = d.getVar("MACHINE")

    with root_packages_file.open("r") as f:
        packages = json.load(f)

    objset = oe.sbom30.ObjectSet.new_objset(
        d, "%s-%s-rootfs" % (image_basename, machine)
    )

    rootfs_build = objset.add_root(objset.new_task_build("rootfs", "rootfs"))
    set_timestamp_now(d, rootfs_build, "build_buildEndTime")

    files_by_hash = {}
    collect_build_package_inputs(d, objset, rootfs_build, packages, files_by_hash)

    files = set()
    for dirpath, dirnames, filenames in os.walk(image_rootfs, onerror=walk_error):
        dirnames.sort()
        filenames.sort()
        for fn in filenames:
            fpath = Path(dirpath) / fn
            if fpath.is_symlink() or not fpath.is_file():
                continue

            relpath = str(fpath.relative_to(image_rootfs))
            h = bb.utils.sha256_file(fpath)

            found = False
            if h in files_by_hash:
                for f in files_by_hash[h]:
                    if isinstance(f, oe.spdx30.software_File) and f.name == relpath:
                        files.add(oe.sbom30.get_element_link_id(f))
                        found = True
                        break

            if not found:
                files.add(
                    objset.new_file(
                        objset.new_spdxid("rootfs-file", relpath),
                        relpath,
                        fpath,
                    )
                )

    rootfs = create_deploy_package(
        d,
        objset,
        rootfs_build,
        objset.new_spdxid("rootfs", image_basename),
        image_basename,
        None,
        files,
    )
    rootfs.software_primaryPurpose = oe.spdx30.software_SoftwarePurpose.archive

    oe.sbom30.write_recipe_jsonld_doc(
        d, objset, "rootfs", deploydir, create_task_link=True
    )


def create_image_spdx(d, spdx_deploy_dir):
    import oe.sbom30

    pn = d.getVar("PN")
    current_task = "do_" + d.getVar("BB_CURRENTTASK")

    image_deploy_dir = Path(d.getVar("IMGDEPLOYDIR"))
    manifest_path = Path(d.getVar("IMAGE_OUTPUT_MANIFEST"))

    image_basename = d.getVar("IMAGE_BASENAME")
    machine = d.getVar("MACHINE")

    objset = oe.sbom30.ObjectSet.new_objset(d, f"{pn}-{current_task}-deploy")

    with manifest_path.open("r") as f:
        manifest = json.load(f)

    builds = []
    for task in manifest:
        imagetype = task["imagetype"]
        taskname = task["taskname"]

        image_build = objset.add_root(
            objset.new_task_build(taskname, "image/%s" % imagetype)
        )
        set_timestamp_now(d, image_build, "build_buildEndTime")
        builds.append(image_build)

        artifacts = []

        for image in task["images"]:
            image_filename = image["filename"]
            image_path = image_deploy_dir / image_filename
            if os.path.isdir(image_path):
                a, _ = add_package_files(
                    d,
                    objset,
                    image_path,
                    lambda file_counter: objset.new_spdxid(
                        "imagefile", str(file_counter)
                    ),
                    lambda filepath: [],
                    license_data=None,
                    ignore_dirs=[],
                    ignore_top_level_dirs=[],
                    archive=None,
                )
                artifacts.extend(a)
            else:
                a = objset.add_root(
                    oe.spdx30.software_File(
                        _id=objset.new_spdxid("image", image_filename),
                        creationInfo=objset.doc.creationInfo,
                        name=image_filename,
                        verifiedUsing=[
                            oe.spdx30.Hash(
                                algorithm=oe.spdx30.HashAlgorithm.sha256,
                                hashValue=bb.utils.sha256_file(image_path),
                            ),
                            oe.spdx30.Hash(
                                algorithm=oe.spdx30.HashAlgorithm.sha512,
                                hashValue=bb.utils.sha512_file(image_path),
                            ),
                        ],
                    )
                )

                artifacts.append(a)

            for a in artifacts:
                set_purposes(
                    d, a, "SPDX_IMAGE_PURPOSE:%s" % imagetype, "SPDX_IMAGE_PURPOSE"
                )

                set_timestamp_now(d, a, "builtTime")

        if artifacts:
            create_deploy_package(
                d,
                objset,
                image_build,
                objset.new_spdxid(taskname, "image", imagetype),
                "image",
                f"do_{taskname}",
                artifacts,
            )

    if builds:
        rootfs_image, _ = oe.sbom30.find_root_obj_in_jsonld(
            d,
            "rootfs",
            "%s-%s-rootfs" % (image_basename, machine),
            oe.spdx30.software_Package,
            # TODO: Should use a purpose to filter here?
        )
        objset.new_scoped_relationship(
            builds,
            oe.spdx30.RelationshipType.hasInput,
            oe.spdx30.LifecycleScopeType.build,
            [oe.sbom30.get_element_link_id(rootfs_image)],
        )

    objset.add_aliases()
    objset.link()
    oe.sbom30.write_recipe_jsonld_doc(
        d,
        objset,
        "deploy",
        spdx_deploy_dir,
        create_task_link=True,
    )


def create_image_sbom_spdx(d):
    import oe.sbom30

    pn = d.getVar("PN")
    image_name = d.getVar("IMAGE_NAME")
    image_basename = d.getVar("IMAGE_BASENAME")
    image_link_name = d.getVar("IMAGE_LINK_NAME")
    imgdeploydir = Path(d.getVar("SPDXIMAGEDEPLOYDIR"))
    machine = d.getVar("MACHINE")

    spdx_path = imgdeploydir / (image_name + ".spdx.json")

    root_elements = []

    # TODO: Do we need to add the rootfs or are the image files sufficient?
    rootfs_image, _ = oe.sbom30.find_root_obj_in_jsonld(
        d,
        "rootfs",
        "%s-%s-rootfs" % (image_basename, machine),
        oe.spdx30.software_Package,
        # TODO: Should use a purpose here?
    )
    root_elements.append(oe.sbom30.get_element_link_id(rootfs_image))

    image_objset, _ = oe.sbom30.find_jsonld(
        d,
        "deploy",
        f"{pn}-do_image_complete-deploy",
    )
    for o in image_objset.foreach_root(oe.spdx30.software_File):
        root_elements.append(oe.sbom30.get_element_link_id(o))

    objset, sbom = oe.sbom30.create_sbom(d, image_name, root_elements)

    # Set supplier on root elements if SPDX_IMAGE_SUPPLIER is defined
    supplier = objset.new_agent("SPDX_IMAGE_SUPPLIER", add=False)
    if supplier is not None:
        supplier_id = supplier if isinstance(supplier, str) else supplier._id
        if not isinstance(supplier, str):
            objset.add(supplier)
        for elem in sbom.rootElement:
            if hasattr(elem, "suppliedBy"):
                elem.suppliedBy = supplier_id

    oe.sbom30.write_jsonld_doc(d, objset, spdx_path)

    def make_image_link(target_path, suffix):
        if image_link_name:
            link = imgdeploydir / (image_link_name + suffix)
            if link != target_path:
                link.symlink_to(os.path.relpath(target_path, link.parent))

    make_image_link(spdx_path, ".spdx.json")


def sdk_create_spdx(d, sdk_type, spdx_work_dir, toolchain_outputname):
    sdk_name = toolchain_outputname + "-" + sdk_type
    sdk_packages = oe.sdk.sdk_list_installed_packages(d, sdk_type == "target")

    objset = oe.sbom30.ObjectSet.new_objset(d, sdk_name)

    sdk_rootfs = objset.add_root(
        oe.spdx30.software_Package(
            _id=objset.new_spdxid("sdk-rootfs", sdk_name),
            creationInfo=objset.doc.creationInfo,
            name=sdk_name,
            software_primaryPurpose=oe.spdx30.software_SoftwarePurpose.archive,
        )
    )
    set_timestamp_now(d, sdk_rootfs, "builtTime")

    sdk_build = objset.add_root(
        objset.new_task_build("sdk-rootfs-" + sdk_type, "sdk-rootfs")
    )
    set_timestamp_now(d, sdk_build, "build_buildEndTime")

    objset.new_scoped_relationship(
        [sdk_build],
        oe.spdx30.RelationshipType.hasOutput,
        oe.spdx30.LifecycleScopeType.build,
        [sdk_rootfs],
    )

    collect_build_package_inputs(d, objset, sdk_build, sdk_packages)

    objset.add_aliases()
    oe.sbom30.write_jsonld_doc(
        d, objset, spdx_work_dir / ("sdk-rootfs-%s.spdx.json" % sdk_type)
    )


def create_sdk_sbom(d, sdk_deploydir, spdx_work_dir, toolchain_outputname):
    # Load the target and host documents written earlier
    rootfs_objsets = []
    rootfs = []
    for sdk_type in ("target", "host"):
        objset = oe.sbom30.load_jsonld(
            d, spdx_work_dir / ("sdk-rootfs-%s.spdx.json" % sdk_type), required=True
        )
        root = objset.find_root(oe.spdx30.software_Package)
        if root is None:
            bb.fatal("Unable to find %s rootfs artifact" % sdk_type)
        rootfs_objsets.append(objset)
        rootfs.append(root)

    rootfs_objset = rootfs_objsets[0]

    # Create a new build for the SDK installer
    sdk_build = rootfs_objset.new_task_build("sdk-populate", "sdk-populate")
    set_timestamp_now(d, sdk_build, "build_buildEndTime")

    rootfs_objset.new_scoped_relationship(
        [sdk_build],
        oe.spdx30.RelationshipType.hasInput,
        oe.spdx30.LifecycleScopeType.build,
        rootfs,
    )

    files = set()
    root_files = []

    # NOTE: os.walk() doesn't return symlinks
    for dirpath, dirnames, filenames in os.walk(sdk_deploydir, onerror=walk_error):
        dirnames.sort()
        filenames.sort()
        for fn in filenames:
            fpath = Path(dirpath) / fn
            if not fpath.is_file() or fpath.is_symlink():
                continue

            relpath = str(fpath.relative_to(sdk_deploydir))

            f = rootfs_objset.new_file(
                rootfs_objset.new_spdxid("sdk-installer", relpath),
                relpath,
                fpath,
            )
            set_timestamp_now(d, f, "builtTime")

            if fn.endswith(".manifest"):
                f.software_primaryPurpose = oe.spdx30.software_SoftwarePurpose.manifest
            elif fn.endswith(".testdata.json"):
                f.software_primaryPurpose = (
                    oe.spdx30.software_SoftwarePurpose.configuration
                )
            else:
                set_purposes(d, f, "SPDX_SDK_PURPOSE")
                root_files.append(f)

            files.add(f)

    if files:
        rootfs_objset.new_scoped_relationship(
            [sdk_build],
            oe.spdx30.RelationshipType.hasOutput,
            oe.spdx30.LifecycleScopeType.build,
            files,
        )
    else:
        bb.warn(f"No SDK output files found in {sdk_deploydir}")

    objset, sbom = oe.sbom30.create_sbom(
        d, toolchain_outputname, sorted(list(files)), rootfs_objsets
    )

    # Set supplier on root elements if SPDX_SDK_SUPPLIER is defined
    supplier = objset.new_agent("SPDX_SDK_SUPPLIER", add=False)
    if supplier is not None:
        supplier_id = supplier if isinstance(supplier, str) else supplier._id
        if not isinstance(supplier, str):
            objset.add(supplier)
        for elem in sbom.rootElement:
            if hasattr(elem, "suppliedBy"):
                elem.suppliedBy = supplier_id

    oe.sbom30.write_jsonld_doc(
        d, objset, sdk_deploydir / (toolchain_outputname + ".spdx.json")
    )


def create_recipe_sbom(d, deploydir):
    sbom_name = d.getVar("SPDX_RECIPE_SBOM_NAME")

    recipe, recipe_objset = load_recipe_spdx(d)

    objset, sbom = oe.sbom30.create_sbom(d, sbom_name, [recipe], [recipe_objset])

    oe.sbom30.write_jsonld_doc(d, objset, deploydir / (sbom_name + ".spdx.json"))


def create_deploy_spdx(d, spdxdeploydir, artifactsdir, artifacts):
    pn = d.getVar("PN")
    current_task = "do_" + d.getVar("BB_CURRENTTASK")

    recipe, recipe_objset = load_recipe_spdx(d)

    if artifacts == "AUTO":
        artifacts = []
        for root, dirs, files in os.walk(artifactsdir):
            for p in [Path(os.path.join(root, f)) for f in files]:
                if p.is_file():
                    artifacts.append(p)
    else:
        artifacts = [artifactsdir / p for p in artifacts.split()]

    artifacts.sort(key=lambda p: (p.is_symlink(), p))

    objset = oe.sbom30.ObjectSet.new_objset(d, f"{pn}-{current_task}-deploy")

    build = objset.add_root(objset.new_task_build(current_task, "deploy"))
    set_timestamp_now(d, build, "build_buildEndTime")
    objset.set_is_native(get_is_native(d))

    files = set()
    for a in artifacts:
        relpath = a.relative_to(artifactsdir)
        f = objset.new_file(
            objset.new_spdxid("deploy", str(relpath)),
            a.name,
            a,
        )
        files.add(f)

    if not files:
        if (
            d.getVar("SPDX_DEPLOY_ALLOW_EMPTY:task-" + d.getVar("BB_CURRENTTASK")) or ""
        ) != "1":
            bb.fatal(f"No deployed artifacts found in {artifactsdir}")
            return

    create_deploy_package(
        d,
        objset,
        build,
        objset.new_spdxid("deploy", pn, current_task),
        pn,
        current_task,
        files,
    )

    # Create document
    dest = oe.sbom30.write_recipe_jsonld_doc(
        d,
        objset,
        "deploy",
        spdxdeploydir,
        create_task_link=True,
    )


def create_deploy_sbom(d, deploydir, deploy_tasks):
    pn = d.getVar("PN")
    sbom_name = d.getVar("SPDX_DEPLOY_SBOM_NAME")

    objsets = []
    for t in deploy_tasks:
        o, _ = oe.sbom30.find_jsonld(d, "deploy", f"{pn}-{t}-deploy", required=True)
        objsets.append(o)

    root_objs = []
    for o in objsets:
        root_objs.extend(o.doc.rootElement)

    objset, sbom = oe.sbom30.create_sbom(d, sbom_name, root_objs, objsets)
    oe.sbom30.write_jsonld_doc(d, objset, deploydir / (sbom_name + ".spdx.json"))
