#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

from pathlib import Path

import oe.spdx30
import bb
import re
import hashlib
import uuid
import os
import oe.spdx_common
from datetime import datetime, timezone

OE_SPDX_BASE = "https://rdf.openembedded.org/spdx/3.0/"

VEX_VERSION = "1.0.0"

SPDX_BUILD_TYPE = "http://openembedded.org/bitbake"

OE_ALIAS_PREFIX = "http://spdxdocs.org/openembedded-alias/by-doc-hash/"
OE_DOC_ALIAS_PREFIX = "http://spdxdocs.org/openembedded-alias/doc/"


@oe.spdx30.register(OE_SPDX_BASE + "id-alias")
class OEIdAliasExtension(oe.spdx30.extension_Extension):
    """
    This extension allows an Element to provide an internal alias for the SPDX
    ID. Since SPDX requires unique URIs for each SPDX ID, most of the objects
    created have a unique UUID namespace and the unihash of the task encoded in
    their SPDX ID. However, this causes a problem for referencing documents
    across recipes, since the taskhash of a dependency may not factor into the
    taskhash of the current task and thus the current task won't rebuild and
    see the new SPDX ID when the dependency changes (e.g. ABI safe recipes and
    tasks).

    To help work around this, this extension provides a non-unique alias for an
    Element by which it can be referenced from other tasks/recipes. When a
    final SBoM is created, references to these aliases will be replaced with
    the actual unique SPDX ID.

    Most Elements will automatically get an alias created when they are written
    out if they do not already have one. To suppress the creation of an alias,
    add an extension with a blank `alias` property.


    It is in internal extension that should be removed when writing out a final
    SBoM
    """

    CLOSED = True
    INTERNAL = True

    @classmethod
    def _register_props(cls):
        super()._register_props()
        cls._add_property(
            "alias",
            oe.spdx30.StringProp(),
            OE_SPDX_BASE + "alias",
            max_count=1,
        )

        cls._add_property(
            "link_name",
            oe.spdx30.StringProp(),
            OE_SPDX_BASE + "link-name",
            max_count=1,
        )


@oe.spdx30.register(OE_SPDX_BASE + "file-name-alias")
class OEFileNameAliasExtension(oe.spdx30.extension_Extension):
    CLOSED = True
    INTERNAL = True

    @classmethod
    def _register_props(cls):
        super()._register_props()
        cls._add_property(
            "aliases",
            oe.spdx30.ListProp(oe.spdx30.StringProp()),
            OE_SPDX_BASE + "filename-alias",
        )


@oe.spdx30.register(OE_SPDX_BASE + "license-scanned")
class OELicenseScannedExtension(oe.spdx30.extension_Extension):
    """
    The presence of this extension means the file has already been scanned for
    license information
    """

    CLOSED = True
    INTERNAL = True


@oe.spdx30.register(OE_SPDX_BASE + "document-extension")
class OEDocumentExtension(oe.spdx30.extension_Extension):
    """
    This extension is added to a SpdxDocument to indicate various useful bits
    of information about its contents
    """

    CLOSED = True

    @classmethod
    def _register_props(cls):
        super()._register_props()
        cls._add_property(
            "is_native",
            oe.spdx30.BooleanProp(),
            OE_SPDX_BASE + "is-native",
            max_count=1,
        )


def spdxid_hash(*items):
    h = hashlib.md5()
    for i in items:
        if isinstance(i, oe.spdx30.Element):
            h.update(i._id.encode("utf-8"))
        else:
            h.update(i.encode("utf-8"))
    return h.hexdigest()


def spdx_sde(d):
    sde = d.getVar("SOURCE_DATE_EPOCH")
    if not sde:
        return datetime.now(timezone.utc)

    return datetime.fromtimestamp(int(sde), timezone.utc)


def get_element_link_id(e):
    """
    Get the string ID which should be used to link to an Element. If the
    element has an alias, that will be preferred, otherwise its SPDX ID will be
    used.
    """
    ext = get_alias(e)
    if ext is not None and ext.alias:
        return ext.alias
    return e._id


def get_alias(obj):
    for ext in obj.extension:
        if not isinstance(ext, OEIdAliasExtension):
            continue
        return ext

    return None


