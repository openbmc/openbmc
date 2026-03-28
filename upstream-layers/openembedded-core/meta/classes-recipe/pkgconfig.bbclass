#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

DEPENDS:prepend = "pkgconf-native "

export PKG_CONFIG_PATH ?= ""
export PKG_CONFIG_LIBDIR ?= "${STAGING_LIBDIR}/pkgconfig:${STAGING_DATADIR}/pkgconfig"

export PKG_CONFIG_SYSROOT_DIR ?= "${STAGING_DIR_HOST}"
export PKG_CONFIG_DISABLE_UNINSTALLED ?= "yes"

export PKG_CONFIG_SYSTEM_LIBRARY_PATH ?= "${STAGING_BASELIBDIR}:${STAGING_LIBDIR}"
export PKG_CONFIG_SYSTEM_INCLUDE_PATH ?= "${STAGING_INCDIR}"
