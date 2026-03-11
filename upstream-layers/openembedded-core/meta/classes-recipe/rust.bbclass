#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit rust-common

RUSTC = "rustc"

RUSTC_ARCHFLAGS += "--target=${RUST_HOST_SYS} ${RUSTFLAGS}"

def rust_base_dep(d):
    # Taken from meta/classes/base.bbclass `base_dep_prepend` and modified to
    # use rust instead of gcc
    deps = ""
    if not d.getVar('INHIBIT_DEFAULT_RUST_DEPS'):
        if (d.getVar('HOST_SYS') != d.getVar('BUILD_SYS')):
            deps += " rust-native ${RUSTLIB_DEP}"
        else:
            deps += " rust-native"
    return deps

DEPENDS:append = " ${@rust_base_dep(d)}"

# BUILD_LDFLAGS
# 	${STAGING_LIBDIR_NATIVE}
# 	${STAGING_BASE_LIBDIR_NATIVE}
# BUILDSDK_LDFLAGS
# 	${STAGING_LIBDIR}
# 	#{STAGING_DIR_HOST}
# TARGET_LDFLAGS ?????
#RUSTC_BUILD_LDFLAGS = "\
#	--sysroot ${STAGING_DIR_NATIVE} \
#	-L${STAGING_LIBDIR_NATIVE}	\
#	-L${STAGING_BASE_LIBDIR_NATIVE}	\
#"

# XXX: for some reason bitbake sets BUILD_* & TARGET_* but uses the bare
# variables for HOST. Alias things to make it easier for us.
HOST_LDFLAGS  ?= "${LDFLAGS}"
HOST_CFLAGS   ?= "${CFLAGS}"
HOST_CXXFLAGS ?= "${CXXFLAGS}"
HOST_CPPFLAGS ?= "${CPPFLAGS}"

rustlib_suffix = "${TUNE_ARCH}${TARGET_VENDOR}-${TARGET_OS}/rustlib/${RUST_HOST_SYS}/lib"
# Native sysroot standard library path
rustlib_src = "${prefix}/lib/${rustlib_suffix}"
# Host sysroot standard library path
rustlib = "${libdir}/${rustlib_suffix}"
rustlib:class-native = "${libdir}/rustlib/${BUILD_SYS}/lib"