def hash_id(_id):
    return hashlib.sha256(_id.encode("utf-8")).hexdigest()


def to_list(l):
    if isinstance(l, set):
        l = sorted(list(l))

    if not isinstance(l, (list, tuple)):
        raise TypeError("Must be a list or tuple. Got %s" % type(l))

    return l


class ObjectSet(oe.spdx30.SHACLObjectSet):
    def __init__(self, d):
        super().__init__()
        self.d = d
        self.alias_prefix = None

    def create_index(self):
        self.by_sha256_hash = {}
        super().create_index()

    def add_index(self, obj):
        # Check that all elements are given an ID before being inserted
        if isinstance(obj, oe.spdx30.Element):
            if not obj._id:
                raise ValueError("Element missing ID")

            alias_ext = get_alias(obj)
            if alias_ext is not None and alias_ext.alias:
                self.obj_by_id[alias_ext.alias] = obj

            for v in obj.verifiedUsing:
                if not isinstance(v, oe.spdx30.Hash):
                    continue

                if v.algorithm != oe.spdx30.HashAlgorithm.sha256:
                    continue

                self.by_sha256_hash.setdefault(v.hashValue, set()).add(obj)

        super().add_index(obj)
        if isinstance(obj, oe.spdx30.SpdxDocument):
            self.doc = obj
            alias_ext = get_alias(obj)
            if alias_ext is not None and alias_ext.alias:
                self.alias_prefix = OE_ALIAS_PREFIX + hash_id(alias_ext.alias) + "/"

    def __filter_obj(self, obj, attr_filter):
        return all(getattr(obj, k) == v for k, v in attr_filter.items())

    def foreach_filter(self, typ, *, match_subclass=True, **attr_filter):
        for obj in self.foreach_type(typ, match_subclass=match_subclass):
            if self.__filter_obj(obj, attr_filter):
                yield obj

    def find_filter(self, typ, *, match_subclass=True, **attr_filter):
        for obj in self.foreach_filter(
            typ, match_subclass=match_subclass, **attr_filter
        ):
            return obj
        return None

    def foreach_root(self, typ, **attr_filter):
        for obj in self.doc.rootElement:
            if not isinstance(obj, typ):
                continue

            if self.__filter_obj(obj, attr_filter):
                yield obj

    def find_root(self, typ, **attr_filter):
        for obj in self.foreach_root(typ, **attr_filter):
            return obj
        return None

    def add_root(self, obj):
        self.add(obj)
        self.doc.rootElement.append(obj)
        return obj

    def is_native(self):
        for e in self.doc.extension:
            if not isinstance(e, oe.sbom30.OEDocumentExtension):
                continue

            if e.is_native is not None:
                return e.is_native

        return False

    def set_is_native(self, is_native):
        for e in self.doc.extension:
            if not isinstance(e, oe.sbom30.OEDocumentExtension):
                continue

            e.is_native = is_native
            return

        if is_native:
            self.doc.extension.append(oe.sbom30.OEDocumentExtension(is_native=True))

    def add_aliases(self):
        for o in self.foreach_type(oe.spdx30.Element):
            self.set_element_alias(o)

    def new_alias_id(self, obj, replace):
        unihash = self.d.getVar("BB_UNIHASH")
        namespace = self.get_namespace()
        if unihash not in obj._id:
            bb.warn(f"Unihash {unihash} not found in {obj._id}")
            return None

        if namespace not in obj._id:
            bb.warn(f"Namespace {namespace} not found in {obj._id}")
            return None

        return obj._id.replace(unihash, "UNIHASH").replace(
            namespace, replace + self.d.getVar("PN")
        )

    def remove_internal_extensions(self):
        def remove(o):
            o.extension = [e for e in o.extension if not getattr(e, "INTERNAL", False)]

        for o in self.foreach_type(oe.spdx30.Element):
            remove(o)

        if self.doc:
            remove(self.doc)

    def get_namespace(self):
        namespace_uuid = uuid.uuid5(
            uuid.NAMESPACE_DNS, self.d.getVar("SPDX_UUID_NAMESPACE")
        )
        pn = self.d.getVar("PN")
        return "%s/%s-%s" % (
            self.d.getVar("SPDX_NAMESPACE_PREFIX"),
            pn,
            str(uuid.uuid5(namespace_uuid, pn)),
        )

    def set_element_alias(self, e):
        if not e._id or e._id.startswith("_:"):
            return

        alias_ext = get_alias(e)
        if alias_ext is None:
            alias_id = self.new_alias_id(e, self.alias_prefix)
            if alias_id is not None:
                e.extension.append(OEIdAliasExtension(alias=alias_id))
        elif (
            alias_ext.alias
            and not isinstance(e, oe.spdx30.SpdxDocument)
            and not alias_ext.alias.startswith(self.alias_prefix)
        ):
            bb.warn(
                f"Element {e._id} has alias {alias_ext.alias}, but it should have prefix {self.alias_prefix}"
            )

    def new_spdxid(self, *suffix, include_unihash=True):
        items = [self.get_namespace()]
        if include_unihash:
            unihash = self.d.getVar("BB_UNIHASH")
            items.append(unihash)
        items.extend(re.sub(r"[^a-zA-Z0-9_-]", "_", s) for s in suffix)
        return "/".join(items)

    def new_import(self, key):
        base = f"SPDX_IMPORTS_{key}"
        spdxid = self.d.getVar(f"{base}_spdxid")
        if not spdxid:
            bb.fatal(f"{key} is not a valid SPDX_IMPORTS key")

        for i in self.doc.import_:
            if i.externalSpdxId == spdxid:
                # Already imported
                return spdxid

        m = oe.spdx30.ExternalMap(externalSpdxId=spdxid)

        uri = self.d.getVar(f"{base}_uri")
        if uri:
            m.locationHint = uri

        for pyname, algorithm in oe.spdx30.HashAlgorithm.NAMED_INDIVIDUALS.items():
            value = self.d.getVar(f"{base}_hash_{pyname}")
            if value:
                m.verifiedUsing.append(
                    oe.spdx30.Hash(
                        algorithm=algorithm,
                        hashValue=value,
                    )
                )

        self.doc.import_.append(m)
        return spdxid

    def new_agent(self, varname, *, creation_info=None, add=True):
        ref_varname = self.d.getVar(f"{varname}_ref")
        if ref_varname:
            if ref_varname == varname:
                bb.fatal(f"{varname} cannot reference itself")
            return self.new_agent(ref_varname, creation_info=creation_info)

        import_key = self.d.getVar(f"{varname}_import")
        if import_key:
            return self.new_import(import_key)

        name = self.d.getVar(f"{varname}_name")
        if not name:
            return None

        spdxid = self.new_spdxid("agent", name)
        agent = self.find_by_id(spdxid)
        if agent is not None:
            return agent

        agent_type = self.d.getVar("%s_type" % varname)
        if agent_type == "person":
            agent = oe.spdx30.Person()
        elif agent_type == "software":
            agent = oe.spdx30.SoftwareAgent()
        elif agent_type == "organization":
            agent = oe.spdx30.Organization()
        elif not agent_type or agent_type == "agent":
            agent = oe.spdx30.Agent()
        else:
            bb.fatal("Unknown agent type '%s' in %s_type" % (agent_type, varname))

        agent._id = spdxid
        agent.creationInfo = creation_info or self.doc.creationInfo
        agent.name = name

        comment = self.d.getVar("%s_comment" % varname)
        if comment:
            agent.comment = comment

        for (
            pyname,
            idtype,
        ) in oe.spdx30.ExternalIdentifierType.NAMED_INDIVIDUALS.items():
            value = self.d.getVar("%s_id_%s" % (varname, pyname))
            if value:
                agent.externalIdentifier.append(
                    oe.spdx30.ExternalIdentifier(
                        externalIdentifierType=idtype,
                        identifier=value,
                    )
                )

        if add:
            self.add(agent)

        return agent

    def new_creation_info(self):
        creation_info = oe.spdx30.CreationInfo()

        name = "%s %s" % (
            self.d.getVar("SPDX_TOOL_NAME"),
            self.d.getVar("SPDX_TOOL_VERSION"),
        )
        tool = self.add(
            oe.spdx30.Tool(
                _id=self.new_spdxid("tool", name),
                creationInfo=creation_info,
                name=name,
            )
        )

        authors = []
        for a in self.d.getVar("SPDX_AUTHORS").split():
            varname = "SPDX_AUTHORS_%s" % a
            author = self.new_agent(varname, creation_info=creation_info)

            if not author:
                bb.fatal("Unable to find or create author %s" % a)

            authors.append(author)

        creation_info.created = spdx_sde(self.d)
        creation_info.specVersion = self.d.getVar("SPDX_VERSION")
        creation_info.createdBy = authors
        creation_info.createdUsing = [tool]

        return creation_info

    def copy_creation_info(self, copy):
        c = oe.spdx30.CreationInfo(
            created=spdx_sde(self.d),
            specVersion=self.d.getVar("SPDX_VERSION"),
        )

        for author in copy.createdBy:
            if isinstance(author, str):
                c.createdBy.append(author)
            else:
                c.createdBy.append(author._id)

        for tool in copy.createdUsing:
            if isinstance(tool, str):
                c.createdUsing.append(tool)
            else:
                c.createdUsing.append(tool._id)

        return c

    def new_annotation(self, subject, comment, typ):
        return self.add(
            oe.spdx30.Annotation(
                _id=self.new_spdxid("annotation", spdxid_hash(comment, typ)),
                creationInfo=self.doc.creationInfo,
                annotationType=typ,
                subject=subject,
                statement=comment,
            )
        )

    def _new_relationship(
        self,
        cls,
        from_,
        typ,
        to,
        *,
        spdxid_name="relationship",
        **props,
    ):
        from_ = to_list(from_)
        to = to_list(to)

        if not from_:
            return []

        if not to:
            to = [oe.spdx30.IndividualElement.NoneElement]

        ret = []

        for f in from_:
            hash_args = [typ, f]
            for k in sorted(props.keys()):
                hash_args.append(props[k])
            hash_args.extend(to)

            relationship = self.add(
                cls(
                    _id=self.new_spdxid(spdxid_name, spdxid_hash(*hash_args)),
                    creationInfo=self.doc.creationInfo,
                    from_=f,
                    relationshipType=typ,
                    to=to,
                    **props,
                )
            )
            ret.append(relationship)

        return ret

    def new_relationship(self, from_, typ, to):
        return self._new_relationship(oe.spdx30.Relationship, from_, typ, to)

    def new_scoped_relationship(self, from_, typ, scope, to):
        return self._new_relationship(
            oe.spdx30.LifecycleScopedRelationship,
            from_,
            typ,
            to,
            scope=scope,
        )

    def new_license_expression(
        self, license_expression, license_data, license_text_map={}
    ):
        license_list_version = license_data["licenseListVersion"]
        # SPDX 3 requires that the license list version be a semver
        # MAJOR.MINOR.MICRO, but the actual license version might be
        # MAJOR.MINOR on some older versions. As such, manually append a .0
        # micro version if its missing to keep SPDX happy
        if license_list_version.count(".") < 2:
            license_list_version += ".0"

        spdxid = [
            "license",
            license_list_version,
            re.sub(r"[^a-zA-Z0-9_-]", "_", license_expression),
        ]

        license_text = [
            (k, license_text_map[k]) for k in sorted(license_text_map.keys())
        ]

        if not license_text:
            lic = self.find_filter(
                oe.spdx30.simplelicensing_LicenseExpression,
                simplelicensing_licenseExpression=license_expression,
                simplelicensing_licenseListVersion=license_list_version,
            )
            if lic is not None:
                return lic
        else:
            spdxid.append(spdxid_hash(*(v for _, v in license_text)))
            lic = self.find_by_id(self.new_spdxid(*spdxid))
            if lic is not None:
                return lic

        lic = self.add(
            oe.spdx30.simplelicensing_LicenseExpression(
                _id=self.new_spdxid(*spdxid),
                creationInfo=self.doc.creationInfo,
                simplelicensing_licenseExpression=license_expression,
                simplelicensing_licenseListVersion=license_list_version,
            )
        )

        for key, value in license_text:
            lic.simplelicensing_customIdToUri.append(
                oe.spdx30.DictionaryEntry(key=key, value=value)
            )

        return lic

    def scan_declared_licenses(self, spdx_file, filepath, license_data):
        for e in spdx_file.extension:
            if isinstance(e, OELicenseScannedExtension):
                return

        file_licenses = set()
        for extracted_lic in oe.spdx_common.extract_licenses(filepath):
            lic = self.new_license_expression(extracted_lic, license_data)
            self.set_element_alias(lic)
            file_licenses.add(lic)

        self.new_relationship(
            [spdx_file],
            oe.spdx30.RelationshipType.hasDeclaredLicense,
            [oe.sbom30.get_element_link_id(lic_alias) for lic_alias in file_licenses],
        )
        spdx_file.extension.append(OELicenseScannedExtension())

    def new_file(self, _id, name, path, *, purposes=[]):
        sha256_hash = bb.utils.sha256_file(path)

        for f in self.by_sha256_hash.get(sha256_hash, []):
            if not isinstance(f, oe.spdx30.software_File):
                continue

            if purposes:
                new_primary = purposes[0]
                new_additional = []

                if f.software_primaryPurpose:
                    new_additional.append(f.software_primaryPurpose)
                new_additional.extend(f.software_additionalPurpose)

                new_additional = sorted(
                    list(set(p for p in new_additional if p != new_primary))
                )

                f.software_primaryPurpose = new_primary
                f.software_additionalPurpose = new_additional

            if f.name != name:
                for e in f.extension:
                    if isinstance(e, OEFileNameAliasExtension):
                        e.aliases.append(name)
                        break
                else:
                    f.extension.append(OEFileNameAliasExtension(aliases=[name]))

            return f

        spdx_file = oe.spdx30.software_File(
            _id=_id,
            creationInfo=self.doc.creationInfo,
            name=name,
        )
        if purposes:
            spdx_file.software_primaryPurpose = purposes[0]
            spdx_file.software_additionalPurpose = purposes[1:]

        spdx_file.verifiedUsing.append(
            oe.spdx30.Hash(
                algorithm=oe.spdx30.HashAlgorithm.sha256,
                hashValue=sha256_hash,
            )
        )

        return self.add(spdx_file)

    def new_cve_vuln(self, cve):
        v = oe.spdx30.security_Vulnerability()
        v._id = self.new_spdxid("vulnerability", cve)
        v.creationInfo = self.doc.creationInfo

        v.externalIdentifier.append(
            oe.spdx30.ExternalIdentifier(
                externalIdentifierType=oe.spdx30.ExternalIdentifierType.cve,
                identifier=cve,
                identifierLocator=[
                    f"https://cveawg.mitre.org/api/cve/{cve}",
                    f"https://www.cve.org/CVERecord?id={cve}",
                ],
            )
        )
        return self.add(v)

    def new_vex_patched_relationship(self, from_, to):
        return self._new_relationship(
            oe.spdx30.security_VexFixedVulnAssessmentRelationship,
            from_,
            oe.spdx30.RelationshipType.fixedIn,
            to,
            spdxid_name="vex-fixed",
            security_vexVersion=VEX_VERSION,
        )

    def new_vex_unpatched_relationship(self, from_, to):
        return self._new_relationship(
            oe.spdx30.security_VexAffectedVulnAssessmentRelationship,
            from_,
            oe.spdx30.RelationshipType.affects,
            to,
            spdxid_name="vex-affected",
            security_vexVersion=VEX_VERSION,
            security_actionStatement="Mitigation action unknown",
        )

    def new_vex_ignored_relationship(self, from_, to, *, impact_statement):
        return self._new_relationship(
            oe.spdx30.security_VexNotAffectedVulnAssessmentRelationship,
            from_,
            oe.spdx30.RelationshipType.doesNotAffect,
            to,
            spdxid_name="vex-not-affected",
            security_vexVersion=VEX_VERSION,
            security_impactStatement=impact_statement,
        )

    def import_bitbake_build_objset(self):
        deploy_dir_spdx = Path(self.d.getVar("DEPLOY_DIR_SPDX"))
        bb_objset = load_jsonld(
            self.d, deploy_dir_spdx / "bitbake.spdx.json", required=True
        )
        self.doc.import_.extend(bb_objset.doc.import_)
        self.update(bb_objset.objects)

        return bb_objset

    def import_bitbake_build(self):
        def find_bitbake_build(objset):
            return objset.find_filter(
                oe.spdx30.build_Build,
                build_buildType=SPDX_BUILD_TYPE,
            )

        build = find_bitbake_build(self)
        if build:
            return build

        bb_objset = self.import_bitbake_build_objset()
        build = find_bitbake_build(bb_objset)
        if build is None:
            bb.fatal(f"No build found in {deploy_dir_spdx}")

        return build

    def new_task_build(self, name, typ):
        current_task = self.d.getVar("BB_CURRENTTASK")
        pn = self.d.getVar("PN")

        build = self.add(
            oe.spdx30.build_Build(
                _id=self.new_spdxid("build", name),
                creationInfo=self.doc.creationInfo,
                name=f"{pn}:do_{current_task}:{name}",
                build_buildType=f"{SPDX_BUILD_TYPE}/do_{current_task}/{typ}",
            )
        )

        if self.d.getVar("SPDX_INCLUDE_BITBAKE_PARENT_BUILD") == "1":
            bitbake_build = self.import_bitbake_build()

            self.new_relationship(
                [bitbake_build],
                oe.spdx30.RelationshipType.ancestorOf,
                [build],
            )

        if self.d.getVar("SPDX_INCLUDE_BUILD_VARIABLES") == "1":
            for varname in sorted(self.d.keys()):
                if varname.startswith("__"):
                    continue

                value = self.d.getVar(varname, expand=False)

                # TODO: Deal with non-string values
                if not isinstance(value, str):
                    continue

                build.build_parameter.append(
                    oe.spdx30.DictionaryEntry(key=varname, value=value)
                )

        return build

    def new_archive(self, archive_name):
        return self.add(
            oe.spdx30.software_File(
                _id=self.new_spdxid("archive", str(archive_name)),
                creationInfo=self.doc.creationInfo,
                name=str(archive_name),
                software_primaryPurpose=oe.spdx30.software_SoftwarePurpose.archive,
            )
        )

    @classmethod
    def new_objset(cls, d, name, copy_from_bitbake_doc=True):
        objset = cls(d)

        document = oe.spdx30.SpdxDocument(
            _id=objset.new_spdxid("document", name),
            name=name,
        )

        document.extension.append(
            OEIdAliasExtension(
                alias=objset.new_alias_id(
                    document,
                    OE_DOC_ALIAS_PREFIX + d.getVar("PN") + "/" + name + "/",
                ),
            )
        )
        objset.doc = document
        objset.add_index(document)

        if copy_from_bitbake_doc:
            bb_objset = objset.import_bitbake_build_objset()
            document.creationInfo = objset.copy_creation_info(
                bb_objset.doc.creationInfo
            )
        else:
            document.creationInfo = objset.new_creation_info()

        return objset

    def expand_collection(self, *, add_objectsets=[]):
        """
        Expands a collection to pull in all missing elements

        Returns the set of ids that could not be found to link into the document
        """
        missing_spdxids = set()
        imports = {e.externalSpdxId: e for e in self.doc.import_}

        def merge_doc(other):
            nonlocal imports

            for e in other.doc.import_:
                if not e.externalSpdxId in imports:
                    imports[e.externalSpdxId] = e

            self.objects |= other.objects

        for o in add_objectsets:
            merge_doc(o)

        needed_spdxids = self.link()
        provided_spdxids = set(self.obj_by_id.keys())

        while True:
            import_spdxids = set(imports.keys())
            searching_spdxids = (
                needed_spdxids - provided_spdxids - missing_spdxids - import_spdxids
            )
            if not searching_spdxids:
                break

            spdxid = searching_spdxids.pop()
            bb.debug(
                1,
                f"Searching for {spdxid}. Remaining: {len(searching_spdxids)}, Total: {len(provided_spdxids)}, Missing: {len(missing_spdxids)}, Imports: {len(import_spdxids)}",
            )
            dep_objset, dep_path = find_by_spdxid(self.d, spdxid)

            if dep_objset:
                dep_provided = set(dep_objset.obj_by_id.keys())
                if spdxid not in dep_provided:
                    bb.fatal(f"{spdxid} not found in {dep_path}")
                provided_spdxids |= dep_provided
                needed_spdxids |= dep_objset.missing_ids
                merge_doc(dep_objset)
            else:
                missing_spdxids.add(spdxid)

        self.doc.import_ = sorted(imports.values(), key=lambda e: e.externalSpdxId)
        bb.debug(1, "Linking...")
        self.link()

        # Manually go through all of the simplelicensing_customIdToUri DictionaryEntry
        # items and resolve any aliases to actual objects.
        for lic in self.foreach_type(oe.spdx30.simplelicensing_LicenseExpression):
            for d in lic.simplelicensing_customIdToUri:
                if d.value.startswith(OE_ALIAS_PREFIX):
                    obj = self.find_by_id(d.value)
                    if obj is not None:
                        d.value = obj._id
                    else:
                        self.missing_ids.add(d.value)

        self.missing_ids -= set(imports.keys())
        return self.missing_ids


