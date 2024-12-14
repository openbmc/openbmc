#! /bin/sh
#
# SPDX-License-Identifier: MIT

THIS_DIR="$(dirname "$0")"

VERSION="3.0.1"

shacl2code generate --input https://spdx.org/rdf/$VERSION/spdx-model.ttl \
    --input https://spdx.org/rdf/$VERSION/spdx-json-serialize-annotations.ttl \
    --context https://spdx.org/rdf/$VERSION/spdx-context.jsonld \
    python -o $THIS_DIR/../../meta/lib/oe/spdx30.py
