# SPDX-License-Identifier: MIT

import argparse
from pathlib import Path


from spdx_python_model import v3_0_1 as spdx_3_0_1
from .version import VERSION


def main():
    parser = argparse.ArgumentParser(
        description="Show the packaged files and checksums in an OE image from the SPDX SBoM"
    )
    parser.add_argument("file", help="SPDX 3 input file", type=Path)
    parser.add_argument("--version", "-V", action="version", version=VERSION)

    args = parser.parse_args()

    # Load SPDX data from file into a new object set
    objset = spdx_3_0_1.SHACLObjectSet()
    with args.file.open("r") as f:
        d = spdx_3_0_1.JSONLDDeserializer()
        d.read(f, objset)

    # Find the top level SPDX Document object
    for o in objset.foreach_type(spdx_3_0_1.SpdxDocument):
        doc = o
        break
    else:
        print("ERROR: No SPDX Document found!")
        return 1

    # Find the root SBoM in the document
    for o in doc.rootElement:
        if isinstance(o, spdx_3_0_1.software_Sbom):
            sbom = o
            break
    else:
        print("ERROR: SBoM not found in document")
        return 1

    # Find the root file system package in the SBoM
    for o in sbom.rootElement:
        if (
            isinstance(o, spdx_3_0_1.software_Package)
            and o.software_primaryPurpose == spdx_3_0_1.software_SoftwarePurpose.archive
        ):
            root_package = o
            break
    else:
        print("ERROR: Package not found in document")
        return 1

    # Find all relationships of type "contains" that go FROM the root file
    # system
    files = []
    for rel in objset.foreach_type(spdx_3_0_1.Relationship):
        if not rel.relationshipType == spdx_3_0_1.RelationshipType.contains:
            continue

        if not rel.from_ is root_package:
            continue

        # Iterate over all files in the TO of the relationship
        for o in rel.to:
            if not isinstance(o, spdx_3_0_1.software_File):
                continue

            # Find the SHA 256 hash of the file (if any)
            for h in o.verifiedUsing:
                if (
                    isinstance(h, spdx_3_0_1.Hash)
                    and h.algorithm == spdx_3_0_1.HashAlgorithm.sha256
                ):
                    files.append((o.name, h.hashValue))
                    break
            else:
                files.append((o.name, ""))

    # Print files
    files.sort(key=lambda x: x[0])
    for name, hash_val in files:
        print(f"{name} - {hash_val}")

    return 0