def load_jsonld(d, path, required=False):
    deserializer = oe.spdx30.JSONLDDeserializer()
    objset = ObjectSet(d)
    try:
        with path.open("rb") as f:
            deserializer.read(f, objset)
    except FileNotFoundError:
        if required:
            bb.fatal("No SPDX document named %s found" % path)
        return None

    if not objset.doc:
        bb.fatal("SPDX Document %s has no SPDXDocument element" % path)
        return None

    objset.objects.remove(objset.doc)
    return objset


def jsonld_arch_path(d, arch, subdir, name, deploydir=None):
    if deploydir is None:
        deploydir = Path(d.getVar("DEPLOY_DIR_SPDX"))
    return deploydir / arch / subdir / (name + ".spdx.json")


def jsonld_hash_path(h):
    return Path("by-spdxid-hash") / h[:2], h


def load_jsonld_by_arch(d, arch, subdir, name, *, required=False):
    path = jsonld_arch_path(d, arch, subdir, name)
    objset = load_jsonld(d, path, required=required)
    if objset is not None:
        return (objset, path)
    return (None, None)


def find_jsonld(d, subdir, name, *, required=False):
    package_archs = d.getVar("SPDX_MULTILIB_SSTATE_ARCHS").split()
    package_archs.reverse()

    for arch in package_archs:
        objset, path = load_jsonld_by_arch(d, arch, subdir, name)
        if objset is not None:
            return (objset, path)

    if required:
        bb.fatal("Could not find a %s SPDX document named %s" % (subdir, name))

    return (None, None)


