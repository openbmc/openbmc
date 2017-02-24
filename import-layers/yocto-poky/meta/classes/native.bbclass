# We want native packages to be relocatable
inherit relocatable

# Native packages are built indirectly via dependency,
# no need for them to be a direct target of 'world'
EXCLUDE_FROM_WORLD = "1"

PACKAGES = ""
PACKAGES_class-native = ""
PACKAGES_DYNAMIC = ""
PACKAGES_DYNAMIC_class-native = ""
PACKAGE_ARCH = "${BUILD_ARCH}"

# used by cmake class
OECMAKE_RPATH = "${libdir}"
OECMAKE_RPATH_class-native = "${libdir}"

# When this class has packaging enabled, setting 
# RPROVIDES becomes unnecessary.
RPROVIDES = "${PN}"

TARGET_ARCH = "${BUILD_ARCH}"
TARGET_OS = "${BUILD_OS}"
TARGET_VENDOR = "${BUILD_VENDOR}"
TARGET_PREFIX = "${BUILD_PREFIX}"
TARGET_CC_ARCH = "${BUILD_CC_ARCH}"
TARGET_LD_ARCH = "${BUILD_LD_ARCH}"
TARGET_AS_ARCH = "${BUILD_AS_ARCH}"
TARGET_CPPFLAGS = "${BUILD_CPPFLAGS}"
TARGET_CFLAGS = "${BUILD_CFLAGS}"
TARGET_CXXFLAGS = "${BUILD_CXXFLAGS}"
TARGET_LDFLAGS = "${BUILD_LDFLAGS}"
TARGET_FPU = ""

HOST_ARCH = "${BUILD_ARCH}"
HOST_OS = "${BUILD_OS}"
HOST_VENDOR = "${BUILD_VENDOR}"
HOST_PREFIX = "${BUILD_PREFIX}"
HOST_CC_ARCH = "${BUILD_CC_ARCH}"
HOST_LD_ARCH = "${BUILD_LD_ARCH}"
HOST_AS_ARCH = "${BUILD_AS_ARCH}"

CPPFLAGS = "${BUILD_CPPFLAGS}"
CFLAGS = "${BUILD_CFLAGS}"
CXXFLAGS = "${BUILD_CXXFLAGS}"
LDFLAGS = "${BUILD_LDFLAGS}"
LDFLAGS_build-darwin = "-L${STAGING_LIBDIR_NATIVE} "

STAGING_BINDIR = "${STAGING_BINDIR_NATIVE}"
STAGING_BINDIR_CROSS = "${STAGING_BINDIR_NATIVE}"

# native pkg doesn't need the TOOLCHAIN_OPTIONS.
TOOLCHAIN_OPTIONS = ""

DEPENDS_GETTEXT = "gettext-native"

# Don't build ptest natively
PTEST_ENABLED = "0"

# Don't use site files for native builds
export CONFIG_SITE = "${COREBASE}/meta/site/native"

# set the compiler as well. It could have been set to something else
export CC = "${BUILD_CC}"
export CXX = "${BUILD_CXX}"
export FC = "${BUILD_FC}"
export CPP = "${BUILD_CPP}"
export LD = "${BUILD_LD}"
export CCLD = "${BUILD_CCLD}"
export AR = "${BUILD_AR}"
export AS = "${BUILD_AS}"
export RANLIB = "${BUILD_RANLIB}"
export STRIP = "${BUILD_STRIP}"
export NM = "${BUILD_NM}"

# Path prefixes
base_prefix = "${STAGING_DIR_NATIVE}"
prefix = "${STAGING_DIR_NATIVE}${prefix_native}"
exec_prefix = "${STAGING_DIR_NATIVE}${prefix_native}"

bindir = "${STAGING_BINDIR_NATIVE}"
sbindir = "${STAGING_SBINDIR_NATIVE}"
libdir = "${STAGING_LIBDIR_NATIVE}"
includedir = "${STAGING_INCDIR_NATIVE}"
sysconfdir = "${STAGING_ETCDIR_NATIVE}"
datadir = "${STAGING_DATADIR_NATIVE}"

baselib = "lib"

export lt_cv_sys_lib_dlsearch_path_spec = "${libdir} ${base_libdir} /lib /lib64 /usr/lib /usr/lib64"

NATIVE_PACKAGE_PATH_SUFFIX ?= ""
bindir .= "${NATIVE_PACKAGE_PATH_SUFFIX}"
libdir .= "${NATIVE_PACKAGE_PATH_SUFFIX}"
libexecdir .= "${NATIVE_PACKAGE_PATH_SUFFIX}"

do_populate_sysroot[sstate-inputdirs] = "${SYSROOT_DESTDIR}/${STAGING_DIR_NATIVE}/"
do_populate_sysroot[sstate-outputdirs] = "${STAGING_DIR_NATIVE}/"

# Since we actually install these into situ there is no staging prefix
STAGING_DIR_HOST = ""
STAGING_DIR_TARGET = ""
PKG_CONFIG_DIR = "${libdir}/pkgconfig"

EXTRA_NATIVE_PKGCONFIG_PATH ?= ""
PKG_CONFIG_PATH .= "${EXTRA_NATIVE_PKGCONFIG_PATH}"
PKG_CONFIG_SYSROOT_DIR = ""
PKG_CONFIG_SYSTEM_LIBRARY_PATH[unexport] = "1"
PKG_CONFIG_SYSTEM_INCLUDE_PATH[unexport] = "1"

# we dont want libc-uclibc or libc-glibc to kick in for native recipes
LIBCOVERRIDE = ""
CLASSOVERRIDE = "class-native"
MACHINEOVERRIDES = ""

PATH_prepend = "${COREBASE}/scripts/native-intercept:"

python native_virtclass_handler () {
    classextend = e.data.getVar('BBCLASSEXTEND', True) or ""
    if "native" not in classextend:
        return

    pn = e.data.getVar("PN", True)
    if not pn.endswith("-native"):
        return

    def map_dependencies(varname, d, suffix = ""):
        if suffix:
            varname = varname + "_" + suffix
        deps = d.getVar(varname, True)
        if not deps:
            return
        deps = bb.utils.explode_deps(deps)
        newdeps = []
        for dep in deps:
            if dep == pn:
                continue
            elif "-cross-" in dep:
                newdeps.append(dep.replace("-cross", "-native"))
            elif not dep.endswith("-native"):
                newdeps.append(dep + "-native")
            else:
                newdeps.append(dep)
        d.setVar(varname, " ".join(newdeps))

    e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + ":virtclass-native")

    map_dependencies("DEPENDS", e.data)
    for pkg in [e.data.getVar("PN", True), "", "${PN}"]:
        map_dependencies("RDEPENDS", e.data, pkg)
        map_dependencies("RRECOMMENDS", e.data, pkg)
        map_dependencies("RSUGGESTS", e.data, pkg)
        map_dependencies("RPROVIDES", e.data, pkg)
        map_dependencies("RREPLACES", e.data, pkg)

    provides = e.data.getVar("PROVIDES", True)
    nprovides = []
    for prov in provides.split():
        if prov.find(pn) != -1:
            nprovides.append(prov)
        elif not prov.endswith("-native"):
            nprovides.append(prov.replace(prov, prov + "-native"))
        else:
            nprovides.append(prov)
    e.data.setVar("PROVIDES", ' '.join(nprovides))


}

addhandler native_virtclass_handler
native_virtclass_handler[eventmask] = "bb.event.RecipePreFinalise"

inherit nopackages

do_packagedata[stamp-extra-info] = ""
do_populate_sysroot[stamp-extra-info] = ""

USE_NLS = "no"
