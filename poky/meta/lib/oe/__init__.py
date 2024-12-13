#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

from pkgutil import extend_path
__path__ = extend_path(__path__, __name__)

# Modules with vistorcode need to go first else anything depending on them won't be
# processed correctly (e.g. qa)
BBIMPORTS = ["qa", "data", "path", "utils", "types", "package", "packagedata", \
             "packagegroup", "sstatesig", "lsb", "cachedpath", "license", \
             "reproducible", "rust", "buildcfg", "go"]