def write_jsonld_doc(d, objset, dest):
    if not isinstance(objset, ObjectSet):
        bb.fatal("Only an ObjsetSet can be serialized")
        return

    if not objset.doc:
        bb.fatal("ObjectSet is missing a SpdxDocument")
        return

    objset.doc.rootElement = sorted(list(set(objset.doc.rootElement)))
    objset.doc.profileConformance = sorted(
        list(
            getattr(oe.spdx30.ProfileIdentifierType, p)
            for p in d.getVar("SPDX_PROFILES").split()
        )
    )

    dest.parent.mkdir(exist_ok=True, parents=True)

    if d.getVar("SPDX_PRETTY") == "1":
        serializer = oe.spdx30.JSONLDSerializer(
            indent=2,
        )
    else:
        serializer = oe.spdx30.JSONLDInlineSerializer()

    objset.objects.add(objset.doc)
    with dest.open("wb") as f:
        serializer.write(objset, f, force_at_graph=True)
    objset.objects.remove(objset.doc)


def write_recipe_jsonld_doc(
    d,
    objset,
    subdir,
    deploydir,
    *,
    create_spdx_id_links=True,
):
    pkg_arch = d.getVar("SSTATE_PKGARCH")

    dest = jsonld_arch_path(d, pkg_arch, subdir, objset.doc.name, deploydir=deploydir)

    def link_id(_id):
        hash_path = jsonld_hash_path(hash_id(_id))

        link_name = jsonld_arch_path(
            d,
            pkg_arch,
            *hash_path,
            deploydir=deploydir,
        )
        try:
            link_name.parent.mkdir(exist_ok=True, parents=True)
            link_name.symlink_to(os.path.relpath(dest, link_name.parent))
        except:
            target = link_name.readlink()
            bb.warn(
                f"Unable to link {_id} in {dest} as {link_name}. Already points to {target}"
            )
            raise

        return hash_path[-1]

    objset.add_aliases()

    try:
        if create_spdx_id_links:
            alias_ext = get_alias(objset.doc)
            if alias_ext is not None and alias_ext.alias:
                alias_ext.link_name = link_id(alias_ext.alias)

    finally:
        # It is really helpful for debugging if the JSON document is written
        # out, so always do that even if there is an error making the links
        write_jsonld_doc(d, objset, dest)


