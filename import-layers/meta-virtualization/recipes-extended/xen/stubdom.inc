# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

COMPATIBLE_HOST = '(x86_64.*).*-linux'

require xen-arch.inc

# many of the xen stubdom related recipes build and package static instead of shared libraries
EXCLUDE_FROM_SHLIBS = "1"

# base set of dependencies to be used for xen stubdom recipes
DEPENDS += "\
    lwip \
    mini-os \
"

# unset EVERYTHING from the OE environment.
# Weird things happen when these are exported into the environment.
unset CFLAGS
unset BUILD_CFLAGS
unset TARGET_CFLAGS
unset CFLAGS_FOR_BUILD

unset CPPFLAGS
unset BUILD_CPPFLAGS
unset TARGET_CPPFLAGS
unset CPPFLAGS_FOR_BUILD

unset LDFLAGS
unset BUILD_LDFLAGS
unset TARGET_LDFLAGS
unset LDFLAGS_FOR_BUILD

unset CXXFLAGS
unset TARGET_CXXFLAGS
unset BUILD_CXXFLAGS
unset CXXFLAGS_FOR_BUILD

unset BUILD_CC
unset BUILD_CPP
unset BUILD_CXX
unset BUILD_LD
unset BUILD_AR
unset BUILD_AS
unset BUILD_CCLD
unset BUILD_FC
unset BUILD_RANLIB
unset BUILD_NM
unset BUILD_STRIP
unset BUILD_READELF
unset BUILD_OBJCOPY
unset BUILD_OBJDUMP
unset CC
unset CPP
unset CXX
unset LD
unset AR
unset AS
unset NM
unset RANLIB
unset STRIP
unset STRINGS
unset READELF
unset OBJCOPY
unset OBJDUMP
unset READELF
unset CCLD
unset FC

# Provide support to build both 32-bit and 64-bit stubdoms
python () {
    gnu_dict = {
            'x86_32': 'i686',
            'x86_64': 'x86_64',
        }

    if d.expand('${XEN_TARGET_ARCH}') == 'x86_32':
        d.setVar("GNU_TARGET_ARCH",gnu_dict[d.expand('${XEN_TARGET_ARCH}')])
        d.setVar("PACKAGE_ARCH","core2-32")
    elif d.expand('${XEN_TARGET_ARCH}') == 'x86_64':
        d.setVar("GNU_TARGET_ARCH",gnu_dict[d.expand('${XEN_TARGET_ARCH}')])
}

export GNU_TARGET_ARCH
export XEN_TARGET_ARCH="${@map_xen_arch(d.getVar('TARGET_ARCH'), d)}"
export XEN_COMPILE_ARCH="${@map_xen_arch(d.getVar('BUILD_ARCH'), d)}"

LWIP_SRCDIR = "${RECIPE_SYSROOT}/cross-root-${GNU_TARGET_ARCH}/lwip"
MINIOS_SRCDIR = "${RECIPE_SYSROOT}/cross-root-${GNU_TARGET_ARCH}/mini-os"

# Base set of CPPFLAGS, CFLAGS needed for each component used to build MiniOS-based stubdoms
# LDFLAGS are only used when building stubdoms, so only used in stubdom recipes
# Generic name given because each library uses DEF_, BUILD_, TARGET_, and xxxFLAGS differently
CPPFLAGS_INCLUDE_DIR = "-isystem ${RECIPE_SYSROOT}/cross-root-${GNU_TARGET_ARCH}/${GNU_TARGET_ARCH}-xen-elf/include"

STUBDOM_CPPFLAGS += "\
    -isystem ${MINIOS_SRCDIR}/include \
    -D__MINIOS__ \
    -DHAVE_LIBC \
    -isystem ${MINIOS_SRCDIR}/include/posix \
    -isystem ${MINIOS_SRCDIR}/include/xen \
    -isystem ${MINIOS_SRCDIR}/include/x86 \
    -isystem ${MINIOS_SRCDIR}/include/x86/${XEN_TARGET_ARCH} \
    -U __linux__ \
    -U __FreeBSD__ \
    -U __sun__ \
    -nostdinc \
    ${CPPFLAGS_INCLUDE_DIR} \
    -isystem ${LWIP_SRCDIR}/include \
    -isystem ${LWIP_SRCDIR}/include/ipv4 \
"

STUBDOM_CFLAGS += "\
    -mno-red-zone \
    -O1 \
    -fno-omit-frame-pointer \
    -m64 \
    -fno-reorder-blocks \
    -fno-asynchronous-unwind-tables \
    -DBUILD_ID \
    -fno-strict-aliasing \
    -std=gnu99 \
    -Wall \
    -Wstrict-prototypes \
    -Wdeclaration-after-statement \
    -Wno-unused-but-set-variable \
    -Wno-unused-local-typedefs \
    -fno-stack-protector \
    -fno-exceptions \
"

STUBDOM_LDFLAGS = "\
    -nostdlib \
    -L${RECIPE_SYSROOT}/cross-root-${GNU_TARGET_ARCH}/${GNU_TARGET_ARCH}-xen-elf/lib \
"

# Need to redefine these for stubdom-related builds. It all starts because of
# the prefix used in newlib and then continues because we don't want to
# cross-contaminate stubdom-related recipes with headers and libraries found in
# the OE-defined locations
export prefix="/cross-root-${GNU_TARGET_ARCH}"
export includedir="${prefix}/${GNU_TARGET_ARCH}-xen-elf/include"
export libdir="${prefix}/${GNU_TARGET_ARCH}-xen-elf/lib"
export libexecdir="${libdir}"
export STAGING_INCDIR
export STAGING_LIBDIR

# Typically defined in Xen and Minios .mk files that aren't sourced/read,
# defined to trigger some values and paths in Makefiles
export debug="y"
export stubdom="y"
export XEN_OS="MiniOS"
