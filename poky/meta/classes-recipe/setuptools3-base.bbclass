#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

DEPENDS:append:class-target = " python3-native python3"
DEPENDS:append:class-nativesdk = " python3-native python3"
RDEPENDS:${PN}:append:class-target = " python3-core"

export STAGING_INCDIR
export STAGING_LIBDIR

# LDSHARED is the ld *command* used to create shared library
export LDSHARED  = "${CCLD} -shared"
# LDXXSHARED is the ld *command* used to create shared library of C++
# objects
export LDCXXSHARED  = "${CXX} -shared"
# CCSHARED are the C *flags* used to create objects to go into a shared
# library (module)
export CCSHARED  = "-fPIC -DPIC"
# LINKFORSHARED are the flags passed to the $(CC) command that links
# the python executable
export LINKFORSHARED = "${SECURITY_CFLAGS} -Xlinker -export-dynamic"

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}"
FILES:${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"
FILES:${PN}-dev += "${PYTHON_SITEPACKAGES_DIR}/*.la"

inherit python3native python3targetconfig
