# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "LLVM OpenMP compiler Runtime"
HOMEPAGE = "https://openmp.llvm.org/"
SECTION = "libs"

require common-clang.inc
require common-source.inc

BPN = "openmp"
CVE_PRODUCT += "openmp"

LIC_FILES_CHKSUM = "file://openmp/LICENSE.TXT;md5=d75288d1ce0450b28b8d58a284c09c79"

inherit cmake pkgconfig python3targetconfig

DEPENDS += "elfutils libffi clang"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=RelWithDebInfo \
                  -DOPENMP_LIBDIR_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
                  "

OECMAKE_SOURCEPATH = "${S}/openmp"

PACKAGECONFIG ?= "ompt-tools offloading-plugin"

PACKAGECONFIG:remove:arm = "ompt-tools offloading-plugin"
PACKAGECONFIG:append:mipsarcho32 = " no-atomics"

PACKAGECONFIG[ompt-tools] = "-DOPENMP_ENABLE_OMPT_TOOLS=ON,-DOPENMP_ENABLE_OMPT_TOOLS=OFF,"
PACKAGECONFIG[aliases] = "-DLIBOMP_INSTALL_ALIASES=ON,-DLIBOMP_INSTALL_ALIASES=OFF,"
PACKAGECONFIG[offloading-plugin] = ",,elfutils libffi,libelf libffi"
PACKAGECONFIG[no-atomics] = "-DLIBOMP_HAVE_BUILTIN_ATOMIC=OFF -DLIBOMP_LIBFLAGS='-latomic',,"

PACKAGES += "${PN}-libomptarget ${PN}-gdb-plugin"
FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/lib*${SOLIBSDEV}"
FILES:${PN}-libomptarget = "${libdir}/libomptarget-*.bc"
FILES:${PN}-gdb-plugin = "${datadir}/gdb/python/ompd"

RDEPENDS:${PN}-gdb-plugin += "python3-core"

INSANE_SKIP:${PN} = "dev-so"
# Currently the static libraries contain buildpaths
INSANE_SKIP:${PN}-staticdev += "buildpaths"

COMPATIBLE_HOST:mips64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:powerpc = "null"

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2022-26345] = "cpe-incorrect: specific to the Intel distribution before 2022.1"
