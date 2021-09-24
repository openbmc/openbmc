#
# SPDX-License-Identifier: GPL-2.0-only
#

import hashlib
import itertools
import json

SPDX_VERSION = "2.2"


class _Property(object):
    def __init__(self, *, default=None):
        self.default = default

    def setdefault(self, dest, name):
        if self.default is not None:
            dest.setdefault(name, self.default)


class _String(_Property):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)

    def set_property(self, attrs, name):
        def get_helper(obj):
            return obj._spdx[name]

        def set_helper(obj, value):
            obj._spdx[name] = value

        def del_helper(obj):
            del obj._spdx[name]

        attrs[name] = property(get_helper, set_helper, del_helper)

    def init(self, source):
        return source


class _Object(_Property):
    def __init__(self, cls, **kwargs):
        super().__init__(**kwargs)
        self.cls = cls

    def set_property(self, attrs, name):
        def get_helper(obj):
            if not name in obj._spdx:
                obj._spdx[name] = self.cls()
            return obj._spdx[name]

        def set_helper(obj, value):
            obj._spdx[name] = value

        def del_helper(obj):
            del obj._spdx[name]

        attrs[name] = property(get_helper, set_helper)

    def init(self, source):
        return self.cls(**source)


class _ListProperty(_Property):
    def __init__(self, prop, **kwargs):
        super().__init__(**kwargs)
        self.prop = prop

    def set_property(self, attrs, name):
        def get_helper(obj):
            if not name in obj._spdx:
                obj._spdx[name] = []
            return obj._spdx[name]

        def del_helper(obj):
            del obj._spdx[name]

        attrs[name] = property(get_helper, None, del_helper)

    def init(self, source):
        return [self.prop.init(o) for o in source]


class _StringList(_ListProperty):
    def __init__(self, **kwargs):
        super().__init__(_String(), **kwargs)


class _ObjectList(_ListProperty):
    def __init__(self, cls, **kwargs):
        super().__init__(_Object(cls), **kwargs)


class MetaSPDXObject(type):
    def __new__(mcls, name, bases, attrs):
        attrs["_properties"] = {}

        for key in attrs.keys():
            if isinstance(attrs[key], _Property):
                prop = attrs[key]
                attrs["_properties"][key] = prop
                prop.set_property(attrs, key)

        return super().__new__(mcls, name, bases, attrs)


class SPDXObject(metaclass=MetaSPDXObject):
    def __init__(self, **d):
        self._spdx = {}

        for name, prop in self._properties.items():
            prop.setdefault(self._spdx, name)
            if name in d:
                self._spdx[name] = prop.init(d[name])

    def serializer(self):
        return self._spdx

    def __setattr__(self, name, value):
        if name in self._properties or name == "_spdx":
            super().__setattr__(name, value)
            return
        raise KeyError("%r is not a valid SPDX property" % name)


class SPDXChecksum(SPDXObject):
    algorithm = _String()
    checksumValue = _String()


class SPDXRelationship(SPDXObject):
    spdxElementId = _String()
    relatedSpdxElement = _String()
    relationshipType = _String()
    comment = _String()


class SPDXExternalReference(SPDXObject):
    referenceCategory = _String()
    referenceType = _String()
    referenceLocator = _String()


class SPDXPackageVerificationCode(SPDXObject):
    packageVerificationCodeValue = _String()
    packageVerificationCodeExcludedFiles = _StringList()


class SPDXPackage(SPDXObject):
    name = _String()
    SPDXID = _String()
    versionInfo = _String()
    downloadLocation = _String(default="NOASSERTION")
    packageSupplier = _String(default="NOASSERTION")
    homepage = _String()
    licenseConcluded = _String(default="NOASSERTION")
    licenseDeclared = _String(default="NOASSERTION")
    summary = _String()
    description = _String()
    sourceInfo = _String()
    copyrightText = _String(default="NOASSERTION")
    licenseInfoFromFiles = _StringList(default=["NOASSERTION"])
    externalRefs = _ObjectList(SPDXExternalReference)
    packageVerificationCode = _Object(SPDXPackageVerificationCode)
    hasFiles = _StringList()
    packageFileName = _String()


class SPDXFile(SPDXObject):
    SPDXID = _String()
    fileName = _String()
    licenseConcluded = _String(default="NOASSERTION")
    copyrightText = _String(default="NOASSERTION")
    licenseInfoInFiles = _StringList(default=["NOASSERTION"])
    checksums = _ObjectList(SPDXChecksum)
    fileTypes = _StringList()


class SPDXCreationInfo(SPDXObject):
    created = _String()
    licenseListVersion = _String()
    comment = _String()
    creators = _StringList()


class SPDXExternalDocumentRef(SPDXObject):
    externalDocumentId = _String()
    spdxDocument = _String()
    checksum = _Object(SPDXChecksum)


class SPDXExtractedLicensingInfo(SPDXObject):
    name = _String()
    comment = _String()
    licenseId = _String()
    extractedText = _String()


class SPDXDocument(SPDXObject):
    spdxVersion = _String(default="SPDX-" + SPDX_VERSION)
    dataLicense = _String(default="CC0-1.0")
    SPDXID = _String(default="SPDXRef-DOCUMENT")
    name = _String()
    documentNamespace = _String()
    creationInfo = _Object(SPDXCreationInfo)
    packages = _ObjectList(SPDXPackage)
    files = _ObjectList(SPDXFile)
    relationships = _ObjectList(SPDXRelationship)
    externalDocumentRefs = _ObjectList(SPDXExternalDocumentRef)
    hasExtractedLicensingInfos = _ObjectList(SPDXExtractedLicensingInfo)

    def __init__(self, **d):
        super().__init__(**d)

    def to_json(self, f, *, sort_keys=False, indent=None, separators=None):
        class Encoder(json.JSONEncoder):
            def default(self, o):
                if isinstance(o, SPDXObject):
                    return o.serializer()

                return super().default(o)

        sha1 = hashlib.sha1()
        for chunk in Encoder(
            sort_keys=sort_keys,
            indent=indent,
            separators=separators,
        ).iterencode(self):
            chunk = chunk.encode("utf-8")
            f.write(chunk)
            sha1.update(chunk)

        return sha1.hexdigest()

    @classmethod
    def from_json(cls, f):
        return cls(**json.load(f))

    def add_relationship(self, _from, relationship, _to, *, comment=None):
        if isinstance(_from, SPDXObject):
            from_spdxid = _from.SPDXID
        else:
            from_spdxid = _from

        if isinstance(_to, SPDXObject):
            to_spdxid = _to.SPDXID
        else:
            to_spdxid = _to

        r = SPDXRelationship(
            spdxElementId=from_spdxid,
            relatedSpdxElement=to_spdxid,
            relationshipType=relationship,
        )

        if comment is not None:
            r.comment = comment

        self.relationships.append(r)

    def find_by_spdxid(self, spdxid):
        for o in itertools.chain(self.packages, self.files):
            if o.SPDXID == spdxid:
                return o
        return None

    def find_external_document_ref(self, namespace):
        for r in self.externalDocumentRefs:
            if r.spdxDocument == namespace:
                return r
        return None
