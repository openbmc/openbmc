#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Enable other layers to have modules in the same named directory
from pkgutil import extend_path
__path__ = extend_path(__path__, __name__)