def find_root_obj_in_jsonld(d, subdir, fn_name, obj_type, **attr_filter):
    objset, fn = find_jsonld(d, subdir, fn_name, required=True)

    spdx_obj = objset.find_root(obj_type, **attr_filter)
    if not spdx_obj:
        bb.fatal("No root %s found in %s" % (obj_type.__name__, fn))

    return spdx_obj, objset


def load_obj_in_jsonld(d, arch, subdir, fn_name, obj_type, **attr_filter):
    objset, fn = load_jsonld_by_arch(d, arch, subdir, fn_name, required=True)

    spdx_obj = objset.find_filter(obj_type, **attr_filter)
    if not spdx_obj:
        bb.fatal("No %s found in %s" % (obj_type.__name__, fn))

    return spdx_obj, objset


def find_by_spdxid(d, spdxid, *, required=False):
    if spdxid.startswith(OE_ALIAS_PREFIX):
        h = spdxid[len(OE_ALIAS_PREFIX) :].split("/", 1)[0]
        return find_jsonld(d, *jsonld_hash_path(h), required=required)
    return find_jsonld(d, *jsonld_hash_path(hash_id(spdxid)), required=required)


def create_sbom(d, name, root_elements, add_objectsets=[]):
    objset = ObjectSet.new_objset(d, name)

    sbom = objset.add(
        oe.spdx30.software_Sbom(
            _id=objset.new_spdxid("sbom", name),
            name=name,
            creationInfo=objset.doc.creationInfo,
            software_sbomType=[oe.spdx30.software_SbomType.build],
            rootElement=root_elements,
        )
    )

    missing_spdxids = objset.expand_collection(add_objectsets=add_objectsets)
    if missing_spdxids:
        bb.warn(
            "The following SPDX IDs were unable to be resolved:\n  "
            + "\n  ".join(sorted(list(missing_spdxids)))
        )

    # Filter out internal extensions from final SBoMs
    objset.remove_internal_extensions()

    # SBoM should be the only root element of the document
    objset.doc.rootElement = [sbom]

    # De-duplicate licenses
    unique = set()
    dedup = {}
    for lic in objset.foreach_type(oe.spdx30.simplelicensing_LicenseExpression):
        for u in unique:
            if (
                u.simplelicensing_licenseExpression
                == lic.simplelicensing_licenseExpression
                and u.simplelicensing_licenseListVersion
                == lic.simplelicensing_licenseListVersion
            ):
                dedup[lic] = u
                break
        else:
            unique.add(lic)

    if dedup:
        for rel in objset.foreach_filter(
            oe.spdx30.Relationship,
            relationshipType=oe.spdx30.RelationshipType.hasDeclaredLicense,
        ):
            rel.to = [dedup.get(to, to) for to in rel.to]

        for rel in objset.foreach_filter(
            oe.spdx30.Relationship,
            relationshipType=oe.spdx30.RelationshipType.hasConcludedLicense,
        ):
            rel.to = [dedup.get(to, to) for to in rel.to]

        for k, v in dedup.items():
            bb.debug(1, f"Removing duplicate License {k._id} -> {v._id}")
            objset.objects.remove(k)

        objset.create_index()

    return objset, sbom
