SUMMARY ?= "The Trusted Services: framework for developing root-of-trust services"
HOMEPAGE = "https://trusted-services.readthedocs.io/en/latest/index.html"

LICENSE = "Apache-2.0 & BSD-3-Clause & Zlib"

inherit python3native cmake pkgconfig

COMPATIBLE_HOST = "aarch64.*-linux"
COMPATIBLE_MACHINE ?= "invalid"
COMPATIBLE_MACHINE:qemuarm64-secureboot = "qemuarm64-secureboot"

require trusted-services-src.inc

# By default bitbake includes only ${S} (i.e git/trusted-services) in the maps.
# We also need to include the TS dependencies source trees.
DEBUG_PREFIX_MAP:append = "-fmacro-prefix-map=${WORKDIR}/git=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
 -fdebug-prefix-map=${WORKDIR}/git=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
"

TS_PLATFORM ?= "ts/mock"

# SP images are embedded into optee-os image
# FIP packaging is not supported yet
SP_PACKAGING_METHOD ?= "embedded"

SYSROOT_DIRS += "/usr/${TS_ENV} /usr/opteesp /usr/arm-linux"

# TS cmake files use find_file() to search through source code and build dirs.
# Yocto cmake class limits CMAKE_FIND_ROOT_PATH and find_file() fails.
# Include the source tree and build dirs into searchable path.
OECMAKE_EXTRA_ROOT_PATH = "${WORKDIR}/git/ ${WORKDIR}/build/"

EXTRA_OECMAKE += '-DLIBGCC_LOCATE_CFLAGS="--sysroot=${STAGING_DIR_HOST}" \
                  -DCROSS_COMPILE="${TARGET_PREFIX}" \
                  -DTS_PLATFORM="${TS_PLATFORM}" \
                 '
export CROSS_COMPILE="${TARGET_PREFIX}"

# Default TS installation path
TS_INSTALL = "/usr/${TS_ENV}"

# Use the Yocto cmake toolchain for external components of the arm-linux TS deployments,
# and the TS toolchain for opteesp and sp deployments
def get_ts_toolchain_option(d):
    ts_env=d.getVar('TS_ENV')
    if ts_env == 'opteesp' or ts_env == 'sp':
        return '-DCMAKE_TOOLCHAIN_FILE=${S}/environments/'+ts_env+'/default_toolchain_file.cmake'
    if ts_env == 'arm-linux':
        return '-DTS_EXTERNAL_LIB_TOOLCHAIN_FILE=${WORKDIR}/toolchain.cmake'
    bb.error("Unkown value \"%s\" for TS_ENV." % (ts_env))
    return ''

EXTRA_OECMAKE += "${@get_ts_toolchain_option(d)}"

# Paths to pre-built dependencies required by some TS SPs/tools
EXTRA_OECMAKE += "-Dlibts_ROOT=${STAGING_DIR_HOST}${TS_INSTALL}/lib/cmake/libts/ \
                  -DNEWLIB_INSTALL_DIR=${STAGING_DIR_HOST}/usr/opteesp/newlib \
                 "
